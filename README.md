# FitLife Smart Fitness Tracker

A Java-based web application for tracking fitness workouts with machine learning-powered activity classification. Users can log workouts, track fitness metrics, and receive intelligent predictions on activity types based on their workout characteristics.

## Features

- **User Management**: Secure registration and login with BCrypt password hashing
- **Workout Logging**: Record workouts with activity type, duration, distance, and calories burned
- **Activity Prediction**: ML-powered classification to predict workout types (Running, Cycling, Walking, Gym Workout)
- **Dashboard**: View workout history and fitness statistics
- **User Profiles**: Manage user information including age, weight, and height
- **Sample Data**: Pre-loaded demo account with sample workouts for testing

## Technology Stack

| Component | Technology |
|-----------|-----------|
| **Backend** | Java 11+ (Servlets) |
| **Frontend** | JSP, HTML, CSS, JavaScript |
| **Server** | Apache Tomcat 7 |
| **Database** | MySQL 8.0+ |
| **Build** | Maven 3.9+ |
| **Machine Learning** | WEKA 3.8.6 |
| **Password Security** | BCrypt (jBCrypt 0.4) |
| **JSON Processing** | Gson 2.10.1 |

## Prerequisites

Before running the application, ensure you have:

- **Java Development Kit (JDK)**: Java 11 or later
  - Verify: `java --version`
  
- **Maven**: 3.6 or later
  - Verify: `mvn --version`
  - [Install Maven](https://maven.apache.org/download.cgi) if not installed
  
- **MySQL Server**: 8.0 or later
  - Running on `localhost:3306`
  - [Install MySQL](https://dev.mysql.com/downloads/mysql/)

## Installation

### 1. Clone or Download the Project

```bash
cd FitLifeTracker
```

### 2. Set Up the Database

Create the database and tables by running the SQL script:

```bash
mysql -u root -p < database.sql
```

You'll be prompted for your MySQL password.

**Default credentials in [DBUtil.java](src/main/java/com/fitlife/util/DBUtil.java):**
- User: `root`
- Password: `root`
- Database: `fitlife_db`

**⚠️ Important**: If your MySQL credentials are different, update lines 8-10 in `DBUtil.java`:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/fitlife_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER     = "root";        // Change this
private static final String PASSWORD = "root";        // Change this
```

### 3. Build the Project

```bash
mvn clean install
```

This compiles the code, runs tests, and creates a WAR file in the `target/` directory.

### 4. Run the Application

```bash
mvn tomcat7:run
```

The Tomcat server will start on **port 8081** with the application deployed at `/fitlife`.

### 5. Access the Application

Open your web browser and navigate to:

```
http://localhost:8081/fitlife
```

You'll be redirected to the login page.

## Default Login Credentials

Use the pre-loaded demo account:

- **Username**: `demo`
- **Password**: `password123`

## Project Structure

```
FitLifeTracker/
├── src/
│   ├── main/
│   │   ├── java/com/fitlife/
│   │   │   ├── dao/              # Database Access Objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   └── WorkoutDAO.java
│   │   │   ├── model/            # Data Models
│   │   │   │   ├── User.java
│   │   │   │   └── Workout.java
│   │   │   ├── servlet/          # HTTP Request Handlers
│   │   │   │   ├── LoginServlet.java
│   │   │   │   ├── RegisterServlet.java
│   │   │   │   ├── WorkoutServlet.java
│   │   │   │   ├── PredictServlet.java
│   │   │   │   ├── ProfileServlet.java
│   │   │   │   ├── LogoutServlet.java
│   │   │   ├── ml/               # Machine Learning
│   │   │   │   └── ActivityClassifier.java
│   │   │   └── util/             # Utilities
│   │   │       └── DBUtil.java
│   │   ├── resources/
│   │   │   └── data/
│   │   │       └── workout_data.arff  # WEKA training data
│   │   └── webapp/
│   │       ├── index.jsp              # Home/Login redirect
│   │       ├── login.jsp              # Login page
│   │       ├── register.jsp           # Registration page
│   │       ├── dashboard.jsp          # User dashboard
│   │       ├── workouts.jsp           # Workout list
│   │       ├── add-workout.jsp        # Add new workout
│   │       ├── edit-workout.jsp       # Edit workout
│   │       ├── profile.jsp            # User profile
│   │       ├── error.jsp              # Error page
│   │       ├── css/
│   │       │   └── styles.css         # Application styles
│   │       ├── js/                    # JavaScript files
│   │       └── WEB-INF/
│   │           └── web.xml            # Web application configuration
├── database.sql                        # Database schema and sample data
├── pom.xml                             # Maven configuration
└── README.md                           # This file
```

## Key Features Explained

### User Authentication
- Secure login and registration with BCrypt password hashing
- Session management with 60-minute timeout
- HTTP-only cookies for security

### Workout Tracking
- Log workouts with activity type, duration, distance, and calories
- Activities supported: Running, Cycling, Walking, Gym Workout
- Track workout date and add notes

### Machine Learning Prediction
- WEKA-based classifier trained on `workout_data.arff`
- Predicts activity type based on workout characteristics
- Predictions stored with each workout record

### Database Schema

**Users Table**
- Stores user credentials (with BCrypt hashed passwords)
- Personal information: full name, age, weight, height
- Timestamps for tracking account creation/updates

**Workouts Table**
- Links to user via `user_id`
- Records activity type, duration, distance, calories
- Stores ML prediction results
- Indexes on user_id+date and activity_type for efficient queries

## Configuration

### Session Timeout
Default is 60 minutes. Adjust in [web.xml](src/main/webapp/WEB-INF/web.xml):

```xml
<session-config>
    <session-timeout>60</session-timeout>
</session-config>
```

### Tomcat Port
Default is **8081**. Change in [pom.xml](pom.xml):

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <configuration>
        <port>8081</port>  <!-- Change port here -->
        <path>/fitlife</path>
    </configuration>
</plugin>
```

## Troubleshooting

### Maven Not Found
If you get "mvn: command not found", ensure Maven is installed and added to PATH:

```bash
mvn --version
```

If not installed, download and install Maven 3.9+ from https://maven.apache.org/download.cgi

### MySQL Connection Error
Verify MySQL is running and credentials in [DBUtil.java](src/main/java/com/fitlife/util/DBUtil.java) match your setup:

```bash
mysql -u root -p
```

### Port Already in Use
If port 8081 is already in use, either:
1. Stop the process using that port, or
2. Change the port in `pom.xml`

### WAR File Won't Deploy
Clean and rebuild:

```bash
mvn clean install
mvn tomcat7:run
```

## Build Artifacts

After running `mvn clean install`, the following files are generated:

- **target/FitLifeTracker-1.0-SNAPSHOT.war** - Deployable web archive
- **target/classes/** - Compiled Java classes
- **target/generated-sources/** - Generated code

## Development Notes

- The application uses a simple connection-per-request JDBC pattern
- JSP pages use JSTL for dynamic content
- Database queries use parameterized statements (standard JDBC)
- ML model training data is in ARFF format for WEKA compatibility

## Future Enhancements

- Advanced analytics and progress tracking
- Social features (friend connections, workout sharing)
- Mobile app version
- Real-time workout notifications
- Integration with fitness trackers/wearables

## License

This project is provided as-is for educational purposes.

## Support

For issues or questions, please review the troubleshooting section or check the application logs in:

```
target/tomcat/logs/
```

---

**Ready to run?** Follow the [Installation](#installation) section to get started!
