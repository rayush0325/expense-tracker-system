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
- role-based access control
- ADMIN Role has access to administrative endpoints, including viewing and managing users
- Uses JWT claims to identify user roles
- Category management with CRUD operations (create, view, update, and delete categories)
- User-specific categories with ownership-based access control
- Global categories can be managed by admin only
- Users can create, view, update, and delete their expenses.
- Each expense is associated with a user and a category.
- Users can access and manage only their own expenses.
- Expenses can be associated with user-specific or global categories.
- Category ownership and access are validated when creating or updating expenses.


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

| Method | Endpoint | Access |Description |
|---|---|---|---|
| POST | `/api/v1/user/register` | Public | Register a new user |
| POST | `/api/v1/user/login` | Public |Authenticate user and obtain JWT |
| GET | `/api/v1/user/me` | Protected |Retrieve authenticated user details  |

Protected endpoints require a valid JWT in the `Authorization` header:

### Authorization API Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/user/profile` | USER, ADMIN | Access user profile |
| GET | `/api/v1/admin/users` | ADMIN | Retrieve all users |
| DELETE | `/api/v1/admin/users/{id}` | ADMIN | Delete a user by ID |

### Category API Endpoints

| **Method** | **Endpoint**                  | **Access** | **Description**          |
| ---------- | ----------------------------- | ---------- | ------------------------ |
| POST       | `/api/v1/category`            | USER       | Create a new category    |
| GET        | `/api/v1/category`            | USER       | Retrieve all categories  |
| PUT        | `/api/v1/category/{id}`       | USER       | Update a category        |
| DELETE     | `/api/v1/category/{id}`      | USER       | Delete a category        |

### Expense API Endpoints

| **Method** | **Endpoint**                  | **Access** | **Description**          |
| ---------- | ----------------------------- | ---------- | ------------------------ |
| POST       | `/api/v1/expense`             | USER       | Create a new expense     |
| GET        | `/api/v1/expense`             | USER       | Retrieve all expenses    |
| PUT        | `/api/v1/expense/{id}`        | USER       | Update an existing expense |
| DELETE     | `/api/v1/expense/{id}`        | USER       | Delete an expense        |


**Authorization:** JWT-based authentication with role-based access control.

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

### Authorization Flow

1. JWT authentication filter validates the token and extracts the user's identity and role.
2. Spring Security checks whether the user has the required role to access the requested endpoint.
3. Access is granted if the user is authorized; otherwise:
   - **401 Unauthorized:** Authentication is missing or invalid.
   - **403 Forbidden:** Authenticated user does not have the required role.

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
- Expense filtering and summaries
- Pagination and additional reporting

## Project Status

**In Development** — authentication, registration, frontend-backend integration, validation, and testing are being developed incrementally.

## 🌐 Live Demo

**[Expense Tracker – Open Application](https://expense-tracker-system-henna.vercel.app/)**
