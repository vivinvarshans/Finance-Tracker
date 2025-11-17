# Finance Tracker - Spring Boot Backend

## Overview

This is a **professionally architected RESTful API** built with **Spring Boot** following industry best practices. The backend implements the **MVC pattern** with clear separation of concerns across three core modules:

1. **Authentication Module** - User registration, login, and JWT-based authentication
2. **Transaction Management Module** - CRUD operations for financial transactions
3. **Budget Analysis Module** - Budget tracking and financial analytics

## 🏗️ Architecture

### Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Authentication**: JSON Web Tokens (JWT)

### Project Structure

```
backend/
├── src/main/java/com/finance/tracker/
│   ├── FinanceTrackerApplication.java          # Main application class
│   ├── config/
│   │   └── SecurityConfig.java                 # Security & CORS configuration
│   ├── controller/                             # REST Controllers (API Layer)
│   │   ├── AuthController.java                 # Authentication endpoints
│   │   ├── TransactionController.java          # Transaction CRUD endpoints
│   │   ├── BudgetController.java               # Budget management endpoints
│   │   └── AnalyticsController.java            # Analytics endpoints
│   ├── service/                                # Service Layer (Business Logic)
│   │   ├── AuthService.java                    # Authentication logic
│   │   ├── TransactionService.java             # Transaction management
│   │   ├── BudgetService.java                  # Budget operations
│   │   └── AnalyticsService.java               # Analytics calculations
│   ├── repository/                             # Data Access Layer (Spring Data JPA)
│   │   ├── UserRepository.java
│   │   ├── TransactionRepository.java
│   │   ├── BudgetRepository.java
│   │   ├── GoalRepository.java
│   │   └── CustomCategoryRepository.java
│   ├── model/                                  # JPA Entities
│   │   ├── User.java
│   │   ├── Transaction.java
│   │   ├── Budget.java
│   │   ├── Goal.java
│   │   └── CustomCategory.java
│   ├── dto/                                    # Data Transfer Objects
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── TransactionRequest.java
│   │   ├── TransactionResponse.java
│   │   ├── BudgetRequest.java
│   │   ├── BudgetResponse.java
│   │   └── ...
│   ├── exception/                              # Exception Handling
│   │   ├── GlobalExceptionHandler.java         # Global exception handler
│   │   ├── ResourceNotFoundException.java
│   │   ├── AuthenticationException.java
│   │   └── ErrorResponse.java
│   ├── security/
│   │   └── JwtAuthenticationFilter.java        # JWT filter
│   └── util/
│       └── JwtUtil.java                        # JWT utility methods
├── src/main/resources/
│   └── application.properties                  # Configuration
└── pom.xml                                     # Maven dependencies
```

## 🎯 Key Features

### 1. **MVC Pattern Implementation**
- **Model**: JPA entities with proper relationships and validation
- **View**: JSON responses via REST API
- **Controller**: REST controllers handling HTTP requests

### 2. **Three Core Modules**

#### Authentication Module
- User registration with validation
- Secure login with JWT token generation
- Password encryption using BCrypt
- User profile management

#### Transaction Management Module
- Create, Read, Update, Delete (CRUD) operations
- Transaction categorization (Income/Expense)
- Date-based filtering
- Automatic budget updates

#### Budget Analysis Module
- Budget creation and tracking
- Monthly budget monitoring
- Category-wise expense analytics
- Dashboard statistics
- Monthly expense trends

### 3. **Spring Data JPA for ORM**
- Automatic query generation
- Custom query methods
- Database relationship management
- Transaction management
- Optimized database operations

### 4. **Proper Exception Handling**
- Global exception handler with `@RestControllerAdvice`
- Custom exceptions (ResourceNotFoundException, etc.)
- Consistent error response format
- Validation error handling
- HTTP status code management

### 5. **Service Layer Validation**
- Input validation using Jakarta Validation
- Business logic validation
- Data integrity checks
- Authorization verification

## 📡 API Endpoints

### Authentication Endpoints

