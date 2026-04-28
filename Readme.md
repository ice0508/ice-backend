# OU User Service

Spring Boot 4 + MyBatis user management service with a small admin page.

## Overview

This project provides:

- User CRUD APIs
- User registration and login
- BCrypt password hashing
- A simple admin page at `/admin.html`
- MySQL persistence through MyBatis

## Tech Stack

- Java 17
- Spring Boot 4.0.5
- Spring Web MVC
- MyBatis Spring Boot Starter
- MySQL Connector/J
- Lombok
- Spring Security Crypto

## Project Structure

```text
src/main/java/com/ou
|- controller    REST endpoints
|- mapper        MyBatis SQL mappers
|- pojo          request/response/domain objects
|- service       business logic

src/main/resources
|- application.yaml
|- application-example.yaml
|- static/admin.html
```

## Requirements

- Java 17
- Maven 3.9+ or the included Maven wrapper
- MySQL 8+

## Configuration

The app reads its database settings from `src/main/resources/application.yaml`.

Default runtime settings:

- Server port: `10011`
- Bind address: `::`
- Database URL: `jdbc:mysql://localhost:3306/rhm1`

For safer local setup, prefer environment variables instead of editing credentials directly:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
```

You can also use `src/main/resources/application-example.yaml` as a template for a clean local configuration.

## Run Locally

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Or package first:

```powershell
.\mvnw.cmd clean package
java -jar target\ou-0.0.1-SNAPSHOT.jar
```

After startup:

- API base URL: `http://localhost:10011`
- Admin page: `http://localhost:10011/admin.html`

## API Summary

All endpoints are under `/users`.

### Get all users

```http
GET /users
```

### Get paged users

```http
GET /users/page?page=1&size=10
```

Response data shape:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "records": []
  }
}
```

### Create user

```http
POST /users
Content-Type: application/json
```

Example body:

```json
{
  "username": "alice",
  "password_hash": "plain-text-password",
  "email": "alice@example.com",
  "phone": "13800000000",
  "identity": "USER",
  "status": true
}
```

Notes:

- The service hashes `password_hash` before storing it.
- `identity` accepts enum values such as `USER` and `ADMIN`.

### Update user

```http
PUT /users/{id}
Content-Type: application/json
```

Example body:

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "phone": "13800000000",
  "identity": "ADMIN",
  "status": true
}
```

Important:

- The current implementation only allows updates when the submitted `identity` is `ADMIN`.
- Password updates are not part of this endpoint.
- Username, email, and phone are checked for uniqueness before update.

### Delete user

```http
DELETE /users/{id}
```

### Register

```http
POST /users/register
Content-Type: application/json
```

Example body:

```json
{
  "username": "alice",
  "password_hash": "plain-text-password",
  "email": "alice@example.com",
  "phone": "13800000000"
}
```

Registration behavior:

- `identity` is forced to `USER`
- `status` defaults to `true` when omitted
- Username, email, and phone must be unique

### Login

```http
POST /users/login
Content-Type: application/json
```

Example body:

```json
{
  "account": "alice@example.com",
  "password": "plain-text-password"
}
```

Login supports either:

- `username`
- `email`

Successful login returns the user object with `password_hash` cleared.

## Response Format

The API uses a unified response wrapper:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

Common codes:

- `200` success
- `400` bad request
- `404` not found
- `500` server error

## Database Expectations

The mapper assumes a `users` table with fields matching the domain model:

- `id`
- `username`
- `password_hash`
- `email`
- `phone`
- `identity`
- `status`
- `created_at`
- `updated_at`

Recommended constraints:

- Primary key on `id`
- Unique indexes on `username`, `email`, and `phone`

## Admin Page

`src/main/resources/static/admin.html` provides a minimal Vue 3 + Element Plus page for:

- Listing users
- Paged browsing
- Deleting users

It calls the backend directly with `fetch()` and assumes the API is served from the same origin.

## Known Limitations

- No authentication or authorization guard protects admin operations.
- `PUT /users/{id}` uses the submitted `identity` field as the admin check, which is not secure.
- There is no schema migration file in the repository.
- The test suite currently contains only a Spring context smoke test.

## Development Notes

Earlier implementation notes were preserved in [docs/development-notes.md](docs/development-notes.md).
