# University Library API (Spring Boot) - v1

This project implements the **initial REST API** for a university library management system.

## Run locally

Requirements:
- Java 17+
- Maven 3.8+

From the project root:

```bash
mvn spring-boot:run
```

The API will be available at:
- `http://localhost:8080/api`

## Swagger UI (interactive testing)

Open:
- `http://localhost:8080/swagger-ui`

## H2 Database Console

Open:
- `http://localhost:8080/h2`

JDBC URL:
- `jdbc:h2:mem:librarydb`

## Default users

Seeded on startup:
- **Admin**: `admin / admin123`
- **Employee**: `employee / employee123`

## Quick test examples (curl)

### 1) Register student
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"sara","password":"123456","fullName":"Sara Ahmadi","studentNumber":"S1402"}'
```

### 2) Login (get JWT)
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"sara","password":"123456"}'
```

Copy the `token` value and use it as:
`Authorization: Bearer <TOKEN>`

### 3) Get books (public)
```bash
curl "http://localhost:8080/api/books?title=Java"
```

### 4) Borrow request (student)
```bash
curl -X POST "http://localhost:8080/api/borrow/request" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"bookId":1}'
```

### 5) Approve request (employee)
```bash
# login as employee first to get a token
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"employee","password":"employee123"}'

# approve request id=1
curl -X PUT "http://localhost:8080/api/borrow/requests/1/approve" \
  -H "Authorization: Bearer <EMPLOYEE_TOKEN>"
```

## Notes
- Responses are returned in a consistent JSON wrapper (`success`, `data`, `error`, `timestamp`, `path`).
- GET `/api/books/**` and GET `/api/stats/summary` are public (guest).
- Other endpoints require JWT authentication.
