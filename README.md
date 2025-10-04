[English](./README.md) | [한국어](./README.ko.md) | [日本語](./README.ja.md)

---

# 📚 JLPT Question Generation Learning Helper - Backend

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/user/repo/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](#-tech-stack)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen.svg)](#-tech-stack)

This is the backend server for the "JLPT Question Generation Learning Helper" web application. It handles core business logic, including user management, authentication, JLPT question generation, record management, and integration with AI services.

## ✨ Features

- **🔐 User Authentication**: Secure login and registration functionality based on JWT (JSON Web Token).
- **🤖 AI Question Generation**: Dynamically generates JLPT practice questions by integrating with a generative AI model.
- **📖 Question Management**: Provides RESTful APIs for creating, reading, updating, and deleting generated questions.
- **📈 Learning Record Management**: Tracks and manages users' problem-solving history and accuracy rates.
- **🗣️ AI Q&A**: Forwards user questions to the AI model and retrieves answers.
- **👑 Admin Functionality**: A Thymeleaf-based admin page for managing users, questions, and learning records.

## 🛠️ Tech Stack

| Category | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.4.7 |
| **Build Tool** | Gradle | |
| **Database** | MariaDB (Production), H2 (Test) | |
| **DB Migration** | Flyway | |
| **Auth/Security**| Spring Security, JJWT | 0.11.5 |
| **ORM** | Spring Data JPA | |
| **Template Engine**| Thymeleaf | |
| **Others** | Lombok, Actuator, Testcontainers | |

## 🏛️ Project Structure

```
src/main/java/com/rookies3/genaiquestionapp
├── GenAiQuestionAppApplication.java  # Spring Boot Main Application
├── admin/                  # Controllers and services for admin features
├── ai/                     # Logic for integrating with external AI services
├── auth/                   # User authentication (signup, login)
├── config/                 # Global configurations (Security, CORS)
├── entity/                 # JPA entities (database table mappings)
├── exception/              # Global exception handling
├── jwt/                    # Utilities for JWT creation and validation
├── problem/                # Creating, reading, and managing questions
├── record/                 # Managing user learning records
├── userquestion/           # Handling user questions
└── util/                   # Common utility classes
```

## 🚀 Getting Started

### 1. Prerequisites

- Java 17
- Gradle
- Docker (Optional)

### 2. Installation & Configuration

1.  **Clone the repository**
    ```bash
    git clone https://github.com/your-username/jlpt-backend.git
    cd jlpt-backend
    ```

2.  **Application Configuration (Best Practice)**
    Create an `application-prod.yml` file in the `src/main/resources/` directory and modify the contents below to fit your production environment. For local development, create and use `application-dev.yml`.

    ```yaml
    # src/main/resources/application-prod.yml
    spring:
      datasource:
        url: jdbc:mariadb://localhost:3306/your_db_name
        username: your_db_user
        password: your_db_password
        driver-class-name: org.mariadb.jdbc.Driver
      jpa:
        hibernate:
          ddl-auto: validate # Set to validate as we are using Flyway
      security:
        oauth2:
          resourceserver:
            jwt:
              # Use a very strong, random string of at least 64 characters.
              secret-key: '''your_super_strong_and_long_jwt_secret_key_here'''
    ```

### 3. Running Locally

- **Run with `dev` profile (uses H2 in-memory DB)**
  ```bash
  ./gradlew bootRun --args=\'--spring.profiles.active=dev\'
  ```

- **Run with `prod` profile (uses MariaDB)**
  ```bash
  ./gradlew bootRun --args=\'--spring.profiles.active=prod\'
  ```
  The application will run on port `http://localhost:8080` by default.

## 🐳 Running with Docker

1.  **Build the JAR file**
    ```bash
    ./gradlew clean build -x test
    ```

2.  **Build the Docker image**
    ```bash
    docker build -t jlpt-backend:latest .
    ```

3.  **Run the Docker container**
    ```bash
    docker run -p 8080:8080 \
      -e "SPRING_PROFILES_ACTIVE=prod" \
      -e "SPRING_DATASOURCE_URL=jdbc:mariadb://<host>:<port>/<db>" \
      -e "SPRING_DATASOURCE_USERNAME=<user>" \
      -e "SPRING_DATASOURCE_PASSWORD=<password>" \
      -e "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET-KEY=<your_jwt_secret>" \
      jlpt-backend:latest
    ```
    > **Note**: When running with Docker, it is recommended to inject configurations using Spring Boot's standard environment variable format.

## 📖 API Endpoints

Detailed API specifications can be found at **[Link to API Docs]**.

- `POST /api/auth/signup`: Register a new user.
- `POST /api/auth/login`: Log in and issue a JWT.
- `GET /api/problems`: Get a list of questions.
- `POST /api/problems`: Generate a new question via AI.
- `POST /api/user-questions`: Request an AI answer to a user's question.

## 🗄️ Database Migration

This project uses `Flyway` to manage database schema versions. When the application starts, SQL scripts in the `src/main/resources/db/migration` path are automatically executed to keep the schema up to date.

## 🤝 Contributing

Contributions are always welcome! Please create an issue or submit a Pull Request.

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.
