-- ============================================================
-- FitLife Smart Fitness Tracker - Database Schema
-- MySQL 8.0+
-- Run this script to initialise the database
-- ============================================================

CREATE DATABASE IF NOT EXISTS fitlife_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitlife_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,         -- BCrypt hash
    full_name   VARCHAR(100),
    age         INT,
    weight_kg   DOUBLE,
    height_cm   DOUBLE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Workouts Table
CREATE TABLE IF NOT EXISTS workouts (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NOT NULL,
    activity_type   VARCHAR(50)  NOT NULL,          -- Running, Cycling, Walking, Gym Workout
    duration_mins   INT          NOT NULL,           -- minutes
    distance_km     DOUBLE       DEFAULT 0.0,
    calories_burned INT          NOT NULL,
    workout_date    DATE         NOT NULL,
    notes           TEXT,
    predicted_type  VARCHAR(50),                    -- ML prediction result
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, workout_date),
    INDEX idx_activity  (activity_type)
);

-- Sample Data (Demo User) — password is "password123" BCrypt hash
INSERT IGNORE INTO users (username, email, password, full_name, age, weight_kg, height_cm)
VALUES ('demo', 'demo@fitlife.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVdQ2/2Jae',
        'Demo User', 25, 70.0, 175.0);

-- Sample workouts for demo user
INSERT IGNORE INTO workouts (user_id, activity_type, duration_mins, distance_km, calories_burned, workout_date, notes)
VALUES
(1, 'Running',     30, 5.2,  320, '2025-10-01', 'Morning run'),
(1, 'Cycling',     45, 15.0, 450, '2025-10-03', 'Road cycling'),
(1, 'Walking',     60, 4.0,  200, '2025-10-05', 'Evening walk'),
(1, 'Gym Workout', 50, 0.0,  380, '2025-10-07', 'Strength training'),
(1, 'Running',     20, 3.2,  220, '2025-10-09', 'Interval training'),
(1, 'Cycling',     70, 20.0, 650, '2025-10-11', 'Endurance ride'),
(1, 'Walking',     40, 3.0,  150, '2025-10-13', 'Quick walk'),
(1, 'Gym Workout', 30, 0.0,  250, '2025-10-15', 'Weightlifting');
