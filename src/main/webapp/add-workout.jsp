<%@ page contentType="text/html;charset=UTF-8" %>
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Workout — FitLife</title>
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
        <h2>Log New Workout</h2>
        <a href="<%= ctx %>/workouts" class="btn btn-outline">← Back</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error">${error}</div>
    <% } %>

    <div class="card">
        <form method="post" action="<%= ctx %>/workouts" id="workoutForm">
            <input type="hidden" name="action" value="save">

            <div class="form-row">
                <div class="form-group">
                    <label>Activity Type *</label>
                    <select name="activityType" class="form-control" required>
                        <option value="">-- Select --</option>
                        <option value="Running">🏃 Running</option>
                        <option value="Cycling">🚴 Cycling</option>
                        <option value="Walking">🚶 Walking</option>
                        <option value="Gym Workout">🏋️ Gym Workout</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Workout Date *</label>
                    <input type="date" name="workoutDate" class="form-control" required
                           value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Duration (mins) *</label>
                    <input type="number" name="durationMins" id="duration" class="form-control"
                           min="1" max="600" placeholder="e.g. 30" required onchange="triggerPredict()">
                </div>
                <div class="form-group">
                    <label>Distance (km) <span style="color:#94A3B8;font-size:.8rem">0 for gym</span></label>
                    <input type="number" name="distanceKm" id="distance" class="form-control"
                           min="0" step="0.1" placeholder="e.g. 5.2" value="0" onchange="triggerPredict()">
                </div>
            </div>

            <div class="form-group">
                <label>Calories Burned *</label>
                <input type="number" name="caloriesBurned" id="calories" class="form-control"
                       min="0" placeholder="e.g. 320" required onchange="triggerPredict()">
            </div>

            <div class="form-group">
                <label>Notes</label>
                <textarea name="notes" class="form-control" rows="3" placeholder="Optional: describe your workout"></textarea>
            </div>

            <!-- AI Prediction Box -->
            <div id="predictionBox" style="display:none;margin-top:1rem;padding:1rem;background:#F3E8FF;border-radius:8px;border:1px solid #E9D5FF">
                <p style="font-size:.82rem;color:#5B21B6;margin-bottom:.5rem">🤖 AI Activity Prediction</p>
                <div class="pred-label" id="predLabel">—</div>
                <div id="predBars" style="margin-top:.75rem"></div>
                <p style="font-size:.75rem;color:#7C3AED;margin-top:.5rem">
                    Model accuracy: <span id="predAccuracy">—</span>%
                </p>
            </div>

            <div style="display:flex;gap:.75rem;margin-top:1.25rem">
                <button type="submit" class="btn btn-primary">💾 Save Workout</button>
                <a href="<%= ctx %>/workouts" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
const ctxPath = '<%= ctx %>';
let debounceTimer;

function triggerPredict() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(runPredict, 800);
}

async function runPredict() {
    const duration = document.getElementById('duration').value;
    const distance = document.getElementById('distance').value || 0;
    const calories = document.getElementById('calories').value;

    if (!duration || !calories) return;

    try {
        const url = ctxPath + '/predict?duration=' + duration + '&distance=' + distance + '&calories=' + calories;
        const res = await fetch(url);
        const data = await res.json();

        if (data.error) return;

        const box = document.getElementById('predictionBox');
        box.style.display = 'block';
        document.getElementById('predLabel').textContent = data.prediction || 'Unknown';
        document.getElementById('predAccuracy').textContent = data.accuracy || '—';

        const barsDiv = document.getElementById('predBars');
        barsDiv.innerHTML = '';
        
        if (data.distribution) {
            for (const label in data.distribution) {
                const pct = data.distribution[label];
                const barRow = document.createElement('div');
                barRow.className = 'pred-bar-row';
                barRow.innerHTML = '<span style="width:90px;flex-shrink:0">' + label + '</span>' +
                    '<div class="pred-bar-wrap"><div class="pred-bar" style="width:' + pct + '%"></div></div>' +
                    '<span style="width:40px;text-align:right">' + pct + '%</span>';
                barsDiv.appendChild(barRow);
            }
        }
    } catch (e) {
        console.log('Prediction error (non-blocking):', e);
    }
}
</script>

</body>
</html>
