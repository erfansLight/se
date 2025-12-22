# University Library Management System (Initial) — RESTful API v1

> **Base path:** `/api`  \
> **Response format:** JSON (all endpoints)

این فایل شامل **طراحی نسخه اولیه REST API** برای سیستم مدیریت کتابخانه دانشگاه است.
همه مسیرها با `/api` شروع می‌شوند و پاسخ‌ها JSON هستند.

### لیست Endpoint ها (طبق تمرین)

**۱) احراز هویت (Authentication)**

- `POST /api/auth/register` ثبت‌نام دانشجو
- `POST /api/auth/login` ورود (همه کاربران)
- `POST /api/auth/change-password` تغییر رمزعبور (کارمند/مدیر)

**۲) کتاب‌ها (Books)**

- `GET /api/books` لیست کتاب‌ها (با فیلتر Query Params)
- `GET /api/books/{id}` جزئیات یک کتاب
- `POST /api/books` ایجاد کتاب جدید (کارمند)
- `PUT /api/books/{id}` بروزرسانی کتاب (کارمند)
- `GET /api/books/search` جستجوی پیشرفته (عنوان/نویسنده/سال)

**۳) امانت (Borrowing)**

- `POST /api/borrow/request` ثبت درخواست امانت (دانشجو)
- `GET /api/borrow/requests/pending` درخواست‌های در انتظار تایید (کارمند)
- `PUT|POST /api/borrow/requests/{id}/approve` تایید درخواست (کارمند)
- `PUT|POST /api/borrow/requests/{id}/reject` رد درخواست (کارمند)
- `PUT|POST /api/borrow/{id}/return` ثبت بازگرداندن (کارمند)

**۴) دانشجویان (Students)**

- `GET /api/students/{id}` پروفایل دانشجو
- `PUT|POST /api/students/{id}/status` فعال/غیرفعال کردن دانشجو (کارمند)
- `GET /api/students/{id}/borrow-history` تاریخچه امانت‌های دانشجو (کارمند)

**۵) گزارش‌ها و آمار (Reports & Statistics)**

- `GET /api/stats/summary` آمار خلاصه (مهمان/کارمند/مدیر)
- `GET /api/stats/borrows` آمار پیشرفته امانت‌ها (مدیر)
- `GET /api/stats/employees/{id}/performance` گزارش عملکرد کارمند (مدیر)
- `GET /api/stats/top-delayed` دانشجویان با بیشترین تاخیر (مدیر)

**۶) کارکنان (Employees) — فقط مدیر**

- `POST /api/admin/employees` ایجاد حساب کارمند جدید
- `GET /api/admin/employees` لیست کارکنان

---

Below is a more detailed specification (recommended JSON shapes, examples, and rules).

---

## 0) Conventions

### 0.1 Authorization & Roles

Roles (aligned with the project enum `UserRole`):

- `GUEST`
- `STUDENT`
- `EMPLOYEE`
- `ADMIN`

**Suggested auth in REST version (v1):**

- `POST /api/auth/login` returns an `accessToken`.
- Requests that require authentication should send:

```
Authorization: Bearer <accessToken>
```

> Note: your current Java project is a CLI and does not implement tokens yet. This is just the REST design.

### 0.2 Standard JSON wrapper (recommended)

All responses use one of these shapes:

**Success**

```json
{
  "success": true,
  "data": {},
  "meta": null
}
```

**Error**

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Human readable message",
    "details": {}
  }
}
```

### 0.3 Common HTTP status codes

- `200 OK` / `201 Created`
- `400 Bad Request` (validation)
- `401 Unauthorized` (bad/missing token)
- `403 Forbidden` (role not allowed)
- `404 Not Found`
- `409 Conflict` (business rule conflict)
- `422 Unprocessable Entity` (invalid business input like dates)

---

## 1) Authentication

### 1.1 Student Register

**POST** `/api/auth/register`

Request body:

```json
{
  "username": "sara",
  "password": "123456"
}
```

Response (`201`):

```json
{
  "success": true,
  "data": {
    "username": "sara",
    "role": "STUDENT",
    "active": true
  },
  "meta": null
}
```

Errors:

- `400` invalid body
- `409` username already exists

### 1.2 Login (all users)

**POST** `/api/auth/login`

Request body:

```json
{
  "username": "admin",
  "password": "admin"
}
```

Response (`200`):

```json
{
  "success": true,
  "data": {
    "accessToken": "<jwt-or-random-token>",
    "tokenType": "Bearer",
    "user": {
      "username": "admin",
      "role": "ADMIN",
      "active": true
    }
  },
  "meta": null
}
```

Errors:

- `401` invalid username/password
- `403` user exists but is inactive

### 1.3 Change Password (EMPLOYEE / ADMIN)

**POST** `/api/auth/change-password`

Allowed:

- `EMPLOYEE`: change **own** password
- `ADMIN`: change own password or reset others

Request body (employee self-change):

```json
{
  "oldPassword": "old",
  "newPassword": "new"
}
```

Request body (admin reset another user):

```json
{
  "targetUsername": "sara",
  "newPassword": "new"
}
```

Response (`200`):

```json
{ "success": true, "data": { "changed": true }, "meta": null }
```

---

## 2) Books

Book JSON:

```json
{
  "id": "B001",
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "year": 2018,
  "status": "AVAILABLE"
}
```

### 2.1 List books (search & filter)

**GET** `/api/books`

Use **Query Parameters**:

- `title` (contains)
- `author` (contains)
- `year` (exact)
- `status` (`AVAILABLE` / `BORROWED`) *(optional)*

Example:

`/api/books?title=Java&author=Bloch`

Response (`200`):

```json
{
  "success": true,
  "data": [
    { "id": "B001", "title": "Effective Java", "author": "Joshua Bloch", "year": 2018, "status": "AVAILABLE" }
  ],
  "meta": null
}
```

### 2.2 Get book details

**GET** `/api/books/{id}`

Response (`200`) = single Book object.

Errors:

- `404` book not found

### 2.3 Create new book (EMPLOYEE)

**POST** `/api/books`

Request body:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "year": 2008
}
```

