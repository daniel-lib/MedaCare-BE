# 🏥 MedaCare Backend

This is the backend codebase for **MedaCare**, a telemedicine platform built with **Spring Boot**. The project is designed to handle authentication, user management, and other backend services for the MedaCare application.

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/daniel-lib/medaCare-BE.git
cd medaCare-BE
```

### 2. Configure the Application

Update the `src/main/resources/application.properties` file with your database credentials and other environment-specific configurations:

```properties
spring.datasource.url=jdbc:postgresql://<your-database-host>:<port>/<database-name>
spring.datasource.username=<your-database-username>
spring.datasource.password=<your-database-password>
security.jwt.secret-key=<your-jwt-secret-key>
```

### 3. Build and Run the Application

#### Using Maven Wrapper:
```bash
./mvnw spring-boot:run
```
Or, if Maven is installed:
```bash
mvn spring-boot:run
```
## 📁 Project Structure

```text
.
├── src/
│   ├── main/
│   │   ├── java/com/medacare/backend/
│   │   │   ├── config/                # Configuration files (e.g., security, JWT)
│   │   │   ├── controller/            # REST controllers
│   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   ├── model/                 # Entity models
│   │   │   ├── repository/            # Data access layer
│   │   │   ├── service/               # Business logic layer
│   │   │   └── CoreApplication.java   # Main application entry point
│   │   └── resources/
│   │       ├── application.properties # Application configuration
│   └── test/
│       └── java/com/meda_care/core/   # Unit and integration tests
├── pom.xml                            # Maven project configuration
├── mvnw                               # Maven wrapper script 
├── mvnw.cmd                           # Maven wrapper script (Windows)
└── README.md                          # Project overview and setup instructions

```

---

## 🧪 Development Conventions
- Follow feature-based structure: group related controllers, services, and repositories by feature (e.g., authentication, user management).
- Use DTOs for data transfer between layers.
- Keep sensitive information (e.g., database credentials, JWT secret) in environment variables or configuration files.

---

## 🛠 Tech Stack
- **Spring Boot** for backend development
- **PostgreSQL** as the database
- **Spring Security** for authentication and authorization
- **JWT** for stateless authentication
- **Lombok** for reducing boilerplate code
- **Maven** for dependency management and build automation

---

## ✅ Future Plans
<input disabled="" type="checkbox"> Add more endpoints for managing physicians, patients, and appointments.
<input disabled="" type="checkbox"> Implement advanced logging and monitoring.
<input disabled="" type="checkbox"> Add unit and integration tests for all services and controllers.
<input disabled="" type="checkbox"> Integrate with the frontend APIs.

## 🤝 Contributing
1. Create a new branch from main.
2. Follow the feature-based folder structure.
3. Open a PR with a clear description and screenshots (if applicable).