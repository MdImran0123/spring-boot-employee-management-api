# Employee API CRUD

REST API for managing employees. Built with **Spring Boot**, **Spring Data JPA**, and **MySQL**.

**Base URL:** `http://localhost:8080/api/v1`

**Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
**OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  
**Health:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)  
**Info:** [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

**Repository:** [spring-boot-employee-management-api](https://github.com/MdImran0123/spring-boot-employee-management-api)

See [docs/ROADMAP.md](docs/ROADMAP.md) for planned improvements.

---

## Features

- Full CRUD for employees (create, read, update, delete)
- Lookup by id, name, city, age, or salary
- Jakarta Bean Validation on create/update requests
- DTO layer (`EmployeeRequest` / `EmployeeResponse`) — JPA entity is not exposed
- Global `404` handling for missing employees
- CORS enabled for local frontends on ports `5173` and `3000`
- OpenAPI / Swagger UI via springdoc
- Actuator health and info endpoints
- Unit and Web MVC tests

---

## Tech stack

```text
Layer          Technology
Language       Java 21
Framework      Spring Boot 4.1.1
Web            Spring Web MVC
Persistence    Spring Data JPA / Hibernate
Mapping        MapStruct
Validation     Jakarta Bean Validation
Database       MySQL (mysql-connector-j)
Migrations     Flyway
API docs       springdoc-openapi (Swagger UI)
Ops            Spring Boot Actuator (health, info)
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
com.codemyth.service      CRUD / lookup logic
com.codemyth.mapper       Entity to DTO mapping (MapStruct)
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

1. Create the database (Flyway does **not** create the database itself):

```sql
CREATE DATABASE employeedb;
```

2. Start the app. Flyway runs migrations from `src/main/resources/db/migration/` on startup (`ddl-auto=none`).

- `V1__create_employee.sql` creates the `employee` table.
- `V2__indexes.sql` adds indexes on `emp_city` and `emp_name` for search.
- `V3__soft_delete.sql` adds a `deleted` flag so deletes are soft deletes.

**Existing database:** if `employee` already exists and Flyway has never run, baseline once before starting the app:

```bash
./mvnw flyway:baseline -Dflyway.url=jdbc:mysql://localhost:3306/employeedb -Dflyway.user=... -Dflyway.password=...
```

Or point a fresh empty `employeedb` at the app and let `V1` create the table.
---

## Configuration

Spring profiles split **shared** settings (`application.properties`) from **dev** and **prod** overrides. Local runs default to the **dev** profile (`spring.profiles.active=dev`).

| Profile | File | Purpose |
| --- | --- | --- |
| (shared) | `application.properties` | Flyway, pagination, Actuator exposure, app info |
| `dev` | `application-dev.properties` | Local MySQL, optional SQL logging, health details visible |
| `prod` | `application-prod.properties` | `DB_URL` from env, no SQL logging, stricter logging |

Set database credentials as environment variables:

| Variable | Description |
| --- | --- |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `DB_URL` | JDBC URL (**prod** profile only; required when `spring.profiles.active=prod`) |

**Dev** datasource URL (fixed in `application-dev.properties`):

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

From the `EmployeeAPICRUD` folder (uses **dev** profile by default):

```bash
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

**Production profile** (set `DB_URL` plus credentials):

```bash
export DB_URL='jdbc:mysql://your-host:3306/employeedb?useSSL=true&serverTimezone=UTC'
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

Override profile explicitly for local dev:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

App starts on **`http://localhost:8080`**.

Actuator exposes only `health` and `info`. Health details stay hidden:

- `GET /actuator/health`
- `GET /actuator/info`

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
GET        /employees                       List/search employees (query: name, city, age, salary + page, size, sort)
GET        /employees/{empId}               Get employee by id
PUT        /employees/{empId}               Update employee by id
PATCH      /employees/{empId}               Partial update (only sent fields)
DELETE     /employees/{empId}               Soft delete employee by id
DELETE     /employees                       Soft delete all employees
GET        /employees/city/{empCity}        Deprecated — use ?city=
GET        /employees/age/{empAge}          Deprecated — use ?age=
GET        /employees/salary/{empSalary}    Deprecated — use ?salary=
GET        /employees/name/{empName}        Deprecated — use ?name=
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
GET /api/v1/employees?city=Delhi
```

→ `200 OK` with a paginated list (may be empty)

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
├── src/main/resources/application-dev.properties
├── src/main/resources/application-prod.properties
├── src/main/resources/db/migration/V1__create_employee.sql
├── src/main/resources/db/migration/V2__indexes.sql
├── src/main/resources/db/migration/V3__soft_delete.sql
├── src/test/java/com/codemyth/
│   ├── controller/EmployeeControllerTest.java
│   ├── dto/EmployeeRequestValidationTest.java
│   └── service/EmployeeServiceTest.java
├── pom.xml
└── README.md
```
