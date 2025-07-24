# Spring SDD Example - Claude AI Context

## Project Overview
This is a Spring Boot 3.2.1 application with PostgreSQL database, demonstrating a REST API with JPA/Hibernate, Flyway migrations, and soft-delete functionality. The application follows domain-driven design principles with clear separation of concerns.

## Technology Stack
- **Framework:** Spring Boot 3.2.1
- **Java Version:** 17
- **Database:** PostgreSQL 15
- **ORM:** JPA/Hibernate
- **Migration:** Flyway
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose
- **Testing:** JUnit 5, TestContainers
- **Documentation:** OpenAPI 3 (Swagger)

## Project Structure
```
src/main/java/com/example/springsddexample/
├── config          # Configuration classes (Spring Boot config, beans, security)
├── controller      # REST controllers (API endpoints and request handling)
├── exception       # Exception handling (custom exceptions, global exception handlers)
├── model           # Data models and DTOs
│   ├── assembler   # Model assemblers (entity to DTO conversion logic)
│   ├── dto         # Data Transfer Objects (API request/response models)
│   ├── entity      # JPA entities (database table mappings)
│   └── enums       # Enumeration classes (constants and status types)
├── repository      # Data access layer (JPA repositories, database queries)
└── service         # Business logic layer (service classes and interfaces)

src/main/resources/
├── application.yml                   # Application configuration
├── application-dev.yml              # Development profile configuration
├── application-test.yml             # Test profile configuration
└── db/migration/
    ├── V1__Initial_schema.sql       # Initial database schema
    └── V2__Sample_data.sql          # Sample data insertion
```

## Architecture Patterns & Conventions

### Entity Design
- All entities extend `CommonEntity` abstract class
- Use `@SuperBuilder` from Lombok for builder pattern support
- Default status is `ACTIVE` for all new entities
- Soft delete implementation via status field
- Automatic timestamp management via JPA lifecycle callbacks

### API Design
- RESTful endpoints following resource-based naming
- Consistent HTTP status codes (200, 201, 404, 400, etc.)
- Request/response validation using Bean Validation
- Pagination support for list endpoints
- Error responses follow RFC 7807 Problem Details format

### Data Access Patterns
- Repository pattern with Spring Data JPA
- Custom query methods when needed
- Soft delete filtering at repository level
- Optimistic locking for concurrent updates

## Coding Standards

### General Principles
- **KISS Principle:** Keep It Simple Stupid - prefer simple, readable solutions
- **DRY:** Don't Repeat Yourself - extract common logic
- **Single Responsibility:** Each class should have one reason to change
- **Fail Fast:** Validate inputs early and provide clear error messages

