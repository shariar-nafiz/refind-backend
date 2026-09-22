# ReFind Backend

Backend service for **ReFind**, a lost and found platform designed to connect people who have lost items with those who have found them.

> 📖 **Project Proposal & Architecture Roadmap:**  
> For the complete software architecture, Flyway migration roadmap (`V4` - `V8`), status audit of completed vs. pending modules, and feature designs, see [PROJECT_PLAN_AND_ROADMAP.md](PROJECT_PLAN_AND_ROADMAP.md).

## 🛠 Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 4.x
- **Data & Persistence:** Spring Data JPA, PostgreSQL, Flyway Migrations
- **Cache & Key-Value:** Upstash Redis (Spring Data Redis)
- **Security:** Spring Security (JWT)
- **Storage:** Local Filesystem with configurable media upload handlers
- **Notifications:** Firebase Cloud Messaging (FCM) & Resend (Email)
- **Monitoring:** Spring Boot Actuator & Prometheus
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

### 2. Configure Environment Variables

Copy the `.env.example` template to `.env` (or set the corresponding environment variables in your deployment environment):

```bash
cp .env.example .env
```

Key environment variables:
- **Database:** `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SSL_MODE`
- **Redis (Upstash):** `REDIS_URL`, `REDIS_SSL_ENABLED`
- **Storage:** `UPLOAD_DIR`, `UPLOAD_ITEMS_DIR`, `UPLOAD_AVATARS_DIR`
- **Security:** `JWT_SECRET`, `JWT_ACCESS_EXPIRATION_MS`, `JWT_REFRESH_EXPIRATION_MS`
- **Notifications:** `FIREBASE_CREDENTIALS_PATH`, `RESEND_API_KEY`

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
