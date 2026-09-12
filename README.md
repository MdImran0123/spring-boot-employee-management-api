# Employee API CRUD

REST API for managing employees. Built with **Spring Boot**, **Spring Data JPA**, and **MySQL**.

**Base URL:** `http://localhost:8080/api/v1`

**Repository:** [spring-boot-employee-management-api](https://github.com/MdImran0123/spring-boot-employee-management-api)

---

## Features

- Full CRUD for employees (create, read, update, delete)
- Lookup by id, name, city, age, or salary
- Jakarta Bean Validation on create/update requests
- DTO layer (`EmployeeRequest` / `EmployeeResponse`) — JPA entity is not exposed
- Global `404` handling for missing employees
- CORS enabled for local frontends on ports `5173` and `3000`
- Unit and Web MVC tests

---

## Tech stack

```text
Layer          Technology
Language       Java 21
Framework      Spring Boot 4.1.1
Web            Spring Web MVC
Persistence    Spring Data JPA / Hibernate
Validation     Jakarta Bean Validation
Database       MySQL (mysql-connector-j)
Build          Maven
Tests          JUnit 5, Mockito, MockMvc, AssertJ
```

---

## Architecture

Layered design: HTTP in the controller, business rules in the service, persistence in the repository.

```text
Client
  → EmployeeController   (/api/v1)
    → EmployeeService
      → EmployeeRepository (Spring Data JPA)
        → MySQL (employeedb.employee)
```

```text
Package                   Role
com.codemyth.controller   REST endpoints
com.codemyth.service      CRUD / lookup logic; entity ↔ DTO mapping
com.codemyth.repository   Spring Data JPA queries
com.codemyth.model        Employee entity
com.codemyth.dto          EmployeeRequest, EmployeeResponse
com.codemyth.exception    EmployeeNotFoundException, GlobalExceptionHandler
com.codemyth.config       CORS configuration
```

---

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8 running locally on port `3306`

---

## Database setup

1. Create the database:

```sql
CREATE DATABASE employeedb;
```

2. Create the `employee` table (schema is not auto-generated — `ddl-auto=none`):

```sql
USE employeedb;

CREATE TABLE employee (
  emp_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  emp_name   VARCHAR(255) NOT NULL,
  emp_age    INT NOT NULL,
  emp_city   VARCHAR(255) NOT NULL,
  emp_salary DECIMAL(10, 2) NOT NULL
);
```

---

## Configuration

Set database credentials as environment variables (used by `application.properties`):

| Variable | Description |
| --- | --- |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |

Datasource URL (default):

```properties
jdbc:mysql://localhost:3306/employeedb?useSSL=false&serverTimezone=UTC
```

**Windows (PowerShell) example:**

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_password"
```

**Linux / macOS example:**

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

---

## Run the application

From the `EmployeeAPICRUD` folder:

```bash
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

App starts on **`http://localhost:8080`**.

---

## API reference

All endpoints are under `/api/v1`.

### Request / response body

```json
{
  "empName": "Imran",
  "empAge": 28,
  "empCity": "Delhi",
  "empSalary": 50000.00
}
```

Response also includes `empId`:

```json
{
  "empId": 1,
  "empName": "Imran",
  "empAge": 28,
  "empCity": "Delhi",
  "empSalary": 50000.00
}
```

### Endpoints

```text
Method     Endpoint                         Description
POST       /employees                       Create employee
GET        /employees                       List all employees
GET        /employees/{empId}               Get employee by id
PUT        /employees/{empId}               Update employee by id
DELETE     /employees/{empId}               Delete employee by id
DELETE     /employees                       Delete all employees
GET        /employees/city/{empCity}        Search by city (partial, case-insensitive)
GET        /employees/age/{empAge}          Search by exact age
GET        /employees/salary/{empSalary}    Search by exact salary
GET        /employees/name/{empName}        Search by name (partial, case-insensitive)
```

### Examples

**Create**

```http
POST /api/v1/employees
Content-Type: application/json

{
  "empName": "Imran",
  "empAge": 28,
  "empCity": "Delhi",
  "empSalary": 50000.00
}
```

→ `201 Created`

**Get by id**

```http
GET /api/v1/employees/1
```

→ `200 OK` or `404 Not Found`

**Update**

```http
PUT /api/v1/employees/1
Content-Type: application/json

{
  "empName": "Imran Khan",
  "empAge": 29,
  "empCity": "Mumbai",
  "empSalary": 55000.00
}
```

→ `200 OK`

**Delete by id**

```http
DELETE /api/v1/employees/1
```

→ `204 No Content`

**Search by city**

```http
GET /api/v1/employees/city/Delhi
```

→ `200 OK` with a list (may be empty)

---

## Validation rules

Applied on `POST` and `PUT` via `@Valid` + `EmployeeRequest`:

```text
Field        Rules
empName      Required, not blank
empAge       Between 18 and 65
empCity      Required, not blank
empSalary    Required, not null, ≥ 0
```

---

## Error handling

Missing employee (by id) throws `EmployeeNotFoundException`, handled by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-09-11T13:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with Id 99"
}
```

---

## CORS

`/api/**` allows:

- Origins: `http://localhost:5173`, `http://localhost:3000`
- Methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

---

## Tests

```bash
./mvnw test
```

Coverage includes:

- `EmployeeServiceTest` — service / repository interaction
- `EmployeeControllerTest` — Web MVC endpoints
- `EmployeeRequestValidationTest` — request validation

---

## Project structure

```text
EmployeeAPICRUD/
├── src/main/java/com/codemyth/
│   ├── EmployeeApicrudApplication.java
│   ├── config/CorsConfig.java
│   ├── controller/EmployeeController.java
│   ├── dto/EmployeeRequest.java
│   ├── dto/EmployeeResponse.java
│   ├── exception/EmployeeNotFoundException.java
│   ├── exception/GlobalExceptionHandler.java
│   ├── model/Employee.java
│   ├── repository/EmployeeRepository.java
│   └── service/EmployeeService.java
├── src/main/resources/application.properties
├── src/test/java/com/codemyth/
│   ├── controller/EmployeeControllerTest.java
│   ├── dto/EmployeeRequestValidationTest.java
│   └── service/EmployeeServiceTest.java
├── pom.xml
└── README.md
```
