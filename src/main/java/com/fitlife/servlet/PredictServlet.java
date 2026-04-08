package com.fitlife.servlet;

import com.fitlife.ml.ActivityClassifier;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * REST-style endpoint for live WEKA predictions.
 * Called via AJAX from the add/edit workout form to give real-time suggestions.
 *
 * GET /predict?duration=30&distance=5.2&calories=320
 * Returns JSON: { "prediction": "Running", "accuracy": 98.0, "distribution": {...} }
 */
@WebServlet("/predict")
public class PredictServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Auth guard
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        try {
            double duration = Double.parseDouble(req.getParameter("duration"));
            double distance = Double.parseDouble(req.getParameter("distance"));
            double calories = Double.parseDouble(req.getParameter("calories"));

            if (duration <= 0 || calories < 0 || distance < 0) {
                json.addProperty("error", "Invalid input values");
                out.print(json);
                return;
            }

            ActivityClassifier clf = ActivityClassifier.getInstance();
            String prediction      = clf.predict(duration, distance, calories);
            double[] dist          = clf.predictDistribution(duration, distance, calories);
            String[] labels        = clf.getClassLabels();

            json.addProperty("prediction", prediction);
            json.addProperty("accuracy",   Math.round(clf.getAccuracy() * 100.0) / 100.0);

            JsonObject distObj = new JsonObject();
            for (int i = 0; i < labels.length; i++) {
                distObj.addProperty(labels[i], Math.round(dist[i] * 1000.0) / 10.0); // as %
            }
            json.add("distribution", distObj);

        } catch (NumberFormatException e) {
            json.addProperty("error", "Please enter valid numbers for all fields.");
        } catch (Exception e) {
            json.addProperty("error", "Prediction service unavailable: " + e.getMessage());
            getServletContext().log("PredictServlet error", e);
        }

        out.print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