```
POST   /api/auth/register      # Register new user
POST   /api/auth/login         # Login user
POST   /api/auth/logout        # Logout user
GET    /api/auth/profile       # Get user profile
```

### Transaction Endpoints

```
GET    /api/transactions       # Get all transactions
GET    /api/transactions/{id}  # Get transaction by ID
POST   /api/transactions       # Create transaction
PUT    /api/transactions/{id}  # Update transaction
DELETE /api/transactions/{id}  # Delete transaction
```

### Budget Endpoints

```
GET    /api/budgets                            # Get all budgets
GET    /api/budgets/current                    # Get current month budgets
GET    /api/budgets/month/{month}/year/{year}  # Get budgets for specific month
GET    /api/budgets/{id}                       # Get budget by ID
POST   /api/budgets                            # Create/update budget
PUT    /api/budgets/{id}                       # Update budget
DELETE /api/budgets/{id}                       # Delete budget
```

### Analytics Endpoints

```
GET    /api/analytics/stats                              # Dashboard statistics
GET    /api/analytics/categories                         # Current month category analytics
GET    /api/analytics/categories/month/{month}/year/{year}  # Category analytics for specific month
GET    /api/analytics/monthly/expenses                   # Monthly expense trends
GET    /api/analytics/monthly/income                     # Monthly income trends
```

## 🚀 Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### 1. Clone and Navigate

```bash
cd backend
```

### 2. Configure Database

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_tracker
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
```

### 3. Configure JWT Secret

Update the JWT secret (must be at least 256 bits):

```properties
jwt.secret=your-secure-secret-key-at-least-256-bits-long
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/finance-tracker-1.0.0.jar
```

The API will be available at: `http://localhost:8080/api`

## 🔐 Authentication

The API uses JWT (JSON Web Token) for authentication.

### How to Authenticate

1. **Register** or **Login** to get a JWT token
2. Include the token in the `Authorization` header for subsequent requests:

```
Authorization: Bearer <your-jwt-token>
```

### Example Request

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'

# Use token in subsequent requests
curl -X GET http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 📊 Database Schema

### Tables

- **users** - User accounts
- **transactions** - Financial transactions
- **budgets** - Monthly budgets
- **goals** - Financial goals
- **custom_categories** - User-defined categories

### Relationships

- User → Transactions (One-to-Many)
- User → Budgets (One-to-Many)
- User → Goals (One-to-Many)
- User → CustomCategories (One-to-Many)

## 🧪 Testing

Run tests:

```bash
mvn test
```

## 📝 Code Quality Features

### 1. Lombok Integration
- Reduces boilerplate code
- `@Data`, `@Builder`, `@RequiredArgsConstructor` annotations
- Automatic getter/setter generation

### 2. Logging
- SLF4J with Logback
- Request/response logging
- Error logging
- Debug information

### 3. Validation
- Jakarta Validation annotations
- `@NotBlank`, `@Email`, `@Min`, `@Max`
- Custom validation messages

### 4. Transaction Management
- `@Transactional` for data consistency
- Automatic rollback on errors
- Read-only optimization

## 🔧 Configuration

### Environment Variables

You can override properties using environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/finance_tracker
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-secret-key
export CORS_ORIGINS=http://localhost:3000
```

### Application Profiles

Create `application-dev.properties` or `application-prod.properties` for different environments.

## 🌐 CORS Configuration

The backend is configured to accept requests from `http://localhost:3000` (Next.js frontend).

To change this, update `application.properties`:

```properties
cors.allowed-origins=http://your-frontend-url
```

## 🐳 Docker Support (Optional)

Create a `Dockerfile` in the backend directory:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
mvn clean package
docker build -t finance-tracker-api .
docker run -p 8080:8080 finance-tracker-api
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)

## 🤝 Integration with Frontend

Update your Next.js frontend to point to this backend:

1. Change API base URL to: `http://localhost:8080/api`
2. Use JWT token from login response
3. Include `Authorization: Bearer <token>` in all authenticated requests

## 📄 License

This project is part of the Finance Tracker application.

---

**Built with Spring Boot - Following Enterprise-Grade Best Practices** ✨
