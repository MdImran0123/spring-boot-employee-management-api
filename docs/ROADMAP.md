# Employee API — Full Improvement Roadmap

Phased improvements for **EmployeeAPICRUD**. Implement one phase at a time; update tests, README, and `employee-ui` when an API contract changes.

**Status:** Planned (not yet implemented unless noted in a phase checklist).

---

## Why this file lives here

```text
Location                        Verdict
EmployeeAPICRUD/docs/ROADMAP.md Best — backend docs with the module; README stays focused on run/setup
Inside README.md                Avoid — keeps the README long and hard to scan
Repo-root docs/                 Better only for multi-module shared docs
.cursor/plans/                  Avoid — Cursor-internal; not ideal for GitHub / portfolio readers
```


---

## Current baseline

Already in place:

- Layered design: controller → service → repository
- DTOs (`EmployeeRequest` / `EmployeeResponse`), Bean Validation
- CORS for local frontends (`5173`, `3000`)
- Unit and Web MVC tests
- Global handler for `EmployeeNotFoundException` (404)

Gaps this roadmap addresses: validation error body, pagination, path-heavy search, empty list → 404, migrations, mapping, soft delete, PATCH, logging, OpenAPI, Actuator, profiles, JWT security, Docker, Testcontainers.

```text
Client
  → EmployeeController   (/api/v1)
    → EmployeeService
      → EmployeeRepository (Spring Data JPA)
        → MySQL (employeedb.employee)
```

---

## Phase 1 — API quality (highest value, low risk)

### 1.1 Validation error handler

**Why:** `@Valid` failures currently return Spring’s default 400 body. Clients need field-level messages in a stable JSON shape.

**What:**

- Extend `GlobalExceptionHandler` with `MethodArgumentNotValidException`.
- Response shape aligned with 404: `timestamp`, `status`, `error`, `message`, plus `errors: { field: message }`.
- Add a safe generic `Exception` → `500` handler (no stack traces in the response body).

**Touch:** `com.codemyth.exception.GlobalExceptionHandler` (+ controller tests for 400 body).

### 1.2 Empty search → `200 OK` + `[]`

**Why:** An empty list is not a missing resource. Use `404` only when a specific employee id is not found (`GET` / `PUT` / `DELETE` by id).

**What:**

- In `EmployeeService`, stop throwing on empty city / age / salary / name lookups; return an empty list.
- Keep id-not-found as `EmployeeNotFoundException` → 404.
- Fix typo if any leftover message remains: `bu Salary` → `by Salary`.

**Touch:** `EmployeeService` (+ related tests).

### 1.3 Pagination on list

**Why:** Unbounded `findAll()` does not scale.

**What:**

- `GET /api/v1/employees?page=0&size=20&sort=empId,asc`
- Spring `Pageable` with a response that exposes `content`, `page`, `size`, `totalElements`, `totalPages` (Spring `Page` JSON or a small wrapper DTO).
- Defaults: `size=20`; enforce a max (e.g. `100`).

**Touch:** `EmployeeController`, `EmployeeService`, `EmployeeRepository`, tests, README, `employee-ui` list load.

### 1.4 Unified search (query params)

**Why:** Separate paths (`/city/{x}`, `/age/{x}`, …) cannot combine filters cleanly.

**What (chosen approach):**

- Primary API:  
  `GET /api/v1/employees?name=&city=&age=&salary=&page=&size=`
- Optional filters; use `JpaSpecificationExecutor` or a null-safe `@Query`.
- Mark path-based search endpoints **deprecated** (Javadoc + README); remove in a later release after `employee-ui` migrates to query params.

**Touch:** controller, repository, service, tests, README, `employee-ui` search.

---

## Phase 2 — Schema, DTOs, mapping

### 2.1 Flyway migrations

**Why:** `ddl-auto=none` plus hand-run SQL drifts across machines.

**What:**

- Add `flyway-core` + `flyway-mysql`.
- `src/main/resources/db/migration/V1__create_employee.sql` — current `employee` table.
- Document Flyway in README; remove “run CREATE TABLE manually” as the primary path (keep note for existing DBs).

### 2.2 DB indexes

**Why:** City/name search benefits from indexes.

**What:** Flyway `V2__indexes.sql` on `emp_city` and `emp_name`.

### 2.3 DTO as Java `record`

**Why:** Less boilerplate, immutable API contracts.

**What:** Convert `EmployeeRequest` / `EmployeeResponse` to records; keep validation annotations; update tests.

### 2.4 MapStruct mapper

