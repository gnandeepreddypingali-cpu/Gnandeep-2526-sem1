<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><title>Error — FitLife</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css"></head>
<body>
<div class="auth-wrapper">
    <div class="auth-card" style="text-align:center">
        <h1 style="font-size:3rem">⚠️</h1>
        <h2 style="margin:.5rem 0">Something went wrong</h2>
        <p style="color:#64748B;margin-bottom:1.5rem">An unexpected error occurred. Please try again.</p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
    </div>
</div>
</body>
</html>
