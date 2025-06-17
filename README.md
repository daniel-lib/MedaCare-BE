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
---

## 📁 Project Structure

```
MedaCare-BE/
├── .gitignore                  # Git ignore rules
├── .gitattributes              # Git attributes for line endings
├── Dockerfile                  # Docker build instructions
├── mvnw, mvnw.cmd              # Maven wrapper scripts (Linux/Windows)
├── pom.xml                     # Maven project configuration
├── README.md                   # Project documentation
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── medacare/
        │           └── backend/
        │               ├── config/                # Application and security configuration classes
        │               ├── controller/            # REST API controllers (handle HTTP requests)
        │               ├── dto/                   # Data Transfer Objects for API/service communication
        │               ├── initialization/        # Data seeding and initialization logic
        │               ├── model/                 # Domain models and JPA entities
        │               │   ├── helper/            # Helper or utility models (e.g., InstitutionFile)
        │               │   └── appointmentBooking/# Appointment-related models
        │               ├── repository/            # Spring Data JPA repositories (data access layer)
        │               │   └── appointment/       # Appointment-related repositories
        │               ├── service/               # Business logic and service classes
        │               └── CoreApplication.java   # Main Spring Boot application entry point
        └── resources/
            ├── application.properties            # Main application configuration
            └── application-dev.properties        # (gitignored) Development config
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