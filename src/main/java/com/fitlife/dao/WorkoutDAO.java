package com.fitlife.dao;

import com.fitlife.model.Workout;
import com.fitlife.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Workout entity.
 * Handles CRUD operations, search/filter, and summary statistics.
 */
public class WorkoutDAO {

    // ---- CREATE ----

    public boolean addWorkout(Workout w) throws SQLException {
        String sql = "INSERT INTO workouts (user_id, activity_type, duration_mins, distance_km, " +
                     "calories_burned, workout_date, notes, predicted_type) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, w.getUserId());
            ps.setString(2, w.getActivityType());
            ps.setInt(3, w.getDurationMins());
            ps.setDouble(4, w.getDistanceKm());
            ps.setInt(5, w.getCaloriesBurned());
            ps.setDate(6, w.getWorkoutDate());
            ps.setString(7, w.getNotes());
            ps.setString(8, w.getPredictedType());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) w.setId(rs.getInt(1));
                return true;
            }
            return false;
        }
    }

    // ---- READ (single) ----

    public Workout findById(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM workouts WHERE id=? AND user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    // ---- READ (all for user) ----

    public List<Workout> getWorkouts(int userId) throws SQLException {
        return searchAndFilter(userId, null, null, null, null);
    }

    // ---- SEARCH & FILTER ----

    /**
     * Flexible query — all parameters are optional (pass null to skip).
     * @param activityType  filter by activity type
     * @param minDuration   minimum duration in minutes
     * @param dateFrom      inclusive start date (yyyy-MM-dd)
     * @param dateTo        inclusive end date (yyyy-MM-dd)
     */
    public List<Workout> searchAndFilter(int userId, String activityType,
                                         Integer minDuration, String dateFrom, String dateTo)
            throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT * FROM workouts WHERE user_id=?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (activityType != null && !activityType.isEmpty()) {
            sql.append(" AND activity_type=?");
            params.add(activityType);
        }
        if (minDuration != null && minDuration > 0) {
            sql.append(" AND duration_mins >= ?");
            params.add(minDuration);
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append(" AND workout_date >= ?");
            params.add(java.sql.Date.valueOf(dateFrom));
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            sql.append(" AND workout_date <= ?");
            params.add(java.sql.Date.valueOf(dateTo));
        }
        sql.append(" ORDER BY workout_date DESC, id DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            List<Workout> list = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    // ---- UPDATE ----

    public boolean updateWorkout(Workout w) throws SQLException {
        String sql = "UPDATE workouts SET activity_type=?, duration_mins=?, distance_km=?, " +
                     "calories_burned=?, workout_date=?, notes=?, predicted_type=? " +
                     "WHERE id=? AND user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getActivityType());
            ps.setInt(2, w.getDurationMins());
            ps.setDouble(3, w.getDistanceKm());
            ps.setInt(4, w.getCaloriesBurned());
            ps.setDate(5, w.getWorkoutDate());
            ps.setString(6, w.getNotes());
            ps.setString(7, w.getPredictedType());
            ps.setInt(8, w.getId());
            ps.setInt(9, w.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    // ---- DELETE ----

    public boolean deleteWorkout(int id, int userId) throws SQLException {
        String sql = "DELETE FROM workouts WHERE id=? AND user_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---- STATISTICS ----

    /**
     * Returns a summary map for the dashboard:
     *   totalWorkouts, totalCalories, totalMinutes, totalDistance, avgCalories,
     *   and breakdown counts per activity type.
     */
    public Map<String, Object> getSummaryStats(int userId) throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) AS total_workouts, " +
                     "SUM(calories_burned) AS total_calories, " +
                     "SUM(duration_mins) AS total_minutes, " +
                     "SUM(distance_km) AS total_distance, " +
                     "AVG(calories_burned) AS avg_calories " +
                     "FROM workouts WHERE user_id=?";

        String breakdownSql = "SELECT activity_type, COUNT(*) AS cnt FROM workouts " +
                              "WHERE user_id=? GROUP BY activity_type";

        Map<String, Object> stats = new LinkedHashMap<>();

        try (Connection conn = DBUtil.getConnection()) {
            // Main stats
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    stats.put("totalWorkouts",  rs.getInt("total_workouts"));
                    stats.put("totalCalories",  rs.getInt("total_calories"));
                    stats.put("totalMinutes",   rs.getInt("total_minutes"));
                    stats.put("totalDistance",  rs.getDouble("total_distance"));
                    stats.put("avgCalories",    (int) rs.getDouble("avg_calories"));
                }
            }

            // Activity breakdown
            Map<String, Integer> breakdown = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(breakdownSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    breakdown.put(rs.getString("activity_type"), rs.getInt("cnt"));
                }
            }
            stats.put("breakdown", breakdown);
        }

        return stats;
    }

    // ---- Helper ----

    private Workout mapRow(ResultSet rs) throws SQLException {
        Workout w = new Workout();
        w.setId(rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));
        w.setActivityType(rs.getString("activity_type"));
        w.setDurationMins(rs.getInt("duration_mins"));
        w.setDistanceKm(rs.getDouble("distance_km"));
        w.setCaloriesBurned(rs.getInt("calories_burned"));
        w.setWorkoutDate(rs.getDate("workout_date"));
        w.setNotes(rs.getString("notes"));
        w.setPredictedType(rs.getString("predicted_type"));
        w.setCreatedAt(rs.getTimestamp("created_at"));
        return w;
    }
}
