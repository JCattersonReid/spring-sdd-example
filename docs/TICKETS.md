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

## Example Ticket
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

### 1. Planning Phase
Before any implementation, Claude will create a comprehensive implementation plan:

**Claude Planning Output Format:**
```
## TICKET ANALYSIS: PROJ-XXXX
### Understanding & Scope
- [Restate the ticket goal in technical terms]
- [Identify all components that need modification]
- [List any assumptions being made]

### Technical Implementation Plan
#### Files to Modify/Create:
- **Controller:** [Specific controller class and methods]
- **Service:** [Service class and new/modified methods]
- **Repository:** [Repository changes if needed]
- **Entity/DTO:** [Any model changes required]
- **Tests:** [Specific test classes to create/modify]

#### Implementation Steps:
1. [Step 1 with specific technical details]
2. [Step 2 with specific technical details]
3. [Step 3 with specific technical details]
4. [etc.]

#### Testing Strategy:
- **Unit Tests:** [Specific test scenarios and coverage targets]
- **Integration Tests:** [Database/API integration test scenarios]
- **Edge Cases:** [Boundary conditions and error scenarios to test]

#### Database Changes:
- [Any schema modifications needed]
- [New queries or repository methods]
- [Migration requirements if applicable]

### Risk Assessment:
- [Potential integration issues]
- [Performance considerations]
- [Backward compatibility concerns]

### Estimated Effort:
- Implementation: [X hours]
- Testing: [Y hours]
- Total: [Z hours]

### Questions/Clarifications Needed:
- [Any ambiguities in requirements]
- [Technical decisions requiring input]
```

### 2. Branch Creation
```bash
git checkout -b PROJ-XXXX
```
Replace XXXX with the ticket number from the prompt.

### 3. Implementation Requirements
- Complete all acceptance criteria listed in the ticket
- Implement service logic with **minimum 85% code coverage** (lines tested, not just covered)
- Add controller tests for all new/modified endpoints
- Add repository tests if custom queries are implemented
- Follow all coding standards and conventions outlined in the Architecture Guide
- Use test utils classes for consistent test data creation

### 4. Code Quality Standards
- **Service Layer:** Must achieve 85% line coverage through meaningful tests
- **Controller Layer:** Test all HTTP status codes, request/response validation, and edge cases
- **Repository Layer:** Test custom queries, pagination, and filtering logic
- **Integration Tests:** Use TestContainers for database-dependent functionality
- **Test Naming:** Follow camelCase descriptive naming (e.g., `shouldReturnFilteredUsersWhenStatusProvided`)

### 5. Commit Process
After implementation is complete, commit changes using:
```bash
git add -A
git commit -m "PROJ-XXXX: [Descriptive message explaining what was implemented]"
git push origin PROJ-XXXX
```

**Commit Message Examples:**
- `PROJ-1234: Add user status filtering to GET /users endpoint with validation`
- `PROJ-5678: Implement soft delete functionality for Product entity with audit logging`

### 6. Pull Request Creation
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

### 7. Required Git Commands
Only use these specific git commands during the workflow:
- `git checkout -b PROJ-XXXX` - Create feature branch
- `git add -A` - Stage all changes
- `git commit -m "message"` - Commit with descriptive message
- `git push origin PROJ-XXXX` - Push feature branch
- `gh pr create` - Create pull request

## Context Requirements

### Additional Context is Helpful For:
- Complex business rules or domain logic
- Integration with external systems or APIs
- Specific performance or security requirements
- Non-standard data relationships or cascading effects

### Context Typically NOT Needed For:
- Standard CRUD operations following established patterns
- Basic validation or formatting requirements
- Simple API endpoints matching existing conventions
- Common patterns already implemented in the codebase


TICKET-0010

**Title:** Add Group CRUD - Group API

**Goal:** Implement complete CRUD operations for Group entity to enable group management functionality in the system.

**Acceptance Criteria:**
- [ ] Create Group entity extending CommonEntity with specified schema fields
- [ ] Implement GroupRepository with custom queries for active groups
- [ ] Create GroupService with all CRUD operations following soft-delete pattern
- [ ] Add GroupController with RESTful endpoints (GET, POST, PUT, DELETE)
- [ ] Implement GroupDto and GroupAssembler for request/response mapping
- [ ] Add input validation for group creation and updates
- [ ] Return appropriate HTTP status codes (200, 201, 404, 400)
- [ ] Add comprehensive test coverage (minimum 85% for service layer)
- [ ] Create Flyway migration for groups table with specified schema
- [ ] Update API documentation with new endpoints
- [ ] Skip controller tests for now
- [ ] Ensure to use workflow in TICKETS.md to create the branch and PR


**Technical Notes:**
- Create Group entity in model/entity package with schema:
    - id: UUID (primary key)
    - name: VARCHAR(240) NOT NULL
    - description: VARCHAR(500) NOT NULL
    - selfJoin: boolean DEFAULT false
    - selfLeave: boolean DEFAULT false
    - adminId: UUID (foreign key to User.id)
    - status: Status ENUM (inherited from CommonEntity)
    - createdDate: timestamp (inherited from CommonEntity)
    - updatedDate: timestamp (inherited from CommonEntity)
- Add GroupDto in model/dto package
- Follow existing User entity patterns for consistency
- Implement GroupRepository.findByStatusAndNameContaining() for search
- Use GroupAssembler for entity-DTO conversion
- Add GroupController with endpoints: GET /groups, POST /groups, PUT /groups/{id}, DELETE /groups/{id}
- Follow pagination pattern for list endpoint
- Use GroupTestUtils class for test data creation
- Add @JoinColumn for adminId relationship to User entity

**Context:** Groups will be used to organize users and manage permissions in future features. Each group should have a unique name within active groups and support soft-delete functionality like other entities in the system. The selfJoin and selfLeave flags control whether users can join/leave groups independently, while adminId tracks the group administrator.