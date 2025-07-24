# Finance Analyzer - Personal Finance Management API

A comprehensive Spring Boot REST API for personal finance management and analysis, featuring secure authentication, transaction tracking, and CSV data import capabilities.

## 🚀 Technology Stack

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 21
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT Authentication
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Build Tool**: Maven
- **Libraries**: Lombok, OpenCSV, JJWT

## 🏗️ Architecture & Design Patterns

- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separate request/response objects for API contracts
- **JWT Authentication**: Stateless authentication with token-based security
- **Global Exception Handling**: Centralized error management
- **Data Validation**: Bean validation with custom constraints

## 🔧 Key Features

### Authentication & Authorization

- User registration and login with JWT tokens
- Password encryption using BCrypt
- Role-based access control
- Secure endpoints with authentication

### Transaction Management

- CRUD operations for financial transactions
- Support for income and expense tracking
- Category-based transaction organization
- Date-based transaction filtering

### Data Import & Processing

- CSV file upload and processing
- Bulk transaction import from bank statements
- File upload history tracking
- Error handling for malformed data

### User Management

- User profile management
- Personal information updates
- Account security features

## 📋 API Endpoints

### Authentication

- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User authentication

### Transactions

- `GET /api/transactions` - List all transactions
- `GET /api/transaction/{id}` - Get specific transaction
- `POST /api/transaction` - Create new transaction
- `PUT /api/transaction/{id}` - Update transaction
- `DELETE /api/transaction/{id}` - Delete transaction

### Categories

- `GET /api/categories` - List transaction categories
- `POST /api/category` - Create new category
- `PUT /api/category/{id}` - Update category
- `DELETE /api/category/{id}` - Delete category

### File Upload

- `POST /api/files/upload` - Upload CSV file
- `GET /api/files` - List uploaded files
- `GET /api/files/{id}` - Get file details

### User Profile

- `GET /api/profile` - Get user profile
- `PUT /api/profile` - Update user profile

## 🛠️ Setup & Installation

### Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL 12+

### Configuration

1. Clone the repository
2. Configure database connection in `application.properties`
3. Update JWT secret and expiration settings
4. Run the application: `mvn spring-boot:run`

### Database Setup

```sql
-- Create database
CREATE DATABASE finance_analyzer;

-- Update application.properties with your database credentials
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_analyzer
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 📚 API Documentation

Access the interactive API documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🔒 Security Features

- JWT-based authentication
- Password encryption with BCrypt
- Input validation and sanitization
- CORS configuration
- Secure HTTP headers

## 📊 Data Models

### Core Entities

- **User**: Authentication and profile information
- **Transaction**: Financial transaction records
- **Category**: Transaction categorization
- **FileUpload**: CSV import tracking

### Enums

- **TransactionType**: INCOME, EXPENSE
- **TransactionSource**: MANUAL, CSV_IMPORT

## 🧪 Testing

The project includes comprehensive test coverage:

- Unit tests for services and controllers
- Integration tests for API endpoints
- Security tests for authentication flows

Run tests: `mvn test`

## 🚀 Deployment

### Build JAR

```bash
mvn clean package
```

### Run Application

```bash
java -jar target/finance-analyzer-0.0.1-SNAPSHOT.jar
```

## 📈 Performance Considerations

- Database indexing on frequently queried fields
- Pagination for large datasets
- Efficient JPA queries with proper relationships
- Connection pooling configuration

## 🔧 Development Tools

- **IDE Support**: Full IntelliJ IDEA/Eclipse compatibility
- **Hot Reload**: Spring Boot DevTools for development
- **Logging**: Structured logging with SLF4J
- **Code Quality**: Lombok for reducing boilerplate

## 📝 License

This project is developed as a portfolio piece demonstrating Spring Boot expertise and best practices in API development.
