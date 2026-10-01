# Backend API Guide - Complete Documentation

## 📍 Quick Navigation
- **Get Started**: [Running the API](#running-the-api)
- **Endpoints**: [API Endpoints](#api-endpoints)
- **Authentication**: [Authentication](#authentication)
- **Pagination**: [Pagination](#pagination)
- **Troubleshooting**: [Common Issues](#troubleshooting)

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Gradle build tool
- PostgreSQL 12+ 
- Port 8080 available

### Running the API

```bash
# Navigate to API directory
cd C:\Users\mksan\git\inventory\InventoryApi

# Build the project (skips tests for speed)
./gradlew clean build -x test

# Start the application
./gradlew bootRun
```

**Expected Output:**
```
2026-04-22T00:27:00...  INFO  Started InventoryApiApplication in XX.XXX seconds
Server started on port 8080
```

### Accessing the API
- **Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

---

## ✅ Configuration Status

### Cache Configuration
- **Status**: ✅ DISABLED for development
- **Setting**: `spring.cache.type=none`
- **Redis**: Not required (optional for production)
- **Impact**: No Redis connection errors on startup

### Database Configuration
- **Type**: PostgreSQL
- **Host**: localhost
- **Port**: 5432
- **Database**: inventory_db
- **Schema**: sam
- **Status**: Update on startup (Hibernate DDL)

---

## 🏗️ Architecture Overview

```
PostgreSQL Database (Port 5432)
    ↑
    │ JDBC
    ↓
Hibernate ORM (Entity Mapping)
    ↑
    │
Spring Data JPA (CRUD Operations)
    ↑
    │
Repository Layer (Database Access)
    ↑
    │
Service Layer (Business Logic & Validation)
    ↑
    │
REST Controllers (HTTP Endpoints)
    ↑
    │
Spring Security (JWT Authentication)
    ↑
    │
Client (React Frontend / cURL / Postman)
```

---

## 📂 Project Structure

```
InventoryApi/
├── src/main/java/org/sam/inventory/
│   ├── config/
│   │   ├── SecurityConfig.java       (JWT & CORS config)
│   │   ├── CacheConfig.java          (Cache manager setup)
│   │   ├── WebConfig.java            (Web configuration)
│   │   └── AppConfig.java            (Application beans)
│   ├── controller/
│   │   ├── ProductController.java    (Product endpoints)
│   │   ├── PurchaseController.java   (Purchase endpoints)
│   │   ├── SaleController.java       (Sale endpoints)
│   │   ├── AuthController.java       (Authentication endpoints)
│   │   └── UserController.java       (User management)
│   ├── service/
│   │   ├── ProductService.java       (Product business logic)
│   │   ├── PurchaseService.java      (Purchase business logic)
│   │   ├── SaleService.java          (Sale business logic)
│   │   └── AuthenticationService.java (Auth logic)
│   ├── repository/
│   │   ├── ProductRepository.java    (Product DB access)
│   │   ├── PurchaseRepository.java   (Purchase DB access)
│   │   ├── SaleRepository.java       (Sale DB access)
│   │   └── UserRepository.java       (User DB access)
│   ├── entity/
│   │   ├── Product.java              (Product entity)
│   │   ├── Purchase.java             (Purchase entity)
│   │   ├── Sale.java                 (Sale entity)
│   │   ├── User.java                 (User entity)
│   │   └── Role.java                 (Role entity)
│   ├── dto/
│   │   ├── ProductDto.java           (Product DTO)
│   │   ├── PurchaseDto.java          (Purchase DTO)
│   │   ├── SaleDto.java              (Sale DTO)
│   │   ├── LoginRequest.java         (Login request)
│   │   └── LoginResponse.java        (Login response)
│   └── util/
│       └── SecurityUtil.java         (Security utilities)
├── src/main/resources/
│   ├── application.properties        (Configuration)
│   ├── db/migration/                 (Flyway migrations)
│   └── static/                       (Static files)
└── build.gradle                      (Dependencies)
```

---

## 🔐 Authentication

### JWT Token-Based Authentication

All endpoints (except `/auth/login` and `/auth/register`) require a valid JWT token.

### Login

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
{
  "email": "admin@example.com",
  "password": "encrypted_password_here",
  "encrypted": true
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@example.com",
  "fullName": "Admin User",
  "roles": ["ADMIN"]
}
```

### Using the Token

Include the token in all subsequent requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Available Roles
- **ADMIN**: Full access to all endpoints
- **MANAGER**: Access to product and sale management
- **USER**: Read-only access

---

## 📊 Pagination

All list endpoints return paginated data from the **first 20 records** on initial load.

### How Pagination Works

**Frontend**:
1. Fetch first 20 records
2. User scrolls to bottom
3. InfiniteScroll component detects scroll
4. Fetch next 20 records
5. Append to existing list
6. Repeat until all records loaded

**Benefits**:
- ✅ Better performance (load data incrementally)
- ✅ Reduced memory usage (don't load all records at once)
- ✅ Smooth user experience (continuous scrolling)
- ✅ Works great on mobile devices

### Pagination Configuration

- **Page Size**: 20 records per page
- **Load Trigger**: When user scrolls to end of table
- **Loading Indicator**: Spinner shown while loading

---

## 📡 API Endpoints

### Products

#### Get All Products (Paginated)
- **Method**: `GET`
- **Endpoint**: `/api/products`
- **Auth**: Required
- **Response**: `[Product, ...]`

```bash
curl -H "Authorization: Bearer TOKEN" http://localhost:8080/api/products
```

#### Get Product by ID
- **Method**: `GET`
- **Endpoint**: `/api/products/{id}`
- **Auth**: Required
- **Response**: `Product`

#### Create Product
- **Method**: `POST`
- **Endpoint**: `/api/products`
- **Auth**: Required (ADMIN/MANAGER)
- **Body**: `ProductDto`

#### Update Product
- **Method**: `PUT`
- **Endpoint**: `/api/products/{id}`
- **Auth**: Required (ADMIN/MANAGER)
- **Body**: `ProductDto`

#### Delete Product
- **Method**: `DELETE`
- **Endpoint**: `/api/products/{id}`
- **Auth**: Required (ADMIN only)

#### Get Low Stock Products
- **Method**: `GET`
- **Endpoint**: `/api/products/low-stock`
- **Auth**: Required
- **Response**: `[Product, ...]`

#### Get Expired Products
- **Method**: `GET`
- **Endpoint**: `/api/products/expired`
- **Auth**: Required
- **Response**: `[Product, ...]`

#### Get Total Inventory Value
- **Method**: `GET`
- **Endpoint**: `/api/products/total-value`
- **Auth**: Required
- **Response**: `{ "value": 50000.00 }`

---

### Purchases

#### Get All Purchases (Paginated)
- **Method**: `GET`
- **Endpoint**: `/api/purchases`
- **Auth**: Required
- **Response**: `[Purchase, ...]`

#### Create Purchase
- **Method**: `POST`
- **Endpoint**: `/api/purchases`
- **Auth**: Required (ADMIN/MANAGER)
- **Body**: `PurchaseDto`

#### Update Purchase
- **Method**: `PUT`
- **Endpoint**: `/api/purchases/{id}`
- **Auth**: Required (ADMIN/MANAGER)
- **Body**: `PurchaseDto`

#### Delete Purchase
- **Method**: `DELETE`
- **Endpoint**: `/api/purchases/{id}`
- **Auth**: Required (ADMIN only)

---

### Sales

#### Get All Sales (Paginated)
- **Method**: `GET`
- **Endpoint**: `/api/sales`
- **Auth**: Required
- **Response**: `[Sale, ...]`

#### Create Sale
- **Method**: `POST`
- **Endpoint**: `/api/sales`
- **Auth**: Required (ADMIN/MANAGER)
- **Body**: `SaleDto`

#### Update Sale
- **Method**: `PUT`
- **Endpoint**: `/api/sales/{id}`
- **Auth**: Required (ADMIN/MANAGER)
- **Body**: `SaleDto`

#### Delete Sale
- **Method**: `DELETE`
- **Endpoint**: `/api/sales/{id}`
- **Auth**: Required (ADMIN only)

---

### Authentication

#### Login
- **Method**: `POST`
- **Endpoint**: `/api/auth/login`
- **Auth**: Not required
- **Body**: `{ email, password, encrypted }`
- **Response**: `{ token, refreshToken, email, fullName, roles }`

#### Register
- **Method**: `POST`
- **Endpoint**: `/api/auth/register`
- **Auth**: Not required
- **Body**: `{ email, password, fullName }`
- **Response**: `{ message: "User registered successfully" }`

#### Change Password
- **Method**: `POST`
- **Endpoint**: `/api/auth/change-password`
- **Auth**: Required
- **Body**: `{ email, currentPassword, newPassword, encrypted }`

---

## 🔍 Common Response Formats

### Success Response
```json
{
  "id": 1,
  "name": "Product Name",
  "sku": "SKU-001",
  ...
}
```

### Error Response
```json
{
  "timestamp": "2026-04-22T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Field validation failed",
  "path": "/api/products"
}
```

### List Response
```json
{
  "content": [
    { "id": 1, "name": "Product 1", ... },
    { "id": 2, "name": "Product 2", ... },
    ...
  ],
  "pageable": {
    "sort": { "sorted": false, "unsorted": true },
    "offset": 0,
    "pageSize": 20,
    "pageNumber": 0
  },
  "totalPages": 5,
  "totalElements": 100,
  "last": false,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false, "unsorted": true },
  "numberOfElements": 20,
  "first": true,
  "empty": false
}
```

---

## 🚨 HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Successful request |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Validation error |
| 401 | Unauthorized | Missing/invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Internal error |

---

## 🐛 Troubleshooting

### Backend Won't Start

**Error**: `Port 8080 already in use`
```bash
# Find process using port 8080
netstat -ano | findstr ":8080"

# Kill the process
taskkill /PID <PID> /F
```

**Error**: `Unable to connect to database`
```bash
# Verify PostgreSQL is running and accessible
psql -h localhost -U sandeep -d inventory_db
```

### API Returns 400 Bad Request

**Possible Causes**:
1. Invalid request body
2. Validation error on entity
3. Missing required fields

**Solution**: Check error message in response body

### JWT Token Errors

**Error**: `Unauthorized - Token invalid`
```
Solution: Obtain new token by logging in
```

**Error**: `Forbidden - Insufficient permissions`
```
Solution: Ensure user has required role (ADMIN/MANAGER)
```

---

## 📝 Testing the API

### Using cURL

```bash
# Get all products
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/products

# Create product
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productDescription":"Test","sku":"SKU-001",...}'
```

### Using Postman

1. Open Postman
2. Create new request
3. Set Authorization type to "Bearer Token"
4. Paste your JWT token
5. Set method and endpoint
6. Send request

---

## 📚 Related Documentation

- **UI Guide**: See `UI_GUIDE.md` for frontend documentation
- **Setup Guide**: See project README for initial setup
- **Configuration**: See `application.properties` for all settings

---

**Last Updated**: April 22, 2026  
**Status**: ✅ Production Ready  
**Redis**: ✅ Disabled (Optional for production)  
**Pagination**: ✅ Implemented (20 records per page)
