# Employee API — Full Improvement Roadmap

Phased improvements for **EmployeeAPICRUD**. Implement one phase at a time; update tests, README, and `employee-ui` when an API contract changes.

**Status:** Phases 1–4 complete (see checklist). Phases 5–9 are planned next; implement one sub-phase at a time.

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

Phases 1–4 delivered: validation error body, pagination, unified query search, Flyway, MapStruct, soft delete, PATCH, logging, OpenAPI, Actuator, profiles, JWT, RBAC, Docker, Testcontainers.

**Next focus:** Remove deprecated APIs, align `employee-ui` with pagination / PATCH / combined search, then admin features, DevOps, and documentation (Phases 5–9).

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

4.1 authentication; 4.2 authorization; 4.3–4.4 deployment and integration testing.

### 4.1 Spring Security + JWT

**Why:** Open write/delete endpoints (especially `DELETE /employees`) are unsafe on any shared network.

**What (chosen for REST):**

- Public (dev-friendly): GET list / get / search, Swagger, health
- Authenticated: POST, PUT, PATCH, DELETE
- `POST /api/v1/auth/login` issues JWT (in-memory user acceptable for v1; DB users later)
- CORS remains for `5173` / `3000` and allows `Authorization`

### 4.2 Role-based access control (RBAC)

**Why:** Phase 4.1 only distinguishes anonymous vs authenticated; every logged-in user can POST, PUT, PATCH, and DELETE. You need **authenticated reads** and **different powers per role** (for example who may delete one employee vs delete-all).

**Note:** 4.1 remains done; 4.2 is a **breaking change for anonymous clients** on employee GET endpoints and refines authorization without replacing JWT login.

**What (chosen approach):**

Roles (v1):

```text
Role     Employee API access
VIEWER   GET /api/v1/employees/** (list, search, by id, deprecated path GETs)
EDITOR   VIEWER + POST, PUT, PATCH
ADMIN    EDITOR + DELETE /employees/{id} and DELETE /employees (delete-all)
```

**Auth / token:**

- Extend JWT to carry roles (e.g. `roles` claim or Spring-standard authorities).
- Update `JwtAuthenticationFilter` to set `GrantedAuthority` from the token (today: hardcoded `ROLE_USER`).
- Remove broad `permitAll` on `GET /api/v1/employees/**` in `SecurityConfig`; require authentication for all employee endpoints.
- Primary enforcement: `@EnableMethodSecurity` + `@PreAuthorize` on `EmployeeController` methods (clear per-endpoint rules).

**Users and roles storage (builds on 4.1 “DB users later”):**

- Flyway `V4__users_and_roles.sql`: `app_user`, `role`, `user_role` (or equivalent); BCrypt passwords; seed dev users (e.g. `viewer@dev`, `editor@dev`, `admin@dev`).
- JPA **Option 1:** `AppUser` `@ManyToMany` `Role` via `@JoinTable(name = "user_role")` — no `UserRole` entity.
- Replace `InMemoryUserDetailsManager` with JPA `UserDetailsService` loading roles from DB.
- `POST /api/v1/auth/login`: unchanged path; response may add optional `roles` array for clients (document in README when implemented).

**Public without JWT (unchanged ops/docs):**

- `POST /api/v1/auth/login`
- Actuator `health` / `info`
- Swagger / OpenAPI UI paths (same as 4.1)

**API errors:**

- Reuse existing JSON **401** (no or invalid token) and **403** (`RestAccessDeniedHandler`) for wrong role.

**Tests:**

- WebMvc: VIEWER → GET 200, POST 403; EDITOR → PATCH 200, DELETE 403; ADMIN → delete-all 204.
- Login issues a token whose roles match the DB user.

**Client / docs (when 4.2 lands):**

- `employee-ui`: attach Bearer on **GET** as well as writes; optional UI (hide Delete All unless admin).
- README: role matrix, sample users, breaking change note (“reads no longer public”).

**Touch:** `SecurityConfig`, `JwtService`, `JwtAuthenticationFilter`, `AuthController`; new user/role entities, repositories, Flyway V4; `EmployeeController` (+ security annotations); tests (`EmployeeControllerTest`, `AuthControllerTest`, RBAC cases); README, `employee-ui`.

### 4.3 Docker

