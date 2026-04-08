<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login"); return;
    }
    com.fitlife.model.User u = (com.fitlife.model.User) session.getAttribute("user");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Profile — FitLife</title>
    <link rel="stylesheet" href="<%= ctx %>/css/styles.css">
</head>
<body>
<div class="navbar">
    <a href="<%= ctx %>/dashboard" class="brand">💪 FitLife</a>
    <nav>
        <a href="<%= ctx %>/dashboard">Dashboard</a>
        <a href="<%= ctx %>/workouts">Workouts</a>
        <a href="<%= ctx %>/profile" class="active">Profile</a>
        <a href="<%= ctx %>/logout">Logout</a>
    </nav>
</div>

<div class="container" style="max-width:680px">
    <h2 style="margin-bottom:1.25rem">My Profile</h2>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success">${success}</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
    <% } %>

    <!-- BMI Card -->
    <div class="stats-grid" style="margin-bottom:1.25rem">
        <div class="stat-card">
            <div class="value"><%= u.getUsername() %></div>
            <div class="label">Username</div>
        </div>
        <div class="stat-card">
            <div class="value"><%= u.getWeightKg() %> kg</div>
            <div class="label">Weight</div>
        </div>
        <div class="stat-card">
            <div class="value"><%= u.getHeightCm() %> cm</div>
            <div class="label">Height</div>
        </div>
        <div class="stat-card">
            <div class="value"><%= u.getBmi() %></div>
            <div class="label">BMI</div>
        </div>
    </div>

    <!-- Update Profile -->
    <div class="card" style="margin-bottom:1.25rem">
        <h3 style="margin-bottom:1rem">Update Profile</h3>
        <form method="post" action="<%= ctx %>/profile">
            <input type="hidden" name="action" value="updateProfile">
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="fullName" class="form-control" value="<%= u.getFullName() != null ? u.getFullName() : "" %>">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" class="form-control" value="<%= u.getEmail() %>" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Age</label>
                    <input type="number" name="age" class="form-control" min="0" value="<%= u.getAge() %>">
                </div>
                <div class="form-group">
                    <label>Weight (kg)</label>
                    <input type="number" step="0.1" name="weight" class="form-control" min="0" value="<%= u.getWeightKg() %>">
                </div>
            </div>
            <div class="form-group">
                <label>Height (cm)</label>
                <input type="number" step="0.1" name="height" class="form-control" min="0" value="<%= u.getHeightCm() %>">
            </div>
            <button type="submit" class="btn btn-primary">Update Profile</button>
        </form>
    </div>

    <!-- Change Password -->
    <div class="card">
        <h3 style="margin-bottom:1rem">Change Password</h3>
        <form method="post" action="<%= ctx %>/profile">
            <input type="hidden" name="action" value="changePassword">
            <div class="form-group">
                <label>Current Password</label>
                <input type="password" name="oldPassword" class="form-control" required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>New Password</label>
                    <input type="password" name="newPassword" class="form-control" minlength="8" required>
                </div>
                <div class="form-group">
                    <label>Confirm New Password</label>
                    <input type="password" name="confirmNewPassword" class="form-control" required>
                </div>
            </div>
            <button type="submit" class="btn btn-danger">Change Password</button>
        </form>
    </div>
</div>
</body>
</html>
