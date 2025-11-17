# API Documentation

## Authentication API

### 1. Register User

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Registration successful",
  "user": {
    "id": "clx1234567890",
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

---

### 2. Login

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "user": {
    "id": "clx1234567890",
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

---

## Transaction API

### 1. Get All Transactions

**Endpoint:** `GET /api/transactions`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "id": "tx123",
    "amount": 1500.00,
    "description": "Salary",
    "category": "Income",
    "type": "INCOME",
    "date": "2025-11-01T00:00:00",
    "createdAt": "2025-11-01T10:00:00",
    "updatedAt": "2025-11-01T10:00:00"
  }
]
```

---

### 2. Create Transaction

**Endpoint:** `POST /api/transactions`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "amount": 50.00,
  "description": "Groceries",
  "category": "Food",
  "type": "EXPENSE",
  "date": "2025-11-15T14:30:00"
}
```

**Response:** `201 Created`

---

### 3. Update Transaction

**Endpoint:** `PUT /api/transactions/{id}`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "amount": 55.00,
  "description": "Groceries - Updated",
  "category": "Food",
  "type": "EXPENSE",
  "date": "2025-11-15T14:30:00"
}
```

**Response:** `200 OK`

---

### 4. Delete Transaction

**Endpoint:** `DELETE /api/transactions/{id}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `204 No Content`

---

## Budget API

### 1. Get Current Month Budgets

**Endpoint:** `GET /api/budgets/current`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "id": "budget123",
    "category": "Food",
    "amount": 500.00,
    "spent": 250.00,
    "remaining": 250.00,
    "percentageUsed": 50.0,
    "month": 11,
    "year": 2025
  }
]
```

---

### 2. Create/Update Budget

**Endpoint:** `POST /api/budgets`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "category": "Food",
  "amount": 600.00,
  "month": 11,
  "year": 2025
}
```

**Response:** `201 Created`

---

## Analytics API

### 1. Get Dashboard Stats

**Endpoint:** `GET /api/analytics/stats`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 3500.00,
  "balance": 1500.00,
  "monthlyIncome": 2000.00,
  "monthlyExpenses": 1500.00
}
```

---

### 2. Get Category Analytics

**Endpoint:** `GET /api/analytics/categories`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "category": "Food",
    "amount": 500.00,
    "count": 15,
    "percentage": 33.33
  },
  {
    "category": "Transport",
    "amount": 300.00,
    "count": 10,
    "percentage": 20.00
  }
]
```

---

### 3. Get Monthly Expense Analytics

**Endpoint:** `GET /api/analytics/monthly/expenses`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "month": "2025-11",
    "amount": 1500.00
  },
  {
    "month": "2025-10",
    "amount": 1800.00
  }
]
```

---

## Error Responses

All errors follow this format:

```json
{
  "timestamp": "2025-11-15T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Transaction not found with id : 'tx123'",
  "path": "/api/transactions/tx123"
}
```

### Common Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Resource deleted successfully
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Authentication required
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `500 Internal Server Error` - Server error