**Why:** One-command local stack; closer to deployment.

**What:**

- Multi-stage `Dockerfile` (Maven build → JRE image)
- `docker-compose.yml`: MySQL + app, `DB_*` via env
- `.env.example` only (no real secrets in git)

### 4.4 Testcontainers

**Why:** Unit/WebMvc tests alone do not prove Flyway + real MySQL behavior.

**What:** Integration test with MySQL container + Flyway + a few critical HTTP flows; keep existing unit and WebMvc tests.

---

## Phase 5 — API cleanup (breaking, low risk if UI already migrated)

### 5.1 Remove deprecated path-based search

**Why:** Phase 1.4 added `GET /employees?name=&city=&age=&salary=&page=&size=`. Path endpoints duplicate behavior and clutter OpenAPI.

**What:**

- Remove `GET /employees/city/{city}`, `/age/{age}`, `/salary/{salary}`, `/name/{name}` from `EmployeeController`.
- Delete related WebMvc tests that only cover deprecated paths.
- README + springdoc: document query-param search as the only search API.
- **Breaking change note** in README or `docs/CHANGELOG.md` (clients still on path URLs must migrate).

**Touch:** `EmployeeController`, controller tests, README.

### 5.2 Stable API change policy

**Why:** Future breaking changes should not surprise consumers.

**What:**

- Document policy: breaking changes only with CHANGELOG entry; optional future `/api/v2` if needed (no v2 in this phase unless required).
- Add short `docs/CHANGELOG.md` or README subsection for API changes.

**Touch:** `docs/CHANGELOG.md` (new) or README.

---

## Phase 6 — `employee-ui` parity with backend

Backend already supports pagination, unified filters, and PATCH; the UI should use them fully.

### 6.1 Pagination controls

**Why:** `GET /employees` returns paged JSON; UI loads page 0 only and shows totals without navigation.

**What:**

- Track `page` and `size` in state (default `size=20`).
- Pass `page` / `size` to `getAllEmployees` and search calls.
- Previous / Next (and optional page size selector); disable at bounds using `totalPages`.
- After create/update/delete, reload **current** page (or clamp if last item on last page was deleted).

**Touch:** `employee-ui/src/App.jsx`, `employeeApi.js`, README (UI features).

### 6.2 Combined filter search

**Why:** API accepts multiple query params; UI forces a single `searchType` + one value.

**What:**

- Search form: optional fields for name, city, age, salary (empty = omit param).
- “Clear filters” resets to paginated list (`GET /employees?page=&size=`).
- Client-side validation aligned with API (numeric age/salary).

**Touch:** `App.jsx`, `employeeApi.js`.

### 6.3 PATCH for edit flow

**Why:** Backend exposes `PATCH /employees/{id}` for partial updates; UI always sends full PUT body.

**What:**

- On edit, call PATCH with full form as non-null fields (or only changed fields — pick one approach; document in UI README).
- Add `patchEmployee(id, partial)` in `employeeApi.js`.
- Keep PUT on backend; UI prefers PATCH for edits.

**Touch:** `employeeApi.js`, `App.jsx`.

### 6.4 Configurable API base URL

**Why:** Hardcoded `http://localhost:8080` breaks Docker and deployed backends.

**What:**

- `const API = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api/v1"` (keep path suffix consistent).
- Document `.env.example` in `employee-ui` with `VITE_API_URL=http://localhost:8080/api/v1`.
- Phase 8.1: pass build arg / env for production API URL in Docker.

**Touch:** `employeeApi.js`, `employee-ui/.env.example`, README.

### 6.5 Frontend structure (maintainability)

**Why:** Single large `App.jsx` is harder to test and extend.

**What (incremental):**

- Extract: `LoginPanel`, `EmployeeForm`, `EmployeeTable`, `SearchBar`, `PaginationBar`.
- Optional later: TypeScript migration, Vitest + React Testing Library for API helpers and one smoke component test.

**Touch:** `employee-ui/src/components/*`, `package.json` (if Vitest added).

### 6.6 UX polish

**Why:** Better demo and clearer validation feedback.

**What:**

- Field-level display of validation `errors` from 400 responses (map keys to form labels).
- Consistent loading state on pagination/search (not only initial load).
- Keep or refine inline success/error messages (confirm dialogs already exist for delete).

