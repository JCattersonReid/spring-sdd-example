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

## Quick Start
```bash
# Start infrastructure
docker-compose up -d postgres

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

## API Access
- **Application:** http://localhost:8091
- **Health Check:** http://localhost:8091/actuator/health
- **API Docs:** http://localhost:8091/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8091/v3/api-docs

## Documentation Structure
This project uses hierarchical documentation for better organization:

- **[Architecture Guide](docs/ARCHITECTURE.md)** - Technical architecture, patterns, and project structure
- **[Development Guide](docs/DEVELOPMENT.md)** - Local setup, build commands, and development workflows  
- **[Testing Guide](docs/TESTING.md)** - Testing conventions, utilities, and best practices
- **[Ticket Workflow](docs/TICKETS.md)** - Automated ticket processing and development workflow

## Core Principles
- **KISS Principle:** Keep It Simple Stupid - prefer simple, readable solutions
- **DRY:** Don't Repeat Yourself - extract common logic
- **Single Responsibility:** Each class should have one reason to change
- **Fail Fast:** Validate inputs early and provide clear error messages

## Domain Model
### Core Entities
- **User:** Represents system users with authentication details
- **CommonEntity:** Base entity with audit fields and soft delete

### Status Enum
- `ACTIVE`: Entity is active and visible
- `DELETED`: Entity is soft-deleted and hidden
- `ARCHIVED`: Entity exists but is temporarily disabled

## Database Configuration
- **URL:** `jdbc:postgresql://localhost:5433/sdd_example`
- **Username:** `sdd_user`
- **Password:** `sdd_password`
- **Port:** 5433 (host) → 5432 (container)
- **Schema:** Public schema with Flyway version control

## Important Instruction Reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.