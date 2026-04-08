package com.fitlife.model;

import java.sql.Timestamp;

/**
 * Represents an application user.
 */
public class User {

    private int id;
    private String username;
    private String email;
    private String password;      // Stored as BCrypt hash — never plain text
    private String fullName;
    private int    age;
    private double weightKg;
    private double heightCm;
    private Timestamp createdAt;

    public User() {}

    public User(String username, String email, String password, String fullName, int age, double weightKg, double heightCm) {
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.fullName  = fullName;
        this.age       = age;
        this.weightKg  = weightKg;
        this.heightCm  = heightCm;
    }

    // ---- Getters & Setters ----

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getFullName()             { return fullName; }
    public void setFullName(String fn)      { this.fullName = fn; }

    public int getAge()                     { return age; }
    public void setAge(int a)               { this.age = a; }

    public double getWeightKg()             { return weightKg; }
    public void setWeightKg(double w)       { this.weightKg = w; }

    public double getHeightCm()             { return heightCm; }
    public void setHeightCm(double h)       { this.heightCm = h; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp t)   { this.createdAt = t; }

    /** Calculates BMI from stored height/weight. */
    public double getBmi() {
        if (heightCm <= 0) return 0;
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
    }
}
