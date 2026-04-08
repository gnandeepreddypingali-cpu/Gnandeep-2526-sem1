<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — FitLife</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card" style="max-width:480px">
        <h1>💪 FitLife</h1>
        <p class="subtitle">Create your free account</p>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <div class="form-row">
                <div class="form-group">
                    <label>Username *</label>
                    <input type="text" name="username" class="form-control" placeholder="e.g. jsmith" required>
                </div>
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="fullName" class="form-control" placeholder="John Smith">
                </div>
            </div>
            <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" class="form-control" placeholder="john@example.com" required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Password * (min 8 chars)</label>
                    <input type="password" name="password" class="form-control" minlength="8" required>
                </div>
                <div class="form-group">
                    <label>Confirm Password *</label>
                    <input type="password" name="confirmPassword" class="form-control" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Age</label>
                    <input type="number" name="age" class="form-control" min="0" placeholder="25">
                </div>
                <div class="form-group">
                    <label>Weight (kg)</label>
                    <input type="number" step="0.1" name="weight" class="form-control" min="0" placeholder="70">
                </div>
            </div>
            <div class="form-group">
                <label>Height (cm)</label>
                <input type="number" step="0.1" name="height" class="form-control" min="0" placeholder="175">
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;margin-top:.5rem">Create Account</button>
        </form>

        <p style="text-align:center;margin-top:1.2rem;font-size:.9rem;color:#64748B">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login" style="color:#4F46E5;font-weight:600">Sign In</a>
        </p>
    </div>
</div>
</body>
</html>
