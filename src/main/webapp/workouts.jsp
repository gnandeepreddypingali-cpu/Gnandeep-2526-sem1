<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login"); return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Workouts — FitLife</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="navbar">
    <a href="${pageContext.request.contextPath}/dashboard" class="brand">💪 FitLife</a>
    <nav>
        <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/workouts" class="active">Workouts</a>
        <a href="${pageContext.request.contextPath}/profile">Profile</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
</div>

<div class="container">
    <div class="page-header">
        <h2>My Workouts</h2>
        <a href="${pageContext.request.contextPath}/workouts?action=add" class="btn btn-primary">+ Add Workout</a>
    </div>

    <!-- Alerts -->
    <c:if test="${param.success == 'added'}">
        <div class="alert alert-success">✅ Workout logged successfully!</div>
    </c:if>
    <c:if test="${param.success == 'updated'}">
        <div class="alert alert-success">✅ Workout updated successfully!</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">🗑️ Workout deleted.</div>
    </c:if>

    <!-- Filter Form -->
    <div class="card" style="margin-bottom:1.25rem">
        <form method="get" action="${pageContext.request.contextPath}/workouts">
            <div class="filter-row">
                <div class="form-group">
                    <label>Activity Type</label>
                    <select name="activityType" class="form-control">
                        <option value="">All Types</option>
                        <option value="Running"     ${param.activityType == 'Running'     ? 'selected' : ''}>Running</option>
                        <option value="Cycling"     ${param.activityType == 'Cycling'     ? 'selected' : ''}>Cycling</option>
                        <option value="Walking"     ${param.activityType == 'Walking'     ? 'selected' : ''}>Walking</option>
                        <option value="Gym Workout" ${param.activityType == 'Gym Workout' ? 'selected' : ''}>Gym Workout</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Min Duration (mins)</label>
                    <input type="number" name="minDuration" class="form-control" min="0" value="${param.minDuration}" placeholder="e.g. 30">
                </div>
                <div class="form-group">
                    <label>From Date</label>
                    <input type="date" name="dateFrom" class="form-control" value="${param.dateFrom}">
                </div>
                <div class="form-group">
                    <label>To Date</label>
                    <input type="date" name="dateTo" class="form-control" value="${param.dateTo}">
                </div>
                <div class="form-group" style="align-self:flex-end">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/workouts" class="btn btn-outline" style="margin-left:.5rem">Clear</a>
                </div>
            </div>
        </form>
    </div>

    <!-- Workout Table -->
    <div class="card">
        <c:choose>
            <c:when test="${empty workouts}">
                <p style="text-align:center;color:#94A3B8;padding:2.5rem 0">
                    No workouts found.
                    <a href="${pageContext.request.contextPath}/workouts?action=add" style="color:#4F46E5">Log one now!</a>
                </p>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Activity</th>
                            <th>Duration</th>
                            <th>Distance</th>
                            <th>Calories</th>
                            <th>AI Prediction</th>
                            <th>Notes</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="w" items="${workouts}">
                            <tr>
                                <td>${w.workoutDate}</td>
                                <td>
                                    <span class="badge
                                        <c:choose>
                                            <c:when test="${w.activityType == 'Running'}">badge-running</c:when>
                                            <c:when test="${w.activityType == 'Cycling'}">badge-cycling</c:when>
                                            <c:when test="${w.activityType == 'Walking'}">badge-walking</c:when>
                                            <c:otherwise>badge-gym</c:otherwise>
                                        </c:choose>
                                    ">${w.activityType}</span>
                                </td>
                                <td>${w.durationMins} min</td>
                                <td>${w.distanceKm} km</td>
                                <td>${w.caloriesBurned} kcal</td>
                                <td>
                                    <c:if test="${not empty w.predictedType}">
                                        <span style="font-size:.82rem;color:#6D28D9">🤖 ${w.predictedType}</span>
                                    </c:if>
                                </td>
                                <td style="max-width:160px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${w.notes}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/workouts?action=edit&id=${w.id}" class="btn btn-outline btn-sm">Edit</a>
                                    <form method="post" action="${pageContext.request.contextPath}/workouts" style="display:inline"
                                          onsubmit="return confirm('Delete this workout?')">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${w.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <p style="color:#94A3B8;font-size:.82rem;margin-top:.75rem">${workouts.size()} workout(s) found</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
