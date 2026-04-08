package com.fitlife.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a single logged workout session.
 */
public class Workout {

    private int    id;
    private int    userId;
    private String activityType;    // Running | Cycling | Walking | Gym Workout
    private int    durationMins;
    private double distanceKm;
    private int    caloriesBurned;
    private Date   workoutDate;
    private String notes;
    private String predictedType;   // WEKA prediction
    private Timestamp createdAt;

    public Workout() {}

    public Workout(int userId, String activityType, int durationMins,
                   double distanceKm, int caloriesBurned, Date workoutDate, String notes) {
        this.userId         = userId;
        this.activityType   = activityType;
        this.durationMins   = durationMins;
        this.distanceKm     = distanceKm;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate    = workoutDate;
        this.notes          = notes;
    }

    // ---- Getters & Setters ----

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }

    public String getActivityType()             { return activityType; }
    public void setActivityType(String at)      { this.activityType = at; }

    public int getDurationMins()                { return durationMins; }
    public void setDurationMins(int d)          { this.durationMins = d; }

    public double getDistanceKm()               { return distanceKm; }
    public void setDistanceKm(double d)         { this.distanceKm = d; }

    public int getCaloriesBurned()              { return caloriesBurned; }
    public void setCaloriesBurned(int c)        { this.caloriesBurned = c; }

    public Date getWorkoutDate()                { return workoutDate; }
    public void setWorkoutDate(Date d)          { this.workoutDate = d; }

    public String getNotes()                    { return notes; }
    public void setNotes(String n)              { this.notes = n; }

    public String getPredictedType()            { return predictedType; }
    public void setPredictedType(String pt)     { this.predictedType = pt; }

    public Timestamp getCreatedAt()             { return createdAt; }
    public void setCreatedAt(Timestamp t)       { this.createdAt = t; }
}
