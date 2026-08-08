# TM Auth Service

Authentication service for the Transport Management System (TMS). It authenticates users with email and password, issues short-lived JWT access tokens, and manages rotating refresh tokens.

## What it provides

- Stateless Spring Security authentication using BCrypt password hashes
- JWT access tokens containing the authenticated user's email and roles
- Refresh-token rotation: refresh tokens are stored as SHA-256 hashes and revoked when exchanged
- Refresh-token revocation on logout
- PostgreSQL persistence with Flyway migrations
- Health endpoint for deployment probes: `GET /actuator/health`
- Production integration points for Google Cloud SQL and Secret Manager

## Technology

- Java 21 and Spring Boot 3.5
- Spring Web, Spring Security, Spring Data JPA, and Actuator
- PostgreSQL and Flyway
- JJWT
- Maven (wrapper included)

## Run locally

### Prerequisites

- Java 21
- PostgreSQL running locally
- A database named `authdb` (or equivalent values supplied through environment variables)

Set the required JWT secret and, optionally, database settings. The secret must be sufficiently long for HMAC signing (use at least 32 random bytes).

```bash
export JWT_SECRET='replace-with-a-long-random-secret-at-least-32-bytes'
export DATABASE_HOST=localhost
export DATABASE_PORT=5432
export DATABASE_NAME=authdb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your-password
```

Start the service with the development profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The service listens on `http://localhost:8080` by default. Override this with `PORT`.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8080` | HTTP port |
| `JWT_SECRET` | — (required) | HMAC key used to sign access tokens |
| `REFRESH_TOKEN_EXPIRATION_DAYS` | `7` | Refresh-token lifetime in days |
| `DATABASE_HOST` | `localhost` | PostgreSQL host for the `dev` profile |
| `DATABASE_PORT` | `5432` | PostgreSQL port for the `dev` profile |
| `DATABASE_NAME` | `authdb` | PostgreSQL database for the `dev` profile |
| `DATABASE_USERNAME` | `rinugeorge` | PostgreSQL user for the `dev` profile |
| `DATABASE_PASSWORD` | empty | PostgreSQL password for the `dev` profile |
| `SPRING_SECURITY_LOG_LEVEL` | `DEBUG` in dev | Spring Security logging level |

The `prod` profile imports secrets from Google Secret Manager (`sm://`) and is configured for the Cloud SQL instance `tms-microservices-2026:us-central1:postgres`. Provide Google Cloud application credentials with access to the configured secrets before using it.

## API

All authentication routes are rooted at `/api/v1/auth` and are publicly accessible. Other routes require authentication.

### Login

`POST /api/v1/auth/login`

```json
{
  "email": "driver@example.com",
  "password": "your-password"
}
```

Successful response (`200 OK`):

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<refresh-token>"
}
```

Access tokens expire after 15 minutes.

### Refresh a session

`POST /api/v1/auth/refresh`

```json
{
  "refreshToken": "<refresh-token>"
}
```

Successful response (`200 OK`):

```json
{
  "jwtAccessToken": "<new-jwt>",
  "refreshToken": "<new-refresh-token>"
}
```

The submitted refresh token is revoked and replaced. Store the returned refresh token for subsequent requests.

### Logout

`POST /api/v1/auth/logout`

```json
{
  "refreshToken": "<refresh-token>"
}
```

Returns `204 No Content` and revokes the supplied refresh token.

### Health check

`GET /actuator/health`

## Build and test

```bash
./mvnw clean package
./mvnw test
```

## Container image

The supplied `Dockerfile` expects the packaged JAR in `target/`.

```bash
./mvnw clean package -DskipTests
docker build -t tm-auth-service .
docker run --rm -p 8080:8080 \
  -e JWT_SECRET='replace-with-a-long-random-secret-at-least-32-bytes' \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DATABASE_HOST=host.docker.internal \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=your-password \
  tm-auth-service
```

## Database note

Hibernate is configured to validate, rather than create or update, the database schema. Before relying on a fresh database, reconcile the Flyway migration in `src/main/resources/db/migration` with the current JPA entities (`User`, `Role`, and `RefreshToken`): the existing `V1__create_users_table.sql` uses an older column and key layout and does not create the `roles`, `user_roles`, or `refresh_tokens` tables required by the current model.

## Security notes

- Do not commit JWT secrets, database passwords, or Secret Manager exports.
- Refresh tokens are only returned once in raw form; the service stores a hash instead.
- Use HTTPS and a securely generated production JWT secret.
