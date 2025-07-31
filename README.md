# Spring SDD Example

A Spring Boot 3.2.1 REST API demonstrating domain-driven design with PostgreSQL, featuring user management with soft-delete functionality.

## Quick Start

```bash
# Start database
docker-compose up -d postgres

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

## Features

- **REST API** for user management (CRUD operations)
- **Soft Delete** functionality with status-based filtering
- **PostgreSQL** database with Flyway migrations
- **Docker Compose** setup for local development
- **Comprehensive Testing** with JUnit 5 and TestContainers

## API Access

- Application: http://localhost:8091
- Health Check: http://localhost:8091/actuator/health
- API Documentation: http://localhost:8091/swagger-ui.html

## Tech Stack

- **Java 17** with Spring Boot 3.2.1
- **PostgreSQL 15** with JPA/Hibernate
- **Flyway** for database migrations
- **Maven** for build management
- **Docker** for containerization

## Documentation

- [Architecture Guide](docs/ARCHITECTURE.md) - Technical architecture and patterns
- [Development Guide](docs/DEVELOPMENT.md) - Setup and build commands
- [Testing Guide](docs/TESTING.md) - Testing conventions and utilities
- [Ticket Workflow](docs/TICKETS.md) - Development workflow

## Database

The application uses PostgreSQL with the following configuration:
- **Database:** `sdd_example`
- **User:** `sdd_user`
- **Port:** 5433 (host) → 5432 (container)

Default sample users are created during initial migration.