package com.fitlife.servlet;

import com.fitlife.dao.UserDAO;
import com.fitlife.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles user registration — GET shows the form, POST processes it.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Redirect to dashboard if already logged in
        if (req.getSession(false) != null && req.getSession().getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username  = req.getParameter("username").trim();
        String email     = req.getParameter("email").trim();
        String password  = req.getParameter("password");
        String confirm   = req.getParameter("confirmPassword");
        String fullName  = req.getParameter("fullName").trim();
        String ageStr    = req.getParameter("age");
        String weightStr = req.getParameter("weight");
        String heightStr = req.getParameter("height");

        // --- Validation ---
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Username, email, and password are required.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        if (!password.equals(confirm)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        if (password.length() < 8) {
            req.setAttribute("error", "Password must be at least 8 characters.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            int age       = ageStr    != null && !ageStr.isEmpty()    ? Integer.parseInt(ageStr)       : 0;
            double weight = weightStr != null && !weightStr.isEmpty() ? Double.parseDouble(weightStr)  : 0.0;
            double height = heightStr != null && !heightStr.isEmpty() ? Double.parseDouble(heightStr)  : 0.0;

            if (age < 0 || weight < 0 || height < 0) {
                req.setAttribute("error", "Age, weight, and height must be non-negative.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            User user = new User(username, email, password, fullName, age, weight, height);
            boolean success = userDAO.register(user);

            if (success) {
                // Auto-login after registration
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("error", "Username or email already in use. Please choose another.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
            }

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid numeric values. Please check age, weight, and height.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Registration failed. Please try again later.");
            getServletContext().log("Registration error", e);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
