package com.fitlife.servlet;

import com.fitlife.dao.UserDAO;
import com.fitlife.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    private boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;

        User sessionUser = (User) req.getSession().getAttribute("user");
        String action    = req.getParameter("action");

        try {
            if ("updateProfile".equals(action)) {
                sessionUser.setFullName(req.getParameter("fullName").trim());
                sessionUser.setEmail(req.getParameter("email").trim());
                int age       = Integer.parseInt(req.getParameter("age"));
                double weight = Double.parseDouble(req.getParameter("weight"));
                double height = Double.parseDouble(req.getParameter("height"));

                if (age < 0 || weight < 0 || height < 0) {
                    req.setAttribute("error", "Values must be non-negative.");
                    req.getRequestDispatcher("/profile.jsp").forward(req, resp); return;
                }
                sessionUser.setAge(age);
                sessionUser.setWeightKg(weight);
                sessionUser.setHeightCm(height);

                boolean ok = userDAO.updateProfile(sessionUser);
                req.setAttribute(ok ? "success" : "error", ok ? "Profile updated!" : "Update failed.");

            } else if ("changePassword".equals(action)) {
                String oldPass = req.getParameter("oldPassword");
                String newPass = req.getParameter("newPassword");
                String confirm = req.getParameter("confirmNewPassword");

                if (!newPass.equals(confirm)) {
                    req.setAttribute("error", "New passwords do not match.");
                } else if (newPass.length() < 8) {
                    req.setAttribute("error", "Password must be at least 8 characters.");
                } else {
                    boolean ok = userDAO.changePassword(sessionUser.getId(), oldPass, newPass);
                    req.setAttribute(ok ? "success" : "error",
                        ok ? "Password changed successfully." : "Current password is incorrect.");
                }
            }
        } catch (Exception e) {
            req.setAttribute("error", "An error occurred: " + e.getMessage());
        }

        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }
}
