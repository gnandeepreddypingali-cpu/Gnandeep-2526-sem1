package com.fitlife.ml;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Activity classifier using WEKA's J48 (C4.5) Decision Tree algorithm.
 *
 * <p>The model is trained at first use (lazy initialisation) from the bundled
 * ARFF dataset containing 100 workout sessions across four activity types:
 * Running, Cycling, Walking, and Gym Workout.</p>
 *
 * <p>Key design decision: J48 was chosen over Naive Bayes because the dataset
 * has a clear numeric boundary between activities (e.g., distance_km == 0
 * strongly indicates Gym Workout), making a tree-based approach more
 * interpretable and accurate for this domain.</p>
 */
public class ActivityClassifier {

    private static ActivityClassifier instance;
    private Classifier classifier;
    private Instances trainingData;
    private String evaluationSummary;
    private double accuracy;

    /** Singleton access — thread-safe via synchronized. */
    public static synchronized ActivityClassifier getInstance() throws Exception {
        if (instance == null) {
            instance = new ActivityClassifier();
            instance.trainModel();
        }
        return instance;
    }

    private ActivityClassifier() {}

    /**
     * Trains J48 on the bundled ARFF dataset.
     * Performs 10-fold cross-validation to measure model performance.
     */
    private void trainModel() throws Exception {
        // Load ARFF from classpath
        InputStream arffStream = getClass().getClassLoader()
                .getResourceAsStream("data/workout_data.arff");

        if (arffStream == null) {
            throw new RuntimeException("workout_data.arff not found on classpath");
        }

        DataSource source = new DataSource(arffStream);
        trainingData = source.getDataSet();
        trainingData.setClassIndex(trainingData.numAttributes() - 1); // last attr = class

        // Build J48 Decision Tree
        J48 j48 = new J48();
        j48.setOptions(new String[]{
            "-C", "0.25",     // Confidence factor for pruning
            "-M", "2"          // Minimum instances per leaf
        });
        j48.buildClassifier(trainingData);
        this.classifier = j48;

        // 10-fold cross-validation for model evaluation
        Evaluation eval = new Evaluation(trainingData);
        eval.crossValidateModel(classifier, trainingData, 10, new Random(42));

        this.accuracy = eval.pctCorrect();
        this.evaluationSummary = buildEvalSummary(eval);
    }

    /**
     * Predicts the activity type for given workout attributes.
     *
     * @param durationMins   workout duration in minutes
     * @param distanceKm     distance covered in kilometres (0 for gym)
     * @param caloriesBurned calories burned
     * @return predicted activity label (e.g., "Running")
     */
    public String predict(double durationMins, double distanceKm, double caloriesBurned)
            throws Exception {

        // Build an instance using the training data's attribute definitions
        Instance inst = new DenseInstance(3);
        inst.setDataset(trainingData);
        inst.setValue(0, durationMins);
        inst.setValue(1, distanceKm);
        inst.setValue(2, caloriesBurned);

        double classIndex = classifier.classifyInstance(inst);
        String label = trainingData.classAttribute().value((int) classIndex);

        // Convert internal underscore format back to display format
        return label.replace("Gym_Workout", "Gym Workout");
    }

    /**
     * Returns the confidence distribution across all class labels.
     * Each value is a probability in [0, 1].
     */
    public double[] predictDistribution(double durationMins, double distanceKm, double caloriesBurned)
            throws Exception {

        Instance inst = new DenseInstance(3);
        inst.setDataset(trainingData);
        inst.setValue(0, durationMins);
        inst.setValue(1, distanceKm);
        inst.setValue(2, caloriesBurned);

        return classifier.distributionForInstance(inst);
    }

    public String[] getClassLabels() {
        Attribute classAttr = trainingData.classAttribute();
        String[] labels = new String[classAttr.numValues()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = classAttr.value(i).replace("Gym_Workout", "Gym Workout");
        }
        return labels;
    }

    public double getAccuracy()             { return accuracy; }
    public String getEvaluationSummary()    { return evaluationSummary; }

    // ---- Private helpers ----

    private String buildEvalSummary(Evaluation eval) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Accuracy:  %.2f%%\n", eval.pctCorrect()));
        sb.append(String.format("Kappa:     %.4f\n", eval.kappa()));
        sb.append(String.format("MAE:       %.4f\n", eval.meanAbsoluteError()));
        sb.append(String.format("RMSE:      %.4f\n", eval.rootMeanSquaredError()));
        sb.append("\n--- Confusion Matrix ---\n");

        double[][] matrix = eval.confusionMatrix();
        String[] labels = getClassLabels();
        sb.append(String.format("%-14s", ""));
        for (String l : labels) sb.append(String.format("%-14s", l));
        sb.append("\n");

        for (int i = 0; i < matrix.length; i++) {
            sb.append(String.format("%-14s", labels[i]));
            for (double v : matrix[i]) sb.append(String.format("%-14.0f", v));
            sb.append("\n");
        }

        sb.append("\n--- Per-Class Metrics ---\n");
        for (int i = 0; i < labels.length; i++) {
            sb.append(String.format("%-14s Precision=%.2f  Recall=%.2f  F1=%.2f\n",
                labels[i],
                eval.precision(i),
                eval.recall(i),
                eval.fMeasure(i)));
        }
        return sb.toString();
    }
}
