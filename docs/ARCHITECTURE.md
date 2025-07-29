# Architecture Guide

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
├── application.yml                  # Application configuration
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
- **No @Column annotations required:** Let JPA use field names as column names
- **Database-driven validation:** Validation constraints handled by database tables, not source code annotations

### API Endpoint Rules
- **GET endpoints:** Return only ACTIVE status entities
- **DELETE endpoints:** Soft delete (update status to DELETED)
- **POST/PUT endpoints:** Validate input and return appropriate status
- Use HTTP status codes correctly (201 for creation, 204 for no content)

### Code Quality
- No comments required if code is self-explanatory
- Use meaningful variable and method names
- Keep methods small and focused
- Prefer composition over inheritance
- **Prioritize accessor chaining:** Use fluent method chaining with consistent indentation for all objects

## Layer Responsibilities

### Controller Layer
- Handle HTTP requests and responses
- Validate request parameters and payloads
- Transform DTOs to/from entities via assemblers
- Return appropriate HTTP status codes
- Handle exception mapping

### Service Layer
- Implement business logic and rules
- Coordinate between repositories and external services
- Handle transactions and data consistency
- Perform data validation and transformation
- Implement caching strategies where appropriate

### Repository Layer
- Data access and persistence operations
- Custom query implementations
- Soft delete filtering
- Pagination and sorting support
- Database-specific optimizations

### Model Layer
- **Entities:** JPA database mappings with audit fields
- **DTOs:** API request/response objects with validation
- **Assemblers:** Entity to DTO conversion logic
- **Enums:** Type-safe constants and status values

## Domain Model Design

### CommonEntity Abstract Class
Base class providing:
- Audit fields (createdAt, updatedAt, createdBy, updatedBy)
- Soft delete support via status field
- Optimistic locking with version field
- Common builder pattern support

### Status Management
All entities use Status enum for lifecycle management:
- `ACTIVE`: Entity is active and visible in queries
- `DELETED`: Entity is soft-deleted and filtered from results
- `ARCHIVED`: Entity exists but is temporarily disabled

### Relationship Patterns
- Use `@JoinColumn` for foreign key relationships
- Implement bidirectional relationships carefully
- Consider fetch strategies (LAZY vs EAGER)
- Use cascade operations appropriately

### Validation Strategy
- **Database-First Validation:** Rely on database constraints (NOT NULL, UNIQUE, CHECK, etc.)
- **Minimal Bean Validation:** Only use `@Valid` for DTO validation at API boundaries
- **No Entity Validation Annotations:** Avoid `@NotNull`, `@Size`, etc. on entity fields
- **Database Schema Controls Data Integrity:** Let migration scripts define all constraints