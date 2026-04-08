<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login"); return;
    }
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Workout — FitLife</title>
    <link rel="stylesheet" href="<%= ctx %>/css/styles.css">
</head>
<body>
<div class="navbar">
    <a href="<%= ctx %>/dashboard" class="brand">💪 FitLife</a>
    <nav>
        <a href="<%= ctx %>/dashboard">Dashboard</a>
        <a href="<%= ctx %>/workouts" class="active">Workouts</a>
        <a href="<%= ctx %>/profile">Profile</a>
        <a href="<%= ctx %>/logout">Logout</a>
    </nav>
</div>

<div class="container" style="max-width:650px">
    <div class="page-header">
        <h2>Edit Workout</h2>
        <a href="<%= ctx %>/workouts" class="btn btn-outline">← Back</a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="card">
        <form method="post" action="<%= ctx %>/workouts">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${workout.id}">

            <div class="form-row">
                <div class="form-group">
                    <label>Activity Type *</label>
                    <select name="activityType" class="form-control" required>
                        <option value="Running"     ${workout.activityType == 'Running'     ? 'selected' : ''}>🏃 Running</option>
                        <option value="Cycling"     ${workout.activityType == 'Cycling'     ? 'selected' : ''}>🚴 Cycling</option>
                        <option value="Walking"     ${workout.activityType == 'Walking'     ? 'selected' : ''}>🚶 Walking</option>
                        <option value="Gym Workout" ${workout.activityType == 'Gym Workout' ? 'selected' : ''}>🏋️ Gym Workout</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Workout Date *</label>
                    <input type="date" name="workoutDate" class="form-control"
                           value="${workout.workoutDate}" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Duration (mins) *</label>
                    <input type="number" name="durationMins" class="form-control"
                           min="1" value="${workout.durationMins}" required oninput="triggerPredict()">
                </div>
                <div class="form-group">
                    <label>Distance (km)</label>
                    <input type="number" name="distanceKm" id="distance" class="form-control"
                           min="0" step="0.1" value="${workout.distanceKm}" oninput="triggerPredict()">
                </div>
            </div>

            <div class="form-group">
                <label>Calories Burned *</label>
                <input type="number" name="caloriesBurned" id="calories" class="form-control"
                       min="0" value="${workout.caloriesBurned}" required oninput="triggerPredict()">
            </div>

            <div class="form-group">
                <label>Notes</label>
                <textarea name="notes" class="form-control" rows="3">${workout.notes}</textarea>
            </div>

            <c:if test="${not empty workout.predictedType}">
                <div class="alert alert-info" style="margin-bottom:1rem">
                    🤖 Previous AI prediction: <strong>${workout.predictedType}</strong>
                </div>
            </c:if>

            <div style="display:flex;gap:.75rem">
                <button type="submit" class="btn btn-primary">💾 Update Workout</button>
                <a href="<%= ctx %>/workouts" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>
<script>
const ctxPath = '<%= ctx %>';
let t;
function triggerPredict() { clearTimeout(t); t = setTimeout(runPredict, 700); }
async function runPredict() {
    const dur = document.querySelector('[name=durationMins]').value;
    const dis = document.getElementById('distance').value;
    const cal = document.getElementById('calories').value;
    if (!dur || !cal) return;
    try {
        const url = ctxPath + '/predict?duration=' + dur + '&distance=' + (dis||0) + '&calories=' + cal;
        const r = await fetch(url);
        const d = await r.json();
        if (!d.error) console.log('Predicted:', d.prediction);
    } catch(e) {}
}
</script>
</body>
</html>
