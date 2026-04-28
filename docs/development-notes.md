# Development Notes

This file preserves the original project notes in a cleaned, readable form.

## 2026-04-14

- Connected the project to MySQL.
- Verified basic user insertion and querying.
- Enabled scheduling with `@EnableScheduling`.
- Enabled async execution with `@EnableAsync`.

### Notes

- `@Mapper` requires the MyBatis starter dependency.
- Lombok annotations such as `@Data` and `@RequiredArgsConstructor` reduce boilerplate.
- When the database auto-generates `id`, the Java field should be `Integer` instead of `int` so it can remain `null` before insert.
- If timestamp columns are auto-filled by the database, insert SQL should not try to write them explicitly.
- Use `@RequestParam` for simple query parameters and `@RequestBody` for JSON request payloads.

## 2026-04-15

- Added update-by-id and delete-by-id user features.
- Added registration and login.
- Added BCrypt password hashing before persistence.
- Added `LoginRequest` DTO for login payload validation.
- Registration now enforces non-empty username and password.
- Registration checks uniqueness for username, email, and phone.
- Registration defaults new users to `USER` identity and `true` status.

### Notes

- Uniqueness checks are implemented with `COUNT(*)`.
- `Boolean` is used instead of primitive `boolean` where nullability matters.
- `StringUtils.hasText(...)` is used for non-empty text validation.
- The standard API response shape is `code`, `message`, and `data`.
