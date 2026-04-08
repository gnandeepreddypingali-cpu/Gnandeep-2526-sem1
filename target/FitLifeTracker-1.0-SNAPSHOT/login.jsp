<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — FitLife</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card">
        <h1>💪 FitLife</h1>
        <p class="subtitle">Sign in to your fitness dashboard</p>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
        <div class="alert alert-success">Registration successful! Please log in.</div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" class="form-control"
                       placeholder="Enter username" required autofocus>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter password" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;margin-top:.5rem">Sign In</button>
        </form>

        <p style="text-align:center;margin-top:1.2rem;font-size:.9rem;color:#64748B">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/register" style="color:#4F46E5;font-weight:600">Register</a>
        </p>
        <p style="text-align:center;margin-top:.5rem;font-size:.82rem;color:#94A3B8">
            Demo: <strong>demo</strong> / <strong>password123</strong>
        </p>
    </div>
</div>
</body>
</html>
