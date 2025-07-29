# Development Guide

## Local Development Setup

### Prerequisites
- Java 17
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL client (optional)

### Environment Setup
```bash
# Start infrastructure
docker-compose up -d postgres

# Verify database is running
docker ps | grep postgres

# Run application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Configuration
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

## Build & Package Commands

### Development Commands
```bash
# Clean build
./mvnw clean compile

# Package application
./mvnw clean package

# Skip tests during packaging
./mvnw clean package -DskipTests

# Run application locally
./mvnw spring-boot:run
```

### Testing Commands
```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw test -Dtest="*IntegrationTest"

# Test with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run tests with specific profile
./mvnw test -Dspring.profiles.active=test
```

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

## API Development

### Available Endpoints
- **Application:** http://localhost:8091
- **Health Check:** http://localhost:8091/actuator/health
- **API Docs:** http://localhost:8091/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8091/v3/api-docs

### Development Workflow
1. Start PostgreSQL container
2. Run application in development mode
3. Use Swagger UI for API testing
4. Monitor health endpoints during development
5. Run tests frequently during development

## Debugging & Troubleshooting

### Common Issues
- **Port conflicts:** Check if port 8091 or 5433 are already in use
- **Database connectivity:** Verify PostgreSQL container is running
- **Migration failures:** Check Flyway migration scripts for syntax errors
- **Test failures:** Ensure test profile is configured correctly

### Debugging Tips
- Use Spring Boot DevTools for automatic restart
- Enable debug logging for specific packages in application.yml
- Use actuator endpoints to monitor application health
- Check Docker logs: `docker logs <container_name>`