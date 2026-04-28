# Developer Help

## Common Commands

Run the app:

```powershell
.\mvnw.cmd spring-boot:run
```

Run tests:

```powershell
.\mvnw.cmd test
```

Build the jar:

```powershell
.\mvnw.cmd clean package
```

## Local Access

- API: `http://localhost:10011`
- Admin page: `http://localhost:10011/admin.html`

## Configuration Checklist

- Make sure MySQL is running.
- Create the target database referenced by `application.yaml`.
- Set `DB_USERNAME` and `DB_PASSWORD` for local development.
- Confirm the `users` table exists before calling the API.

## Main Endpoints

- `GET /users`
- `GET /users/page?page=1&size=10`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`
- `POST /users/register`
- `POST /users/login`

See [Readme.md](Readme.md) for request examples and behavior details.