**Touch:** `App.jsx`, `App.css`.

---

## Phase 7 — Data model, admin, and security hardening

### 7.1 Audit columns on `employee`

**Why:** Traceability for updates in real systems.

**What:**

- Flyway `V5__employee_audit.sql`: e.g. `created_at`, `updated_at`, optional `updated_by` (username from JWT).
- JPA `@PrePersist` / `@PreUpdate` or `@EnableJpaAuditing`.
- Expose read-only timestamps in `EmployeeResponse` (optional in list, show in detail).

**Touch:** entity, Flyway, DTO, mapper, tests, integration test.

### 7.2 Soft-delete lifecycle

**Why:** Phase 2.5 soft-deletes rows; operators may need restore or purge.

**What:**

- `POST /api/v1/employees/{id}/restore` — **ADMIN** only; sets `deleted = false`.
- Optional: `DELETE /api/v1/employees/{id}/permanent` — **ADMIN** only; hard delete row.
- List endpoints remain active-only unless `?includeDeleted=true` for **ADMIN** (document clearly).

**Touch:** service, controller, security, Flyway if indexes needed, tests.

### 7.3 User & role administration

**Why:** RBAC users live in DB; no API to manage them without SQL.

**What:**

- **ADMIN** endpoints: list users (no password hash), create user, assign roles, disable user, reset password (BCrypt).
- DTOs without leaking hashes; validation on username/password policy.
- OpenAPI + README role matrix update.

**Touch:** new admin user controller/service, tests, README.

### 7.4 Login abuse protection

**Why:** Public `POST /auth/login` is a brute-force target.

**What:**

- Rate limit login by IP (Bucket4j, Spring filter, or reverse-proxy doc for prod).
- Generic 401 message; optional lockout after N failures per user (`failed_attempts` / `locked_until` in DB).

**Touch:** security config, Flyway if DB lockout, tests.

### 7.5 Token lifecycle (optional)

**Why:** Long-lived JWT in `localStorage` is convenient for demos but not ideal for production.

**What (choose one track):**

- **A (simpler):** Shorter access token TTL + README documents re-login; optional refresh token table + `POST /auth/refresh`.
- **B (harder):** httpOnly secure cookie session (document CSRF strategy).

**Default recommendation:** 7.4 first, then 7.5A.

**Touch:** `JwtService`, `AuthController`, `employee-ui` token storage, README security section.

---

## Phase 8 — DevOps, observability, and quality gates

### 8.1 Full-stack Docker Compose

**Why:** Phase 4.3 runs MySQL + API only; UI still manual (`npm run dev`).

**What:**

- `employee-ui/Dockerfile` — multi-stage: `npm ci && npm run build` → nginx (or static serve).
- Extend `docker-compose.yml` with `ui` service; API URL via `VITE_API_URL` at build time or runtime proxy (`/api` → `app:8080`).
- Document one command: `docker compose up --build`.

**Touch:** Dockerfiles, compose, `.env.example`, README.

### 8.2 CI pipeline

**Why:** Regressions caught before merge; portfolio signal.

**What:**

- GitHub Actions: JDK 21, `mvn -B verify` (unit + WebMvc + Testcontainers integration).
- Optional job: `employee-ui` `npm ci && npm run build`.
- Cache Maven/npm; fail on test failure.

**Touch:** `.github/workflows/ci.yml`, README badge (optional).

### 8.3 Metrics and request tracing

**Why:** Actuator health exists; ops needs metrics and correlatable logs.

**What:**

- Expose `prometheus` actuator endpoint in prod profile (secure or internal network only).
- MDC filter: `X-Request-Id` (generate if missing); include in log pattern.
- README: local Prometheus scrape example (optional).

**Touch:** `application-prod.properties`, filter class, security rules for actuator.

### 8.4 Dependency and security scanning

**Why:** Keep Spring and transitive CVEs visible.

**What:**

- OWASP Dependency-Check or GitHub Dependabot for `pom.xml` and `employee-ui/package.json`.
- Document how to run locally.

**Touch:** `pom.xml` plugin or `.github/dependabot.yml`.

### 8.5 Load / smoke testing (optional)

**Why:** Prove pagination and auth under basic load.

**What:**