**Why:** Manual entity ↔ DTO mapping in the service is easy to get wrong as fields grow.

**What:** `EmployeeMapper` (MapStruct); service focuses on orchestration; remove private `mapToResponse`.

### 2.5 Soft delete

**Why:** Hard delete (especially delete-all) loses recoverable data.

**What (chosen):**

- Column `deleted` boolean, default `false` — Flyway `V3__soft_delete.sql`.
- Filter active rows (`@SQLRestriction("deleted = false")` or equivalent query filters).
- `DELETE` by id sets `deleted = true`.
- Optional later: admin hard-delete endpoint.

### 2.6 PATCH partial update

**Why:** PUT forces a full body for every update.

**What:**

- `PATCH /api/v1/employees/{empId}`
- `EmployeePatchRequest` with all fields optional/nullable; apply only non-null fields.

---

## Phase 3 — Observability & docs

### 3.1 Logging

**Why:** Production debugging without attaching a debugger.

**What:** SLF4J (`@Slf4j`) on service (and lightly on controller if useful):

- Info: create / update / delete (ids only)
- Warn: not-found paths
- Error: unexpected failures  
Avoid logging passwords or unnecessary PII.

### 3.2 springdoc-openapi

**Why:** Interactive API docs for humans and frontends.

**What:** springdoc dependency; Swagger UI; brief controller annotations; README link to the UI path (Boot 4 / springdoc path as configured).

### 3.3 Actuator

**Why:** Health checks for ops and Docker.

**What:** `spring-boot-starter-actuator`; expose `health` and `info` by default; tighten further in prod.

### 3.4 Profiles

**Why:** Dev vs prod config should not share one flat file forever.

**What:**

- `application.properties` — shared defaults
- `application-dev.properties` — local MySQL, optional SQL logging
- `application-prod.properties` — stricter settings  

Run example: `--spring.profiles.active=dev`

---

## Phase 4 — Security & ops

### 4.1 Spring Security + JWT

**Why:** Open write/delete endpoints (especially `DELETE /employees`) are unsafe on any shared network.

**What (chosen for REST):**

- Public (dev-friendly): GET list / get / search, Swagger, health
- Authenticated: POST, PUT, PATCH, DELETE
- `POST /api/v1/auth/login` issues JWT (in-memory user acceptable for v1; DB users later)
- CORS remains for `5173` / `3000` and allows `Authorization`

### 4.2 Docker

**Why:** One-command local stack; closer to deployment.

**What:**

- Multi-stage `Dockerfile` (Maven build → JRE image)
- `docker-compose.yml`: MySQL + app, `DB_*` via env
- `.env.example` only (no real secrets in git)

### 4.3 Testcontainers

**Why:** Unit/WebMvc tests alone do not prove Flyway + real MySQL behavior.

**What:** Integration test with MySQL container + Flyway + a few critical HTTP flows; keep existing unit and WebMvc tests.

---

## Target layout (as phases land)

```text
EmployeeAPICRUD/
  docs/
    ROADMAP.md
  src/main/resources/
    db/migration/          # Phase 2
    application.properties
    application-dev.properties
    application-prod.properties
  Dockerfile               # Phase 4
  docker-compose.yml
  .env.example
```

---

## Implementation order

1. **Phase 1** — validation handler, empty → `[]`, pagination, query-param search (+ tests)
2. **Phase 2** — Flyway, indexes, records, MapStruct, soft delete, PATCH
3. **Phase 3** — logging, OpenAPI, Actuator, profiles
4. **Phase 4** — JWT security, Docker, Testcontainers
5. After each breaking phase: update `employee-ui` and `README.md`

---

## Checklist

Use this when starting each phase:

- [x] Phase 1.1 Validation error handler
- [x] Phase 1.2 Empty search → 200 + `[]`
- [x] Phase 1.3 Pagination
- [x] Phase 1.4 Unified query-param search (+ deprecate path search)
- [x] Phase 2.1 Flyway
- [x] Phase 2.2 Indexes
- [x] Phase 2.3 DTO records
- [x] Phase 2.4 MapStruct
- [x] Phase 2.5 Soft delete
- [x] Phase 2.6 PATCH
- [x] Phase 3.1 Logging
- [x] Phase 3.2 springdoc-openapi
- [x] Phase 3.3 Actuator
- [x] Phase 3.4 Profiles
- [ ] Phase 4.1 JWT security
- [ ] Phase 4.2 Docker / Compose
- [ ] Phase 4.3 Testcontainers
