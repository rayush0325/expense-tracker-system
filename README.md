# Expense Tracker System

A full-stack web application built with **Spring Boot** and **React**. The project focuses on REST API development, JWT-based authentication, database persistence, input validation, exception handling, and automated testing.

## Tech Stack

- **Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA

- **Frontend:** React, Vite, Axios, Bootstrap

- **Database:** MySQL

- **Authentication:** JWT, BCrypt

- **API Documentation:** Swagger UI
  
- **Testing:** JUnit, Mockito, AssertJ, MockMvc

- **Build Tool:** Maven

- **Deployment:** Docker, Render, Aiven, Vercel

## Features

- User registration with input validation
- User login using email and password
- JWT-based authentication for protected endpoints
- BCrypt password hashing
- Persistent authentication across browser refreshes using local storage
- Automatic JWT attachment to API requests using an Axios interceptor
- Centralized handling of unauthorized responses
- User profile retrieval and authenticated navbar
- Global and controller-level exception handling
- Database persistence using MySQL
- Unit, controller, and integration tests
- Interactive API documentation using Swagger UI
- Dockerized backend for deployment
- frontend is mobile responsive

## Project Structure

```text
expense-tracker-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   ├── compose.yaml
│   └── pom.xml
│
└── frontend/
    ├── src/
    │   ├── components/
    │   ├── context/
    │   ├── pages/
    │   └── services/
    └── package.json
```

## API Endpoints

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/v1/user/register` | Register a new user | Public |
| POST | `/api/v1/user/login` | Authenticate user and obtain JWT | Public |
| GET | `/api/v1/user/me` | Retrieve authenticated user details | Protected |

Protected endpoints require a valid JWT in the `Authorization` header:

```http
Authorization: Bearer <your-jwt-token>
```

## API Documentation

Swagger UI provides interactive documentation for viewing and testing the REST APIs.

Start the backend and visit:

`https://expense-tracker-system-test.onrender.com/swagger-ui/index.html#/`

## Authentication Flow

1. The user submits login credentials.
2. Spring Security authenticates the credentials.
3. The backend generates and returns a signed JWT.
4. The frontend stores the token in local storage.
5. Axios attaches the token to subsequent API requests.
6. The backend validates the token and authenticates requests to protected endpoints.
7. If an unauthorized response indicates an invalid or expired session, the frontend clears the authentication state and logs the user out.

## Testing

- **Unit tests:** JUnit, Mockito, and AssertJ
- **Controller tests:** MockMvc
- **Integration tests:** Spring Boot and H2
- Registration success and duplicate-email scenarios
- Request validation and exception handling
- Authentication and JWT-related test cases

## Getting Started

### Prerequisites

- Java
- Maven
- Node.js and npm
- MySQL

### 1. Configure the Backend

Configure the MySQL connection and required environment variables. Keep database credentials and JWT secrets outside the source code.

### 2. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

### 3. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

## Docker

The backend includes a Dockerfile to build and run the Spring Boot application in a container.

From the backend directory, build the application image:

```bash
mvn clean package
docker compose up --build
```
## Deployment

## Frontend Deployment (Vercel)

The frontend is deployed on Vercel.

**Live URL:** https://expense-tracker-system-henna.vercel.app/

The backend is deployed on Render using docker.

**Backend URL:** https://expense-tracker-system-test.onrender.com

## Future Improvements

- Expense CRUD operations
- Category management
- Expense filtering and summaries
- Pagination and additional reporting
- Deployment and production configuration

## Project Status

**In Development** — authentication, registration, frontend-backend integration, validation, and testing are being developed incrementally.

## 🌐 Live Demo

**[Expense Tracker – Open Application](https://expense-tracker-system-henna.vercel.app/)**