Response (`201`): created Book.

### 2.4 Update book (EMPLOYEE)

**PUT** `/api/books/{id}`

Request body:

```json
{
  "title": "Clean Code (2nd edition)",
  "author": "Robert C. Martin",
  "year": 2025
}
```

Response (`200`): updated Book.

Errors:

- `404` book not found

### 2.5 Advanced search

**GET** `/api/books/search`

Query parameters (v1):

- `title`
- `author`
- `year`

Example:

`/api/books/search?title=Java&year=2018`

Response (`200`): list of books.

---

## 3) Borrowing

BorrowRequest JSON (aligned with `BorrowRequest` model):

```json
{
  "id": "R1",
  "studentUsername": "sara",
  "bookId": "B001",
  "from": "2025-12-22",
  "to": "2025-12-29",
  "status": "PENDING",
  "receivedAt": null
}
```

### 3.1 Create borrow request (STUDENT)

**POST** `/api/borrow/request`

Request body:

```json
{
  "bookId": "B001",
  "from": "2025-12-22",
  "to": "2025-12-29"
}
```

Response (`201`): created BorrowRequest.

Errors:

- `404` book not found
- `409` book already borrowed
- `403` student inactive

### 3.2 Pending requests (EMPLOYEE)

**GET** `/api/borrow/requests/pending`

Optional query params:

- `studentUsername`
- `bookId`

Response (`200`): list of pending BorrowRequest.

### 3.3 Approve / Reject request (EMPLOYEE)

**PUT** (or **POST**) `/api/borrow/requests/{id}/approve`

Rules (matches current Java logic):

- only `PENDING` can be approved
- request start date (`from`) must be **today or yesterday** (relative to approval date)
- book must be `AVAILABLE`

Optional request body (if you want server to use a provided approval date):

```json
{ "approvalDate": "2025-12-22" }
```

Response (`200`): updated BorrowRequest with status `APPROVED`.

**PUT** (or **POST**) `/api/borrow/requests/{id}/reject`

Optional request body:

```json
{ "reason": "No copies left" }
```

Response (`200`): updated BorrowRequest with status `REJECTED`.

### 3.4 Return book (EMPLOYEE)

**PUT** (or **POST**) `/api/borrow/{id}/return`

Request body:

```json
{ "receivedAt": "2025-12-29" }
```

Response (`200`): updated BorrowRequest (now with `receivedAt`) and the book becomes `AVAILABLE`.

Errors:

- `409` request not approved yet
- `409` already returned

---

## 4) Students

Student JSON (derived from `User`):

```json
{
  "username": "sara",
  "role": "STUDENT",
  "active": true
}
```

### 4.1 Get student profile

**GET** `/api/students/{id}`

Access policy (recommended):

- `STUDENT` can see **own** profile
- `EMPLOYEE` / `ADMIN` can see any student

### 4.2 Activate / Deactivate student (EMPLOYEE)

**PUT** (or **POST**) `/api/students/{id}/status`

Request body:

```json
{ "active": false }
```

Response (`200`): updated student.

### 4.3 Student borrow history (EMPLOYEE)

**GET** `/api/students/{id}/borrow-history`

Response (`200`): list of BorrowRequest for that student.

---

## 5) Reports & Statistics

### 5.1 Summary stats (GUEST / EMPLOYEE / ADMIN)

**GET** `/api/stats/summary`

Response (`200`):

```json
{
  "success": true,
  "data": {
    "students": 120,
    "books": 560,
    "borrowRequestsTotal": 80,
    "pending": 10,
    "approved": 60,
    "returned": 55
  },
  "meta": null
}
```

### 5.2 Advanced borrow stats (ADMIN)

**GET** `/api/stats/borrows`

Optional filters:

- `from` / `to` (date range)
- `status`

Response (`200`): aggregated borrow info.

### 5.3 Employee performance (ADMIN)

**GET** `/api/stats/employees/{id}/performance`

Response (`200`):

```json
{
  "success": true,
  "data": {
    "employeeUsername": "emp1",
    "approved": 12,
    "rejected": 3,
    "returnsProcessed": 9
  },
  "meta": null
}
```

### 5.4 Top delayed students (ADMIN)

**GET** `/api/stats/top-delayed`

Response (`200`):

```json
{
  "success": true,
  "data": [
    { "studentUsername": "sara", "delayDays": 7 },
    { "studentUsername": "ali", "delayDays": 4 }
  ],
  "meta": null
}
```

---

## 6) Employees (ADMIN only)

### 6.1 Create new employee

**POST** `/api/admin/employees`

Request body:

```json
{
  "username": "emp1",
  "password": "pass"
}
```

Response (`201`): created employee user.

### 6.2 List employees

**GET** `/api/admin/employees`

Response (`200`): list of employees.
