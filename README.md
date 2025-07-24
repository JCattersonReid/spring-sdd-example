# Spring SDD Example

A Spring Boot REST API demonstrating domain-driven design principles with PostgreSQL, JPA/Hibernate, and comprehensive testing.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### Running the Application

1. **Start the database:**
   ```bash
   docker-compose up -d postgres
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the API:**
   - Application: http://localhost:8091
   - API Documentation: http://localhost:8091/swagger-ui.html
   - Health Check: http://localhost:8091/actuator/health

## 🏗️ Architecture

### Technology Stack
- **Framework:** Spring Boot 3.2.1
- **Java:** 17
- **Database:** PostgreSQL 15
- **ORM:** JPA/Hibernate
- **Migration:** Flyway
- **Testing:** JUnit 5, Mockito, TestContainers
- **Documentation:** OpenAPI 3 (Swagger)

### Project Structure
```
src/main/java/com/example/springsddexample/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── exception/       # Exception handling
├── model/
│   ├── assembler/   # Entity-DTO conversion
│   ├── dto/         # Data Transfer Objects
│   ├── entity/      # JPA entities
│   └── enums/       # Enumerations
├── repository/      # Data access layer
└── service/         # Business logic
```

## 🔧 Development

### Environment Profiles
- **Default:** Production-ready configuration
- **Dev:** `--spring.profiles.active=dev` - Enhanced logging
- **Test:** `--spring.profiles.active=test` - H2 in-memory database

### Running Tests
```bash
# All tests
./mvnw test

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"

# With coverage report
./mvnw test jacoco:report
```

### Database Management
```bash
# Clean build with fresh database
./mvnw clean compile

# Database migrations are handled automatically by Flyway
# Migration files: src/main/resources/db/migration/
```

## 📊 API Endpoints

### Users API
| Method | Endpoint        | Description      | Status Codes    |
|--------|----------------|------------------|-----------------|
| GET    | `/users`       | List all users   | 200             |
| GET    | `/users/{id}`  | Get user by ID   | 200, 404        |
| POST   | `/users`       | Create new user  | 201, 400, 409   |
| PUT    | `/users/{id}`  | Update user      | 200, 400, 404   |
| PATCH  | `/users/{id}`  | Partial update   | 200, 400, 404   |
| DELETE | `/users/{id}`  | Soft delete user | 204, 404        |

### Example Request/Response
```json
POST /users
{
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}

Response: 201 Created
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T12:00:00Z",
  "updatedAt": "2024-01-01T12:00:00Z",
  "_links": {
    "self": {
      "href": "http://localhost:8091/users/550e8400-e29b-41d4-a716-446655440000"
    }
  }
}
```

## 🗄️ Database

### Connection Details
- **Host:** localhost:5433
- **Database:** sdd_example
- **Username:** sdd_user
- **Password:** sdd_password

### Entity Design
All entities extend `CommonEntity` providing:
- UUID primary keys
- Automatic timestamp management (`createdAt`, `updatedAt`)
- Soft delete functionality via `status` field
- JPA lifecycle callbacks

### Soft Delete Pattern
- Entities are never physically deleted
- DELETE operations set `status = 'DELETED'`
- GET operations only return `status = 'ACTIVE'` entities

## 🧪 Testing

### Test Architecture
- **Unit Tests:** Service and validation logic (85% coverage minimum)
- **Integration Tests:** Full API testing with TestContainers
- **Test Utilities:** Standardized test data creation

### Test Data Creation
```java
// Using test utilities for consistent data
User testUser = UserTestUtils.createActiveUser();
UserEntity testEntity = UserTestUtils.createActiveUserEntity();

// Custom variations
User customUser = UserTestUtils.createUserWithCustomEmail("custom@example.com");
```

## 🚢 Deployment

### Docker
```bash
# Full application stack
docker-compose up -d

# Production build
./mvnw clean package -DskipTests
docker build -t spring-sdd-example .
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Environment profile (dev, test, prod)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## 🔍 Monitoring

### Health Checks
- Application health: `/actuator/health`
- Database connectivity included in health checks
- Ready for production monitoring integration

### Logging
- Structured logging with correlation IDs
- Environment-specific log levels
- Database query logging in development

## 📝 Development Standards

### Code Conventions
- **Entities:** Extend `CommonEntity`, use `@SuperBuilder`
- **DTOs:** Extend `CommonModel`, use `@SuperBuilder`
- **Tests:** Descriptive names, Given-When-Then structure
- **API:** RESTful design, proper HTTP status codes

### Architecture Patterns
- Repository pattern for data access
- Service layer for business logic
- Assembler pattern for entity-DTO conversion
- Global exception handling with `@RestControllerAdvice`

## 🤝 Contributing

1. Follow the coding standards outlined in `CLAUDE.md`
2. Ensure minimum 85% test coverage for service layer
3. Add integration tests for new endpoints
4. Update API documentation for schema changes
5. Run full test suite before committing

## 📄 License

This project is a development example demonstrating Spring Boot best practices.