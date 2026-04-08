package com.fitlife.dao;

import com.fitlife.model.User;
import com.fitlife.util.DBUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * Data Access Object for User entity.
 * Handles all user-related database operations.
 */
public class UserDAO {

    /**
     * Registers a new user. Password is BCrypt-hashed before persisting.
     * @return true if registration succeeded, false if username/email is duplicate.
     */
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, full_name, age, weight_kg, height_cm) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashed);
            ps.setString(4, user.getFullName());
            ps.setInt(5, user.getAge());
            ps.setDouble(6, user.getWeightKg());
            ps.setDouble(7, user.getHeightCm());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) user.setId(rs.getInt(1));
                return true;
            }
            return false;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // Duplicate username or email
        }
    }

    /**
     * Authenticates a user by username and plain-text password.
     * @return the User object if credentials are valid, null otherwise.
     */
    public User login(String username, String plainPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(plainPassword, storedHash)) {
                    return mapRow(rs);
                }
            }
            return null;
        }
    }

    /**
     * Retrieves a user by ID.
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    /**
     * Updates profile fields for an existing user.
     */
    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET full_name=?, age=?, weight_kg=?, height_cm=?, email=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setInt(2, user.getAge());
            ps.setDouble(3, user.getWeightKg());
            ps.setDouble(4, user.getHeightCm());
            ps.setString(5, user.getEmail());
            ps.setInt(6, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Changes a user's password after verifying the current one.
     */
    public boolean changePassword(int userId, String oldPlain, String newPlain) throws SQLException {
        String selectSql = "SELECT password FROM users WHERE id=?";
        String updateSql = "UPDATE users SET password=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection()) {
            // Verify old password
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return false;
                if (!BCrypt.checkpw(oldPlain, rs.getString("password"))) return false;
            }
            // Update to new hash
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, BCrypt.hashpw(newPlain, BCrypt.gensalt(10)));
                ps.setInt(2, userId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    // ---- Helper ----

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setAge(rs.getInt("age"));
        u.setWeightKg(rs.getDouble("weight_kg"));
        u.setHeightCm(rs.getDouble("height_cm"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
