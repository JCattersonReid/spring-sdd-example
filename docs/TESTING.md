# Testing Guide

## Testing Framework & Tools
- **Testing:** JUnit 5, TestContainers
- **Coverage:** Jacoco
- **Database:** TestContainers PostgreSQL
- **Assertions:** AssertJ (where applicable)

## Test Conventions

### Method Naming
- **Format:** camelCase and descriptive
- **Pattern:** `should[ExpectedBehavior]When[StateOrCondition]`
- **Examples:**
  - `shouldReturnActiveUsersWhenStatusIsActive`
  - `shouldThrowExceptionWhenUserNotFound`
  - `shouldReturnEmptyListWhenNoUsersExist`

### Test Structure
Follow **Given-When-Then** pattern (structure only, no comments):
```java
@Test
void shouldReturnUserWhenValidIdProvided() {
    User expectedUser = UserTestUtils.createActiveUser();
    when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
    
    User actualUser = userService.findById(1L);
    
    assertThat(actualUser).isEqualTo(expectedUser);
}
```

### Test Data Creation
Use test utility classes for consistent test data:

#### Common Test Utils
```java
public class TestUtils {
    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_USERNAME = "testUser";
    
    public static ZonedDateTime fixedDateTime() {
        return ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    }
}
```

#### Entity-Specific Utils
```java
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

### Test Data Principles
- **Common Test Utils:** Use TestUtils class for shared test data and utilities
- **Entity-Specific Utils:** Create entity-specific static classes (e.g., UserTestUtils, ProductTestUtils)
- **Builder Pattern:** Test utils should provide builder methods for flexible object creation
- **Default Values:** Provide sensible defaults while allowing customization

## Test Types & Coverage Requirements

### Unit Tests
- **Target:** Individual methods and classes in isolation
- **Coverage:** Minimum 85% line coverage for service layer
- **Mocking:** Use Mockito for dependencies
- **Focus:** Business logic, validation, error handling

### Integration Tests
- **Target:** Component interactions and database operations
- **Database:** Use TestContainers for PostgreSQL
- **Coverage:** Repository layer, full request-response cycles
- **Naming:** Classes should end with `IntegrationTest`

### Controller Tests
- **Target:** HTTP endpoints, request/response handling
- **Coverage:** All HTTP status codes, validation, error responses
- **Tools:** MockMvc for web layer testing
- **Focus:** Request mapping, parameter validation, response formatting

### Repository Tests
- **Target:** Data access layer and custom queries
- **Coverage:** Custom query methods, pagination, filtering
- **Database:** TestContainers for real database testing
- **Focus:** Query correctness, data integrity, soft delete filtering

## Test Execution

### Running Tests
```bash
# All tests
./mvnw test

# Unit tests only
./mvnw test -Dtest="*Test"

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"

# Specific test class
./mvnw test -Dtest=UserServiceTest

# Test with coverage report
./mvnw test jacoco:report

# Test with specific profile
./mvnw test -Dspring.profiles.active=test
```

### Coverage Requirements
- **Service Layer:** Minimum 85% line coverage (not just method coverage)
- **Controller Layer:** Test all endpoints, status codes, and validation scenarios
- **Repository Layer:** Test custom queries and database interactions
- **Integration Tests:** Cover critical business workflows end-to-end

## TestContainers Setup

### Database Integration Tests
```java
@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

## Test Quality Guidelines

### What to Test
- **Happy Path:** Normal successful operations
- **Edge Cases:** Boundary conditions, empty inputs, null values
- **Error Scenarios:** Invalid inputs, not found conditions, constraint violations
- **Business Rules:** Domain-specific validation and logic
- **Integration Points:** Service interactions, database operations

### What NOT to Test
- **Framework Code:** Spring Boot auto-configuration, JPA internals
- **Trivial Methods:** Simple getters/setters without logic
- **Third-party Libraries:** External library functionality
- **Configuration Classes:** Simple bean definitions

### Code Style in Tests
- **No Comments:** Avoid Given-When-Then comments or any explanatory comments
- **Accessor Chaining:** Use fluent method chaining with consistent indentation
- **Clean Structure:** Let the code structure speak for itself

### Test Maintenance
- Keep tests simple and focused on single behaviors
- Use meaningful test data that represents realistic scenarios
- Avoid test dependencies - each test should be independent
- Clean up test data appropriately (TestContainers handles this)
- Review and update tests when requirements change