# ReFind Backend

Backend service for **ReFind**, a lost and found platform designed to connect people who have lost items with those who have found them.

## 🛠 Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 4.x
- **Data & Persistence:** Spring Data JPA, PostgreSQL
- **Security:** Spring Security
- **API Documentation:** Springdoc OpenAPI (Swagger UI)
- **Build Tool:** Gradle (Kotlin DSL)

---

## 📋 Prerequisites

Ensure you have the following installed locally:
- [Java 21 JDK](https://adoptium.net/) or higher
- [PostgreSQL](https://www.postgresql.org/) (running instance)
- Git

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/shariar-nafiz/refind-backend.git
cd refind-backend
```

### 2. Configure Database & Environment

Configure your database connection settings in `src/main/resources/application.properties` or via environment variables:

```properties
spring.application.name=refind

spring.datasource.url=jdbc:postgresql://localhost:5432/refind_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Build the Application

```bash
./gradlew clean build
```

### 4. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080` by default.

---

## 📖 API Documentation

Once the application is running, access the interactive OpenAPI documentation:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🧪 Running Tests

To run the test suite:

```bash
./gradlew test
```

---

## 📁 Project Structure

```
├── gradle/                  # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/            # Application source code
│   │   │   └── com/shariarunix/refind/
│   │   │       └── RefindApplication.java
│   │   └── resources/       # Application properties and resources
│   └── test/                # Unit and integration tests
├── build.gradle.kts         # Build configuration
├── settings.gradle.kts      # Project settings
├── .gitignore               # Git ignore rules
└── README.md                # Project documentation
```

---

## 📄 License

This project is licensed under the MIT License.
