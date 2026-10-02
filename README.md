# Expense Tracker System (Spring Boot + React + Vite)

A full-stack expense tracking application built with **Spring Boot** and **React**.
The project focuses on REST API development, database persistence, validation, exception handling, testing, and frontend-backend integration.

# 🛠️ Prerequisites
Before you begin, ensure you have met the following requirements:

- Java JDK (17 or higher)
- Node.js (18 or higher) & npm
- Maven (or use the included ./mvnw wrapper)

# 🚀 Getting Started
1. Backend (Spring Boot)
  - The backend runs on http://localhost:8080 by default.
  - To run the backend:
    - ./mvnw spring-boot:run

2. Frontend (React + Vite)
  - The frontend runs on http://localhost:5173 by default (Vite default port).
  - To run the frontend:
    - cd frontend
    - npm install
    - npm run dev


## Tech Stack

**Backend**

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* Spring Security
* MySQL
* Maven
* JUnit, Mockito, AssertJ

**Frontend**

* React
* Vite
* Axios
* Bootstrap
* React Router
* React Toastify

## Features

* User registration
* Request validation
* BCrypt password encryption
* Duplicate email handling
* Global and local exception handling
* REST API integration with React
* Client-side form validation
* Unit and integration testing
* MySQL database persistence

## Project Structure

```text
expense-tracker-system/
├── backend/
│   ├── src/main/java/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   ├── dto/
│   │   ├── exception/
│   │   └── config/
│   └── pom.xml
│
└── frontend/
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   └── services/
    └── package.json
```

## API

### User Registration

```http
POST /api/v1/user/register
```

Request:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

Successful response:

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### Response Statuses

| Status            | Description                  |
| ----------------- | ---------------------------- |
| `201 Created`     | User registered successfully |
| `400 Bad Request` | Invalid request data         |
| `409 Conflict`    | Email already exists         |

## Testing

The project includes:

* Service unit tests using Mockito and AssertJ
* Controller tests using MockMvc
* Integration tests using Spring Boot and H2
* Database persistence verification
* Duplicate email test cases


Configure the required MySQL database and environment variables before starting the backend.

## Future Improvements

* User login
* JWT authentication
* Role-based authorization
* Expense CRUD
* Category management
* Expense filtering and summaries

## API Documentation

The REST APIs are documented using **Swagger / OpenAPI**.

After starting the backend, Swagger UI can be accessed at:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger UI allows the available APIs to be viewed and tested directly from the browser.


