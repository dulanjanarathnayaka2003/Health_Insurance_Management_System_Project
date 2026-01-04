<!-- Copilot instructions for the Healthinsureonline project -->
# Healthinsureonline — AI coding assistant guidance

This file helps AI agents (Copilot-style) become immediately productive in this repository. It documents the project's architecture, developer workflows, important files, and project-specific conventions discovered in the codebase.

## Quick summary
- Stack: Spring Boot (Java 21), Maven, Spring Data JPA, Spring Security, Thymeleaf/static HTML, MySQL (dev config).  
- App entry: `src/main/java/com/sliit/healthins/HealthinsureonlineApplication.java`.  
- Static frontend: `src/main/resources/static/` (several role-specific HTML pages).  
- DB: top-level `health_ins_db.sql` (schema) and Flyway-like SQL in `src/main/resources/db/migration/`.

## Build & run (developer) — Windows PowerShell
- Build and run tests: `.
  .\mvnw.cmd clean package`  
- Run app (dev): `.
  .\mvnw.cmd spring-boot:run`  
- Run tests only: `.
  .\mvnw.cmd test`  
- Run packaged jar: `java -jar target\healthinsureonline-0.0.1-SNAPSHOT.jar`

Notes: use the included `mvnw.cmd` on Windows to respect the project's Maven wrapper. When scripting in PowerShell, run commands from the repository root.

## Important files & where to look (examples)
- Main app: `src/main/java/com/sliit/healthins/HealthinsureonlineApplication.java` — custom Tomcat properties and startup logging.  
- Configs: `src/main/java/com/sliit/healthins/config/` — `SecurityConfig`, `DatabaseConfig`, `EmailConfig`, `SystemConfig`, etc.  
- Services: `src/main/java/com/sliit/healthins/service/*` — business logic lives here (e.g., `ClaimsService`, `AdminService`).  
- Repositories: `src/main/java/com/sliit/healthins/repository/*` — Spring Data JPA interfaces (e.g., `UserRepository`, `PolicyRepository`).  
- Models/DTOs: `src/main/java/com/sliit/healthins/model` and `dto`.  
- Static UI: `src/main/resources/static/` — login, dashboards, portal pages.  
- App properties: `src/main/resources/application.properties` — contains DB, mail, and security defaults (sensitive values present in repo; treat carefully).

## Architecture notes (big picture)
- Typical Spring Boot layered architecture: controllers → services → repositories → JPA entities.  
- Static frontend is served via Spring Boot `static/` resources (no separate SPA build in repo).  
- Email sending and PDF generation are implemented as Spring `@Component`s (`EmailSenderUtil`, `PdfGeneratorUtil`).  
- Scheduling is enabled (`@EnableScheduling`) and some components rely on scheduled jobs.  
- Startup uses programmatic listeners for logging and context events (see `HealthinsureonlineApplication`).

## Data + migrations
- Primary schema script: `health_ins_db.sql`.  
- There is a Flyway-style migration under `src/main/resources/db/migration/V1__Fix_SystemConfig_Table.sql` — check this if schema discrepancies appear.  
- Default `application.properties` points to a MySQL DB (`jdbc:mysql://localhost:3306/health_ins_db`) — tests may rely on an in-memory DB if configured; currently the repo sets MySQL as default.

## Conventions & patterns specific to this repo
- Package layout: `com.sliit.healthins.{config,controller,dto,exception,model,pattern,repository,service,util}` — follow this when adding files.  
- Services are annotated with `@Service`, repositories with `@Repository`, and utilities with `@Component` (see `util/*`).  
- Lombok is used (dependency present) — ensure the Lombok plugin/annotation processing is enabled in the environment.  
- Logging: class-level logging follows Spring Boot config; `application.properties` sets `com.sliit.healthins` to DEBUG for local troubleshooting.

## Security & secrets
- `application.properties` in repo contains example credentials (DB and mail). Treat these as development/test values — do NOT hardcode real secrets. Prefer overriding via environment variables or an external config.  
- JWT is referenced (`jwt.secret` and `jwt.expiration.ms`) — check `SecurityConfig` and `AuthService` when modifying auth flows.

## Tests & CI hints
- Tests live under `src/test/java/com/sliit/healthins/`. Use `.
  .\mvnw.cmd test` to run them.  
- There is no repository CI config detected here; if adding CI, run `mvn -B -DskipTests=false test` and package on successful tests.

## Common tasks & examples for AI edits
- Add a new REST endpoint: create controller under `controller/`, add service in `service/`, interface in `repository/` if DB needed, then add DTOs in `dto/`. Follow method naming patterns in existing controllers.  
- Add DB-backed feature: add JPA `@Entity` to `model/`, repository interface to `repository/`, and migration SQL under `src/main/resources/db/migration/` if schema changes are required.  
- Update email templates or SMTP settings: check `EmailConfig` and `application.properties`; tests may mock mail sending via utilities.

## File examples to reference in PRs
- Main class: `src/main/java/com/sliit/healthins/HealthinsureonlineApplication.java`  
- Example service: `src/main/java/com/sliit/healthins/service/ClaimsService.java`  
- Example repository: `src/main/java/com/sliit/healthins/repository/ClaimRepository.java`  
- Migration: `src/main/resources/db/migration/V1__Fix_SystemConfig_Table.sql`  
- Static UI: `src/main/resources/static/login.html`

## Safety checks for automated changes
- Never commit production secrets. If making updates that touch `application.properties`, prefer to add placeholders and document env var overrides.  
- When changing DB entities, add a migration script and ensure JPA `ddl-auto` settings are consistent with the intended workflow (`update` is used in the repo).  
- Keep API surface backward-compatible: avoid renaming controller routes without versioning or redirects to avoid breaking static pages that reference endpoints.

## What I couldn't auto-discover (ask user)
- Which environment should be used for integration tests (local MySQL or an in-memory DB)?  
- Any CI/CD conventions or branch policies (PR templates, required checks)?  
- Are there secret management guidelines (Vault, env vars) to follow for deployments?

---
If you'd like, I can: (a) commit this file, (b) add a short PR template, or (c) generate a CONTRIBUTING checklist for dev setup. Which would you prefer next? 
