# Spring SDD Example

A Spring Boot REST API application demonstrating domain-driven design principles with PostgreSQL database, JPA/Hibernate ORM, and comprehensive testing. Features user management with soft-delete functionality and follows modern Spring Boot best practices.

## Technology Stack

- **Framework:** Spring Boot 3.2.1
- **Java Version:** 17
- **Database:** PostgreSQL 15
- **ORM:** JPA/Hibernate with Spring Data
- **Migration:** Flyway Database Migration
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose
- **Additional Libraries:** 
  - Lombok for boilerplate reduction
  - ModelMapper 3.1.1 for object mapping
  - Spring HATEOAS for REST API enhancement

## Project Structure

```
src/main/java/com/example/springsddexample/
├── SpringSddExampleApplication.java    # Main application entry point
├── config/                             # Spring configuration classes
│   ├── ModelMapperConfig.java          # Object mapping configuration
│   └── RestTemplateConfig.java         # HTTP client configuration
├── controller/                         # REST API controllers
│   ├── UserController.java             # User management endpoints
│   └── ControllerErrorHandler.java     # Global exception handling
├── exception/                          # Custom exception classes
│   ├── UserAlreadyExistsException.java # Business logic exceptions
│   └── UserNotFoundException.java      
├── model/                              # Data models and DTOs
│   ├── assembler/                      # Entity-DTO conversion logic
│   ├── dto/                            # Data Transfer Objects
│   ├── entity/                         # JPA entities
│   │   ├── CommonEntity.java           # Base entity with audit fields
│   │   └── UserEntity.java             # User database entity
│   └── enums/                          # Status and type enumerations
├── repository/                         # Data access layer
│   └── UserRepository.java             # JPA repository interface
└── service/                            # Business logic layer
    ├── UserService.java                # Core user operations
    └── UserValidationService.java      # User validation logic

src/main/resources/
├── application.yml                     # Main application configuration
├── application-dev.yml                 # Development profile settings
├── application-test.yml                # Test profile settings
└── db/migration/                       # Flyway database migrations
    └── V1__Initial_schema.sql          # Initial database schema
```

## Key Features

- **User Management API:** Complete CRUD operations for user entities
- **Soft Delete:** Users are marked as deleted rather than removed from database
- **Database Migrations:** Automated schema management with Flyway
- **Comprehensive Testing:** Unit and integration tests with TestContainers
- **Error Handling:** Global exception handling with standardized error responses
- **Audit Fields:** Automatic timestamp tracking for entity lifecycle
- **Docker Support:** Full containerization with Docker Compose

## API Endpoints

The application runs on port `8091` and provides the following REST endpoints:

- **GET /users** - Retrieve all active users
- **GET /users/{id}** - Retrieve specific user by UUID
- **POST /users** - Create new user
- **PUT /users/{id}** - Update existing user
- **PATCH /users/{id}** - Partially update user
- **DELETE /users/{id}** - Soft delete user (marks as deleted)

## Environment Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL 15 (or use Docker setup)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spring-sdd-example
   ```

2. **Start PostgreSQL database**
   ```bash
   docker-compose up -d postgres
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

   Or with specific profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Access the application**
   - Application: http://localhost:8091
   - Health Check: http://localhost:8091/actuator/health
   - Sample API: http://localhost:8091/users

### Database Configuration

- **URL:** `jdbc:postgresql://localhost:5433/sdd_example`
- **Username:** `sdd_user`
- **Password:** `sdd_password`
- **Host Port:** 5433 → Container Port: 5432

### Docker Deployment

Run the entire application stack with Docker:

```bash
# Start all services (database + application)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

## Development Guidelines

### Code Quality Standards

- **KISS Principle:** Keep solutions simple and readable
- **DRY:** Extract common logic to avoid repetition
- **Single Responsibility:** Each class should have one clear purpose
- **Fail Fast:** Validate inputs early with clear error messages

### Entity Conventions

- All entities extend `CommonEntity` for audit fields and soft delete
- Use `@SuperBuilder` from Lombok for flexible object creation
- Default status is `ACTIVE` for new entities
- Implement soft delete via status field rather than physical deletion

### Testing Strategy

```bash
# Run all tests
./mvnw test

# Run only integration tests
./mvnw test -Dtest="*IntegrationTest"

# Generate test coverage report
./mvnw test jacoco:report
```

### Build and Package

```bash
# Clean build
./mvnw clean compile

# Package application (includes tests)
./mvnw clean package

# Package without tests
./mvnw clean package -DskipTests
```

## Contributing

### Development Standards

- Follow existing code conventions and patterns
- Maintain minimum 85% test coverage for service layer
- Use descriptive commit messages with ticket references
- Ensure all tests pass before submitting changes
- Follow the established project structure and naming conventions

### Workflow

1. Create feature branch from main
2. Implement changes following coding standards
3. Add comprehensive unit and integration tests
4. Run full test suite and ensure passing
5. Submit pull request with detailed description

## Architecture Notes

This application demonstrates:

- **Domain-Driven Design:** Clear separation between entities, services, and controllers
- **Repository Pattern:** Data access abstraction with Spring Data JPA
- **Soft Delete Implementation:** Maintains data integrity while providing delete functionality  
- **Audit Trail:** Automatic tracking of entity creation and modification timestamps
- **Error Handling:** Centralized exception handling with consistent error responses
- **Configuration Management:** Profile-based configuration for different environments

The codebase serves as a reference implementation for Spring Boot applications following enterprise-grade patterns and practices.