- k6 or Gatling script: login → list → create → delete (tokens from env).
- Run manually or via CI `workflow_dispatch` (avoid flakiness on shared runners).

**Touch:** `docs/load/` or `scripts/k6/`, README.

---

## Phase 9 — Documentation & portfolio

### 9.1 Architecture documentation

**Why:** Readers should grasp the system in one diagram.

**What:**

- `docs/ARCHITECTURE.md`: Mermaid diagram — Browser → UI → API → JPA → MySQL; JWT + RBAC flow.
- Link ROADMAP, Docker, and UI setup in README “Quick start”.

**Touch:** `docs/ARCHITECTURE.md`, README links.

### 9.2 API consumer artifacts

**Why:** Easier for interviewers and integrators.

**What:**

- Link to `/v3/api-docs` and Swagger UI in docs.
- Optional Postman collection from OpenAPI.

**Touch:** `docs/postman/` or README link.

---

## Target layout (as phases land)

```text
EmployeeAPICRUD/
  docs/
    ROADMAP.md
    CHANGELOG.md          # Phase 5.2
    ARCHITECTURE.md       # Phase 9.1
  src/main/resources/
    db/migration/         # Phase 2; V4 users/roles; V5+ audit (Phase 7)
    application.properties
    application-dev.properties
    application-prod.properties
  Dockerfile              # Phase 4.3
  docker-compose.yml      # Phase 8.1: optional ui service
  .env.example
employee-ui/
  Dockerfile              # Phase 8.1
  .env.example            # Phase 6.4
  src/components/         # Phase 6.5
.github/
  workflows/ci.yml        # Phase 8.2
```

---

## Implementation order

**Completed:**

1. **Phase 1** — validation handler, empty → `[]`, pagination, query-param search (+ tests)
2. **Phase 2** — Flyway, indexes, records, MapStruct, soft delete, PATCH
3. **Phase 3** — logging, OpenAPI, Actuator, profiles
4. **Phase 4** — JWT (4.1), RBAC (4.2), Docker (4.3), Testcontainers (4.4)

**Next:**

5. **Phase 5** — remove deprecated search paths (+ CHANGELOG)
6. **Phase 6** — UI pagination, combined search, PATCH, `VITE_API_URL` (+ structure/UX as time allows)
7. **Phase 7** — audit fields → soft-delete restore → admin user APIs → rate limit → optional refresh tokens
8. **Phase 8** — Compose with UI → CI → metrics/request ID → dependency scan → optional load tests
9. **Phase 9** — architecture doc and API artifacts (can run parallel to 6–8)

After each **breaking** API phase: update CHANGELOG, README, and `employee-ui`.

**Suggested first sprint:** 5.1 → 6.1 → 6.2 → 6.4 → 8.2 (early CI win).

---

## Checklist

Use this when starting each phase:

**Phases 1–4 (done)**

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
- [x] Phase 4.1 JWT security
- [x] Phase 4.2 RBAC
- [x] Phase 4.3 Docker / Compose
- [x] Phase 4.4 Testcontainers

**Phases 5–9 (planned)**

- [ ] Phase 5.1 Remove deprecated path search
- [ ] Phase 5.2 API change policy / CHANGELOG
- [ ] Phase 6.1 UI pagination controls
- [ ] Phase 6.2 Combined filter search
- [ ] Phase 6.3 PATCH edit flow
- [ ] Phase 6.4 `VITE_API_URL` + `.env.example`
- [ ] Phase 6.5 Component extraction
- [ ] Phase 6.6 UX (field errors, loading on page change)
- [ ] Phase 7.1 Employee audit columns
- [ ] Phase 7.2 Restore / permanent delete / includeDeleted
- [ ] Phase 7.3 Admin user APIs
- [ ] Phase 7.4 Login rate limiting
- [ ] Phase 7.5 Refresh token or token TTL policy
- [ ] Phase 8.1 Docker UI + full stack compose
- [ ] Phase 8.2 CI (`mvn verify` + UI build)
- [ ] Phase 8.3 Prometheus + request ID logging
- [ ] Phase 8.4 Dependency scanning
- [ ] Phase 8.5 Load tests (optional)
- [ ] Phase 9.1 Architecture doc
- [ ] Phase 9.2 OpenAPI / Postman artifacts
