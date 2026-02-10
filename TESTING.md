# RevWorkForce Testing Strategy

This document outlines the testing framework and strategy used to ensure the reliability and correctness of the RevWorkForce application.

## 1. Testing Frameworks

The project utilizes industry-standard libraries for unit testing and mocking:

*   **JUnit 5 (Jupiter)**: The primary framework for writing and running unit tests.
    *   Used for: Defining test cases, assertions, and test lifecycle management (`@Test`, `@BeforeEach`, `@DisplayName`).
*   **Mockito**: A mocking framework used to simulate dependencies.
    *   Used for: Mocking DAO layers and static utilities to test Service layer logic in isolation (`mockByName`, `verify`).

## 2. Test Coverage

### A. Service Layer Tests
The core business logic resides in the Service layer. We test these classes thoroughly by mocking the underlying Data Access Objects (DAOs).

| Test Class | Target Class | Key Scenarios Tested |
| :--- | :--- | :--- |
| `AdminServiceTest` | `AdminService` | • Employee onboarding (success/failure)<br>• Duplicate email/phone validation<br>• Department/Designation management |
| `AuthServiceTest` | `AuthService` | • Login success vs. invalid credentials<br>• Account locking after max failed attempts<br>• Password recovery flows |
| `LeaveServiceTest` | `LeaveService` | • Applying for leave (valid dates/balance)<br>• Preventing duplicate leave requests |
| `PerformanceTest` | `PerformanceService` | • Review submission mechanics<br>• Goal progress updates |

### B. Utility Tests
Helper classes are tested for edge cases and correctness.

| Test Class | Target Class | Key Scenarios Tested |
| :--- | :--- | :--- |
| `PasswordUtilTest` | `PasswordUtil` | • Hashing consistency<br>• Checking plain text against hash |
| `DateUtilTest` | `DateUtil` | • Date format validation<br>• Date difference calculation (days betweeen) |
| `ValidationUtilTest` | `ValidationUtil` | • Email format regex<br>• Phone number structure |

## 3. How to Run Tests

### Via Command Line (Maven)
To run all tests in the project:
```bash
mvn test
```

To run a specific test class:
```bash
mvn -Dtest=AdminServiceTest test
```

### Via IDE (IntelliJ / Eclipse)
1.  Navigate to `src/test/java`.
2.  Right-click on the `com.revworkforce` package (or specific test file).
3.  Select **"Run Tests"**.

## 4. Test Example

Here is a snippet showing how we test the **Employee Onboarding** logic by mocking the `EmployeeDAO`:

```java
@Test
@DisplayName("Should successfully register a new employee")
void testRegisterEmployee_Success() {
    // 1. Arrange: Mock DAO to return false (not duplicate)
    when(mockEmployeeDAO.isEmailExists(anyString())).thenReturn(false);
    when(mockEmployeeDAO.isPhoneExists(anyString())).thenReturn(false);
    
    // 2. Act: Call the service method
    boolean result = AdminService.registerEmployee(newEmployeeDetails);
    
    // 3. Assert: Verify success and DAO interaction
    assertTrue(result);
    verify(mockEmployeeDAO, times(1)).insertEmployee(any(Employee.class));
}
```

## 5. Test Coverage Reporting
We use **JaCoCo** to track code coverage. After running tests, a comprehensive report is generated.

**[View Full Coverage Report](site/jacoco/index.html)**

## 6. Future Improvements
*   **Integration Testing**: Setup an H2 in-memory database to test DAO queries directly.