### Naming Conventions
- **Variables/Methods:** camelCase (e.g., `userName`, `findActiveUsers()`)
- **Classes:** PascalCase (e.g., `UserService`, `CommonEntity`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_USERNAME_LENGTH`)
- **Packages:** lowercase (e.g., `controller`, `service`)

### Entity Rules
- Extend `CommonEntity` for audit fields and soft delete
- Use `@Builder.Default` for default status
- Implement equals/hashCode based on business keys
- Use appropriate JPA annotations for relationships

### API Endpoint Rules
- **GET endpoints:** Return only ACTIVE status entities
- **DELETE endpoints:** Soft delete (update status to DELETED)
- **POST/PUT endpoints:** Validate input and return appropriate status
- Use HTTP status codes correctly (201 for creation, 204 for no content)

### Test Conventions
- **Method Names:** camelCase and descriptive (e.g., `shouldReturnActiveUsersWhenStatusIsActive`)
- **Test Structure:** Given-When-Then pattern
- **Test Data:** Use builders or factories for consistent test data
- **Integration Tests:** Use TestContainers for database testing

### Test Data Creation
- Common Test Utils: Use TestUtils class for shared test data and utilities
- Entity-Specific Utils: Create entity-specific static classes (e.g., UserTestUtils, ProductTestUtils)
- Builder Pattern: Test utils should provide builder methods for flexible object creation
- Default Values: Provide sensible defaults while allowing customization

### Example test utils structure:
```
/ Common utilities
public class TestUtils {
    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_USERNAME = "testUser";
    
    public static ZonedDateTime fixedDateTime() {
        return ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    }
}

// Entity-specific utilities
public class UserTestUtils {
    public static User createActiveUser() {
        return User.builder()
            .username(TestUtils.DEFAULT_USERNAME)
            .email(TestUtils.DEFAULT_EMAIL)
            .firstName("John")
            .lastName("Doe")
            .status(Status.ACTIVE)
            .build();
    }
    
    public static UserDto createUserDto() {
        return UserDto.builder()
            .username(TestUtils.DEFAULT_USERNAME)
            .email(TestUtils.DEFAULT_EMAIL)
            .firstName("John")
            .lastName("Doe")
            .build();
    }
    
    public static User createUserWithCustomEmail(String email) {
        return createActiveUser().toBuilder()
            .email(email)
            .build();
    }
}
```


### Code Quality
- No comments required if code is self-explanatory
- Use meaningful variable and method names
- Keep methods small and focused
- Prefer composition over inheritance

## Database Configuration

### Connection Details
- **URL:** `jdbc:postgresql://localhost:5433/sdd_example`
- **Username:** `sdd_user`
- **Password:** `sdd_password`
- **Port:** 5433 (host) → 5432 (container)
- **Schema:** Public schema with Flyway version control

### Migration Strategy
- Use Flyway for all schema changes
- Prefix migrations with version number (V1__, V2__, etc.)
- Never modify existing migrations in production
- Include rollback scripts for complex changes

## Development Workflow

### Local Development
```bash
# Start infrastructure
docker-compose up -d postgres

# Run application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw test -Dtest="*IntegrationTest"

# Test with coverage
./mvnw test jacoco:report
```

### Build & Package
```bash
# Clean build
./mvnw clean compile

# Package application
./mvnw clean package

# Skip tests during packaging
./mvnw clean package -DskipTests
```

## API Documentation

### Available Endpoints
- **Application:** http://localhost:8091
- **Health Check:** http://localhost:8091/actuator/health
- **API Docs:** http://localhost:8091/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8091/v3/api-docs

## Domain Model

### Core Entities
- **User:** Represents system users with authentication details
- **CommonEntity:** Base entity with audit fields and soft delete

### Status Enum
- `ACTIVE`: Entity is active and visible
- `DELETED`: Entity is soft-deleted and hidden
- `ARCHIVED`: Entity exists but is temporarily disabled

# Ticket Development Workflow

## Ticket Format
When working with tickets, use this minimalistic format for maximum effectiveness:

```
**Title:** [Action] [Component] - [Brief Description]

**Goal:** [What should be accomplished in 1-2 sentences]

**Acceptance Criteria:**
- [ ] Specific, testable requirement 1
- [ ] Specific, testable requirement 2
- [ ] Specific, testable requirement 3

**Technical Notes:**
- Relevant endpoints/classes/files to modify
- Any constraints or dependencies
- Expected behavior changes

**Context:** [Optional - only if domain knowledge needed]
```

**Example Ticket:**
```
**Title:** Add User Status Filter - User API

**Goal:** Allow filtering users by status in the GET /users endpoint to support admin user management.

**Acceptance Criteria:**
- [ ] Add optional `status` query parameter to GET /users endpoint
- [ ] Filter returns users matching the specified status (ACTIVE, DELETED, INACTIVE)
- [ ] Default behavior unchanged when no status parameter provided
- [ ] Return 400 for invalid status values
- [ ] Update UserController and UserService classes
- [ ] Add integration test for filtering functionality

**Technical Notes:**
- Modify UserController.getUsers() method
- Update UserService.findUsers() to accept status parameter
- Use UserRepository.findByStatus() method
- Follow existing pagination pattern

**Context:** Admins need to view deleted users for audit purposes
```

## Automated Ticket Processing
When a prompt begins with `TICKET-XXXX` (where XXXX is a number), follow this automated workflow:

### 1. Branch Creation
```bash
git checkout -b PROJ-XXXX
```
Replace XXXX with the ticket number from the prompt.

### 2. Implementation Requirements
- Complete all acceptance criteria listed in the ticket
- Implement service logic with **minimum 85% code coverage** (lines tested, not just covered)
- Add controller tests for all new/modified endpoints
- Add repository tests if custom queries are implemented
- Follow all coding standards and conventions outlined in this document
- Use test utils classes for consistent test data creation

### 3. Code Quality Standards
- **Service Layer:** Must achieve 85% line coverage through meaningful tests
- **Controller Layer:** Test all HTTP status codes, request/response validation, and edge cases
- **Repository Layer:** Test custom queries, pagination, and filtering logic
- **Integration Tests:** Use TestContainers for database-dependent functionality
- **Test Naming:** Follow camelCase descriptive naming (e.g., `shouldReturnFilteredUsersWhenStatusProvided`)

### 4. Commit Process
After implementation is complete, commit changes using:
```bash
git add -A
git commit -m "PROJ-XXXX: [Descriptive message explaining what was implemented]"
git push origin PROJ-XXXX
```

**Commit Message Examples:**
- `PROJ-1234: Add user status filtering to GET /users endpoint with validation`
- `PROJ-5678: Implement soft delete functionality for Product entity with audit logging`

### 5. Pull Request Creation
Create a PR using GitHub CLI:
```bash
gh pr create --title "PROJ-XXXX: [Descriptive Title]" --body "[PR Description]"
```

**PR Description Template:**
```
## Ticket Context
[Include the original ticket goal and context]

## Implementation Summary
- [Key implementation decision 1 and rationale]
- [Key implementation decision 2 and rationale]
- [Any design patterns or architectural choices made]

## Changes Made
- [List of modified/created files and their purpose]
- [Database changes if applicable]
- [API changes if applicable]

## Testing
- Service layer coverage: XX% (minimum 85% required)
- Controller tests: [List of scenarios tested]
- Integration tests: [Database/API integration scenarios]

## Acceptance Criteria Completed
- [x] Criterion 1 - Implementation details
- [x] Criterion 2 - Implementation details
- [x] Criterion 3 - Implementation details
```

### 6. Required Git Commands
Only use these specific git commands during the workflow:
- `git checkout -b PROJ-XXXX` - Create feature branch
- `git add -A` - Stage all changes
- `git commit -m "message"` - Commit with descriptive message
- `git push origin PROJ-XXXX` - Push feature branch
- `gh pr create` - Create pull request

## Context Requirements
**Additional context is helpful for:**
- Complex business rules or domain logic
- Integration with external systems or APIs
- Specific performance or security requirements
- Non-standard data relationships or cascading effects

**Context typically NOT needed for:**
- Standard CRUD operations following established patterns
- Basic validation or formatting requirements
- Simple API endpoints matching existing conventions
- Common patterns already implemented in the codebase

## Development Best Practices

### Performance Considerations
- Use appropriate fetch strategies for JPA relationships
- Implement pagination for large result sets
- Consider database indexing for frequently queried fields
- Use connection pooling in production

### Security Guidelines
- Validate all user inputs
- Use parameterized queries to prevent SQL injection
- Implement proper authentication and authorization
- Never log sensitive information

### Monitoring & Observability
- Use Spring Boot Actuator for health checks
- Implement structured logging with correlation IDs
- Monitor database performance and query execution times
- Set up alerts for application errors and performance degradation