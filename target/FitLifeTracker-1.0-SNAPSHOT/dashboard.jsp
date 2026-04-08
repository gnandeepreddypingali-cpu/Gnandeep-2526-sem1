<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    com.fitlife.model.User u = (com.fitlife.model.User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard — FitLife</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
    <a href="${pageContext.request.contextPath}/dashboard" class="brand">💪 FitLife</a>
    <nav>
        <a href="${pageContext.request.contextPath}/dashboard" class="active">Dashboard</a>
        <a href="${pageContext.request.contextPath}/workouts">Workouts</a>
        <a href="${pageContext.request.contextPath}/profile">Profile</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
</div>

<div class="container">
    <div class="page-header">
        <div>
            <h2>Welcome back, <%= u.getFullName() != null && !u.getFullName().isEmpty() ? u.getFullName() : u.getUsername() %>! 👋</h2>
            <p style="color:#64748B;font-size:.9rem;margin-top:.25rem">Here's your fitness overview</p>
        </div>
        <a href="${pageContext.request.contextPath}/workouts?action=add" class="btn btn-primary">+ Log Workout</a>
    </div>

    <!-- Stat Cards -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="value">${stats.totalWorkouts}</div>
            <div class="label">Total Workouts</div>
        </div>
        <div class="stat-card">
            <div class="value" style="color:#10B981">${stats.totalCalories}</div>
            <div class="label">Calories Burned</div>
        </div>
        <div class="stat-card">
            <div class="value" style="color:#F59E0B">${stats.totalMinutes}</div>
            <div class="label">Minutes Active</div>
        </div>
        <div class="stat-card">
            <div class="value" style="color:#EF4444"><fmt:formatNumber value="${stats.totalDistance}" maxFractionDigits="1"/></div>
            <div class="label">km Covered</div>
        </div>
        <div class="stat-card">
            <div class="value" style="color:#8B5CF6">${stats.avgCalories}</div>
            <div class="label">Avg Cal/Session</div>
        </div>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.25rem">

        <!-- Activity Breakdown Chart -->
        <div class="card">
            <h3 style="margin-bottom:1rem;font-size:1rem">Activity Breakdown</h3>
            <c:choose>
                <c:when test="${stats.totalWorkouts > 0}">
                    <canvas id="activityChart"></canvas>
                </c:when>
                <c:otherwise>
                    <p style="color:#94A3B8;text-align:center;padding:2rem 0">No workouts logged yet.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Recent Workouts -->
        <div class="card">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem">
                <h3 style="font-size:1rem">Recent Workouts</h3>
                <a href="${pageContext.request.contextPath}/workouts" style="font-size:.85rem;color:#4F46E5">View all →</a>
            </div>
            <c:choose>
                <c:when test="${empty recentWorkouts}">
                    <p style="color:#94A3B8;text-align:center;padding:2rem 0">
                        No workouts yet.<br>
                        <a href="${pageContext.request.contextPath}/workouts?action=add" style="color:#4F46E5">Log your first workout!</a>
                    </p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="w" items="${recentWorkouts}">
                        <div style="display:flex;justify-content:space-between;align-items:center;padding:.65rem 0;border-bottom:1px solid #F1F5F9">
                            <div>
                                <span class="badge
                                    <c:choose>
                                        <c:when test="${w.activityType == 'Running'}">badge-running</c:when>
                                        <c:when test="${w.activityType == 'Cycling'}">badge-cycling</c:when>
                                        <c:when test="${w.activityType == 'Walking'}">badge-walking</c:when>
                                        <c:otherwise>badge-gym</c:otherwise>
                                    </c:choose>
                                ">${w.activityType}</span>
                                <span style="font-size:.82rem;color:#64748B;margin-left:.5rem">${w.workoutDate}</span>
                            </div>
                            <div style="font-size:.85rem;color:#64748B">${w.durationMins} min &bull; ${w.caloriesBurned} kcal</div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- ML Status -->
    <div class="alert alert-info" style="margin-top:1.25rem">
        <c:choose>
            <c:when test="${mlReady}">
                🤖 <strong>AI Activity Predictor is active.</strong> When you log a workout, the system will automatically predict your activity type using machine learning.
            </c:when>
            <c:otherwise>
                ⚠️ ML model is warming up. Predictions will be available shortly.
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
const breakdownData = ${breakdownJson};
if (Object.keys(breakdownData).length > 0) {
    const ctx = document.getElementById('activityChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(breakdownData),
                datasets: [{
                    data: Object.values(breakdownData),
                    backgroundColor: ['#3B82F6','#10B981','#F59E0B','#8B5CF6','#EF4444'],
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'bottom', labels: { font: { size: 12 } } } }
            }
        });
    }
}
</script>
</body>
</html>
