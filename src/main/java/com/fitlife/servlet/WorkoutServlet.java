package com.fitlife.servlet;

import com.fitlife.dao.WorkoutDAO;
import com.fitlife.ml.ActivityClassifier;
import com.fitlife.model.User;
import com.fitlife.model.Workout;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Central controller for all workout operations and the dashboard.
 * Dispatches by "action" request parameter:
 *   - (none/list) : workout list with optional filter
 *   - add         : show add form
 *   - save        : process add form (POST)
 *   - edit        : show edit form
 *   - update      : process edit form (POST)
 *   - delete      : delete workout (POST)
 *   - dashboard   : dashboard with stats
 */
@WebServlet(urlPatterns = {"/workouts", "/dashboard"})
public class WorkoutServlet extends HttpServlet {

    private final WorkoutDAO workoutDAO = new WorkoutDAO();
    private final Gson gson = new Gson();

    // ---- Auth guard ----

    private boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    private User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    // ===================== GET =====================

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;

        String path   = req.getServletPath();
        String action = req.getParameter("action");

        try {
            if ("/dashboard".equals(path)) {
                showDashboard(req, resp);
            } else if ("add".equals(action)) {
                req.getRequestDispatcher("/add-workout.jsp").forward(req, resp);
            } else if ("edit".equals(action)) {
                showEditForm(req, resp);
            } else {
                showWorkoutList(req, resp);
            }
        } catch (Exception e) {
            getServletContext().log("WorkoutServlet GET error", e);
            req.setAttribute("error", "An error occurred. Please try again.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    // ===================== POST =====================

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;

        String action = req.getParameter("action");
        try {
            switch (action == null ? "" : action) {
                case "save":   saveWorkout(req, resp);   break;
                case "update": updateWorkout(req, resp); break;
                case "delete": deleteWorkout(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/workouts");
            }
        } catch (Exception e) {
            getServletContext().log("WorkoutServlet POST error", e);
            req.setAttribute("error", "Operation failed: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    // ===================== Handlers =====================

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int userId = currentUser(req).getId();
        Map<String, Object> stats = workoutDAO.getSummaryStats(userId);
        List<Workout> recent = workoutDAO.getWorkouts(userId);

        // Limit to 5 most recent for dashboard
        if (recent.size() > 5) recent = recent.subList(0, 5);

        // Prepare chart JSON
        Map<?, ?> breakdown = (Map<?, ?>) stats.get("breakdown");
        req.setAttribute("breakdownJson", gson.toJson(breakdown));
        req.setAttribute("stats", stats);
        req.setAttribute("recentWorkouts", recent);

        // Warm up ML model
        try {
            ActivityClassifier.getInstance();
            req.setAttribute("mlReady", true);
        } catch (Exception e) {
            req.setAttribute("mlReady", false);
        }

        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }

    private void showWorkoutList(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int userId = currentUser(req).getId();

        String activityFilter = req.getParameter("activityType");
        String minDurStr      = req.getParameter("minDuration");
        String dateFrom       = req.getParameter("dateFrom");
        String dateTo         = req.getParameter("dateTo");

        Integer minDuration = null;
        if (minDurStr != null && !minDurStr.isEmpty()) {
            try { minDuration = Integer.parseInt(minDurStr); } catch (NumberFormatException ignored) {}
        }

        List<Workout> workouts = workoutDAO.searchAndFilter(userId, activityFilter, minDuration, dateFrom, dateTo);
        req.setAttribute("workouts", workouts);
        req.getRequestDispatcher("/workouts.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int id     = Integer.parseInt(req.getParameter("id"));
        int userId = currentUser(req).getId();
        Workout w  = workoutDAO.findById(id, userId);

        if (w == null) {
            resp.sendRedirect(req.getContextPath() + "/workouts");
            return;
        }
        req.setAttribute("workout", w);
        req.getRequestDispatcher("/edit-workout.jsp").forward(req, resp);
    }

    private void saveWorkout(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        Workout w = parseWorkoutForm(req, currentUser(req).getId());

        // Validate
        if (w == null) {
            req.setAttribute("error", "Invalid workout data. Please check your inputs.");
            req.getRequestDispatcher("/add-workout.jsp").forward(req, resp);
            return;
        }

        // Run ML prediction
        try {
            ActivityClassifier clf = ActivityClassifier.getInstance();
            String predicted = clf.predict(w.getDurationMins(), w.getDistanceKm(), w.getCaloriesBurned());
            w.setPredictedType(predicted);
        } catch (Exception e) {
            getServletContext().log("ML prediction skipped", e);
        }

        workoutDAO.addWorkout(w);
        resp.sendRedirect(req.getContextPath() + "/workouts?success=added");
    }

    private void updateWorkout(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int userId = currentUser(req).getId();
        Workout w  = parseWorkoutForm(req, userId);

        if (w == null) {
            req.setAttribute("error", "Invalid data.");
            req.getRequestDispatcher("/edit-workout.jsp").forward(req, resp);
            return;
        }

        String idStr = req.getParameter("id");
        w.setId(Integer.parseInt(idStr));

        // Re-run prediction on update
        try {
            ActivityClassifier clf = ActivityClassifier.getInstance();
            w.setPredictedType(clf.predict(w.getDurationMins(), w.getDistanceKm(), w.getCaloriesBurned()));
        } catch (Exception ignored) {}

        workoutDAO.updateWorkout(w);
        resp.sendRedirect(req.getContextPath() + "/workouts?success=updated");
    }

    private void deleteWorkout(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int id     = Integer.parseInt(req.getParameter("id"));
        int userId = currentUser(req).getId();
        workoutDAO.deleteWorkout(id, userId);
        resp.sendRedirect(req.getContextPath() + "/workouts?success=deleted");
    }

    // ===================== Helpers =====================

    /**
     * Parses and validates the workout form.  Returns null if validation fails.
     */
    private Workout parseWorkoutForm(HttpServletRequest req, int userId) {
        try {
            String activityType   = req.getParameter("activityType");
            String durationStr    = req.getParameter("durationMins");
            String distanceStr    = req.getParameter("distanceKm");
            String caloriesStr    = req.getParameter("caloriesBurned");
            String dateStr        = req.getParameter("workoutDate");
            String notes          = req.getParameter("notes");

            // Required fields
            if (activityType == null || activityType.isEmpty() ||
                durationStr == null || durationStr.isEmpty() ||
                caloriesStr == null || caloriesStr.isEmpty() ||
                dateStr == null || dateStr.isEmpty()) {
                return null;
            }

            int    duration = Integer.parseInt(durationStr);
            double distance = (distanceStr == null || distanceStr.isEmpty()) ? 0.0 : Double.parseDouble(distanceStr);
            int    calories = Integer.parseInt(caloriesStr);

            // Non-negative validation
            if (duration <= 0 || calories < 0 || distance < 0) return null;

            Date date = Date.valueOf(dateStr); // throws if invalid format

            Workout w = new Workout(userId, activityType, duration, distance, calories, date,
                                    notes != null ? notes.trim() : "");
            return w;

        } catch (Exception e) {
            return null;
        }
    }
}
