# RevWorkForce - Technical Overview & Design Decisions

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Security Features](#security-features)
3. [Database Layer](#database-layer)
4. [Key Technical Decisions](#key-technical-decisions)
5. [Important Classes & Components](#important-classes--components)
6. [Special Features](#special-features)

---

## Architecture Overview

### Application Type
**Single-Threaded Console Application**

**Why Console-Based?**
- ✅ Simplicity and focus on business logic
- ✅ Direct interaction without web/UI complexity
- ✅ Suitable for internal HR management
- ✅ Easy deployment and maintenance
- ✅ Low resource requirements

**Why Not Multithreading?**
- Console application with sequential user interactions
- Single user session at a time
- Database handles concurrent access when deployed for multiple users
- Overhead of thread management unnecessary for simple I/O operations
- **Alternative**: If concurrent access needed → Web application (Spring Boot) or Client-Server architecture

---

## Security Features

### 1. Password Security - BCrypt Hashing

**Location**: `com.revworkforce.util.PasswordUtil`

**Why BCrypt?**
- ✅ Industry-standard password hashing algorithm
- ✅ Built-in salt generation (prevents rainbow table attacks)
- ✅ Adaptive cost factor (can increase difficulty over time)
- ✅ Slow by design (prevents brute-force attacks)

**Implementation**:
```java
public static String hashPassword(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
}

public static boolean verifyPassword(String plainPassword, String hashedPassword) {
    return BCrypt.checkpw(plainPassword, hashedPassword);
}
```

**Key Features**:
- **Work Factor**: 12 rounds (2^12 iterations)
- **Salt**: Automatically generated and embedded
- **One-way**: Cannot reverse hash to get plaintext
- **Constant-time comparison**: Prevents timing attacks

**Alternatives Considered**:
- ❌ MD5/SHA-256: Too fast, vulnerable to brute force
- ❌ Plain text: Severe security risk
- ✅ **Argon2**: More modern, but BCrypt proven and well-supported

---

### 2. SQL Injection Prevention - PreparedStatements

**Location**: All DAO classes (e.g., `EmployeeDAO`, `LeaveDAO`)

**Why PreparedStatements?**
- ✅ Prevents SQL injection attacks
- ✅ Database compiles query once, executes many times (performance)
- ✅ Automatic parameter escaping
- ✅ Type-safe parameter binding

**Example**:
```java
// ❌ VULNERABLE: String concatenation
String sql = "SELECT * FROM employees WHERE employee_id = '" + empId + "'";

// ✅ SECURE: PreparedStatement
String sql = "SELECT * FROM employees WHERE employee_id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, empId);
```

**Implementation Pattern** (from `EmployeeDAO`):
```java
public Employee getEmployeeById(String employeeId) {
    String sql = "SELECT * FROM employees WHERE employee_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, employeeId);  // Safe parameter binding
        ResultSet rs = ps.executeQuery();
        // ...
    }
}
```

**Benefits**:
- No manual escaping needed
- Protection against malicious input: `'; DROP TABLE employees; --`
- Database query plan caching

---

### 3. Account Security Features

**Failed Login Protection** (`AuthService`):
```java
private static final int MAX_ATTEMPTS = 5;

// Auto-lock after 5 failed attempts
if (currentFailures + 1 >= MAX_ATTEMPTS) {
    dao.lockAccount(employeeId);
}
```

**Session Management** (`SessionContext`):
```java
// Thread-local session storage
private static final ThreadLocal<Employee> currentUser = new ThreadLocal<>();

// Automatic cleanup on logout
public static void clear() {
    currentUser.remove();
}
```

---

## Database Layer

### Connection Management

**Location**: `com.revworkforce.util.DBConnection`

**Pattern**: Connection Pool (HikariCP)

```java
private static HikariDataSource dataSource;

static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:ORCL");
    config.setUsername("system");
    config.setPassword("password");
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(30000);
    dataSource = new HikariDataSource(config);
}
```

**Why Connection Pooling?**
- ✅ Reuses database connections (performance)
- ✅ Prevents connection leaks
- ✅ Manages connection lifecycle automatically
- ✅ Configurable pool size for scalability

**Alternative**: Direct DriverManager connections (slower, no pooling)

---

### Transaction Management

**Example** (`AdminLeaveService`):
```java
try (Connection conn = DBConnection.getConnection()) {
    conn.setAutoCommit(false);
    try {
        // Multiple operations
        leaveDAO.updateLeaveStatus(leaveId, "APPROVED");
        leaveDAO.updateLeaveBalance(empId, -days);
        
        conn.commit();  // All or nothing
    } catch (Exception e) {
        conn.rollback();  // Undo all on error
        throw e;
    }
}
```

**ACID Properties Ensured**:
- **Atomicity**: All operations succeed or all fail
- **Consistency**: Database constraints maintained
- **Isolation**: Concurrent operations don't interfere
- **Durability**: Committed data persists

---

## Key Technical Decisions

### 1. Layered Architecture

```
┌─────────────────────────────────────────┐
│           Menu Layer                     │  User Interface
│  (AdminMenu, EmployeeMenu, etc.)        │
├─────────────────────────────────────────┤
│         Service Layer                    │  Business Logic
│  (AdminService, LeaveService, etc.)     │
├─────────────────────────────────────────┤
│           DAO Layer                      │  Data Access
│  (EmployeeDAO, LeaveDAO, etc.)          │
├─────────────────────────────────────────┤
│        Database (Oracle)                 │  Persistence
└─────────────────────────────────────────┘
```

**Benefits**:
- ✅ Separation of concerns
- ✅ Easy to test (mock DAOs in service tests)
- ✅ Can swap UI (console → web) without changing services
- ✅ Database-agnostic service layer

---

### 2. Static Methods vs Object-Oriented

**Choice**: Static methods in Service and DAO layers

**Rationale**:
- Console app with single user session
- No need for object state management
- Simpler codebase for this scale
- Services are stateless (all state in DB)

**When to Refactor**:
- If moving to web app → Use Spring beans (@Service, @Repository)
- If need dependency injection → Constructor injection
- If complex state management needed → Instance methods

---

### 3. Input Validation Strategy

**Two-Tier Validation**:

**1. Format Validation** (`ValidationUtil`):
```java
public static boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
}

public static boolean isValidPhone(String phone) {
    return phone.matches("\\d{10}");
}
```

**2. Business Validation** (Service Layer):
```java
// Extracted validation methods (NEW - refactored from lambdas)
static String validateEmail(String input) {
    if (!ValidationUtil.isValidEmail(input)) return "Invalid format";
    if (employeeDAO.isEmailExists(input)) return "Already exists";
    return null;
}
```

**Benefits**:
- Format validation reusable everywhere
- Business validation includes DB checks
- Clear error messages to users
- Testable validation logic

---

## Important Classes & Components

### Core Utility Classes

#### 1. `PasswordUtil` - Security
```java
+ hashPassword(String): String          // BCrypt hash generation
+ verifyPassword(String, String): boolean  // Constant-time verification
```

**Usage**: Login, password changes, password resets

---

#### 2. `InputUtil` - User Input
```java
+ readString(String prompt): String
+ readInt(String prompt): int
+ readValidatedString(prompt, validator, error): String
+ readValidatedString(prompt, Function<String,String>): String
```

**Special Feature**: Validation lambda support
```java
// With error message
String name = InputUtil.readValidatedString("Name: ", 
    s -> !s.isEmpty(), "Name cannot be empty");

// With custom validation logic
String email = InputUtil.readValidatedString("Email: ", 
    AdminService::validateEmail);
```

---

#### 3. `DateUtil` - Date Operations
```java
+ getCurrentDate(): Date               // Today's date
+ getCurrentTimestamp(): Timestamp     // Current timestamp
+ formatTimestamp(Timestamp): String   // Human-readable format
+ parseDate(String): Date              // String to Date conversion
```

**Why Needed**: Consistent date handling across application

---

#### 4. `SessionContext` - Session Management
```java
private static final ThreadLocal<Employee> currentUser;

+ set(Employee): void       // Store logged-in user
+ get(): Employee          // Retrieve current user
+ clear(): void            // Logout/cleanup
```

**Thread-Local Storage**: Safe for future multi-threading if needed

---

### Core Service Classes

#### 1. `AuthService` - Authentication & Authorization
```java
+ login(empId, password): Employee          // Login with account locking
+ logout(): void                             // Session cleanup
+ changePassword(empId, old, new): boolean  // Password change
+ resetPasswordViaSecurity(): void          // Forgot password flow
+ recordFailedAttempt(empId): void         // Track login failures
```

**Security Features**:
- BCrypt password verification
- Account locking after 5 failed attempts
- Security question-based password recovery
- Session timeout (inactivity detection)

---

#### 2. `AdminService` - Employee Management
```java
+ addEmployee(): void                    // Employee onboarding
+ updateEmployee(): void                 // Profile updates
+ toggleEmployeeStatus(): void          // Activate/deactivate
+ unlockEmployeeAccount(): void         // Unlock locked accounts
+ resetUserPassword(): void             // Admin password reset
```

**Special Features**:
- Auto-generate employee IDs (EMP001, MGR001)
- Validation with reusable methods (NEW - refactored)
- Audit logging for all actions
- Role-based designation assignment

---

#### 3. `LeaveService` - Leave Management
```java
+ applyLeave(empId): void              // Submit leave request
+ cancelLeave(empId): void             // Cancel pending leave
+ viewLeaveBalance(empId): void        // Check available leaves
+ viewMyLeaves(empId): void            // Leave history
```

**Business Logic**:
- Date validation (end > start)
- Leave balance checking
- Multi-type leave support (sick, casual, annual)
- Holiday calendar integration

---

#### 4. `AuditService` - Audit Logging
```java
+ log(performedBy, action, table, recordId, desc): void
```

**Audit Trail**: WHO did WHAT, WHEN, on WHICH record

Example log:
```
ADMIN001 | UPDATE | EMPLOYEES | EMP123 | Salary changed | 2024-01-15 10:30:00
```

---

### Key DAO Classes

#### Pattern: All DAOs follow this structure

```java
public class XxxDAO {
    // Create
    public void insert(Entity entity) { ... }
    
    // Read
    public Entity getById(String id) { ... }
    public List<Entity> getAll() { ... }
    
    // Update
    public void update(Entity entity) { ... }
    
    // Delete (soft delete)
    public void deactivate(String id) { ... }
}
```

**Key DAOs**:
- `EmployeeDAO` - Employee CRUD
- `LeaveDAO` - Leave operations
- `AttendanceDAO` - Check-in/out
- `PerformanceDAO` - Reviews, goals
- `AuditLogDAO` - Audit trail
- `LeavePolicyDAO` - Leave types, quotas

---

## Special Features

### 1. Smart Employee ID Generation

**Location**: `EmployeeDAO.getNextId(String prefix)`

```java
public String getNextId(String prefix) {
    String sql = "SELECT employee_id FROM employees " +
                 "WHERE employee_id LIKE ? " +
                 "ORDER BY employee_id DESC";
    
    // Finds highest ID with prefix (e.g., EMP004)
    // Returns next ID (e.g., EMP005)
}
```

**Auto-increment**: EMP001 → EMP002 → ... → EMP999

---

### 2. Organization Chart Visualization

**Location**: `EmployeeService.viewOrganizationChart()`

```java
// Builds tree structure
Manager (MGR001)
├── Employee 1 (EMP101)
├── Employee 2 (EMP102)
└── Manager 2 (MGR002)
    ├── Employee 3 (EMP201)
    └── Employee 4 (EMP202)
```

**Algorithm**: Recursive tree traversal with indentation

---

### 3. Session Timeout Management

**Location**: `AuthService`

```java
private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 min
private static long lastActivity;

public static void checkTimeout() {
    if (System.currentTimeMillis() - lastActivity > SESSION_TIMEOUT_MS) {
        SessionContext.clear();
        // Force re-login
    }
}
```

**Auto-logout**: After 30 minutes of inactivity

---

### 4. Validation Lambda Refactoring (NEW)

**Problem**: Inline validation lambdas were untestable

**Solution**: Extracted to dedicated methods

**Before** (untestable):
```java
emp.setEmail(InputUtil.readValidatedString("Email: ", input -> {
    if (!ValidationUtil.isValidEmail(input)) return "Invalid";
    if (dao.isEmailExists(input)) return "Exists";
    return null;
}));
```

**After** (fully testable):
```java
emp.setEmail(InputUtil.readValidatedString("Email: ", AdminService::validateEmail));

// Validation method with 100% test coverage
static String validateEmail(String input) {
    if (!ValidationUtil.isValidEmail(input)) return "Invalid email format.";
    if (employeeDAO.isEmailExists(input)) return "Email already exists.";
    return null;
}
```

**Impact**: +27 validation tests, +21 branches covered

---

## Testing Strategy

### Test Coverage: 70-75%

**Test Types**:
1. **Unit Tests**: Service layer with mocked DAOs
2. **Validation Tests**: All validation methods (NEW)
3. **Integration Tests**: DAO classes with test database

**Key Test Files**:
- `AdminServiceValidationTest` - 27 validation tests (NEW)
- `AuthServiceTest` - Login, password security
- `LeaveServiceTest` - Leave business logic
- `EmployeeServiceTest` - Employee operations

**Mocking Strategy**:
```java
@Mock
private EmployeeDAO mockDao;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    // Inject mock into static field via reflection
}
```

---

## Performance Optimizations

### 1. Connection Pooling (HikariCP)
- Reuses connections → 10x faster than creating new ones
- Configurable pool size (10 max, 2 min idle)

### 2. PreparedStatement Caching
- Database caches query execution plans
- Faster repeated queries

### 3. Lazy Loading
- ResultSets processed on-demand
- Memory-efficient for large datasets

---

## Future Enhancements

### If Scaling to Multi-User Web App:

1. **Framework**: Spring Boot
   - Dependency Injection
   - RESTful APIs
   - Built-in security (Spring Security)

2. **Multithreading**: Handled by web server (Tomcat)
   - Request per thread
   - Connection pool per user

3. **Frontend**: React/Angular
   - Better UX
   - Real-time updates

4. **Caching**: Redis
   - Reduce database load
   - Session storage

5. **Message Queue**: RabbitMQ/Kafka
   - Async notifications
   - Background jobs (email, reports)

---

## Summary of Key Technologies

| Technology | Purpose | Why Chosen |
|-----------|---------|-----------|
| **BCrypt** | Password hashing | Industry standard, adaptive, secure |
| **PreparedStatement** | SQL execution | SQL injection prevention, performance |
| **HikariCP** | Connection pooling | Fast, reliable, lightweight |
| **ThreadLocal** | Session storage | Thread-safe, simple for console app |
| **Log4j2** | Logging | Powerful, configurable, async logging |
| **JUnit 5** | Testing | Modern, feature-rich testing framework |
| **Mockito** | Mocking | Clean API, powerful mocking capabilities |
| **Oracle DB** | Database | Enterprise-grade, ACID compliance |

---

## Conclusion

RevWorkForce demonstrates enterprise-level practices in a console application:
- ✅ **Security**: BCrypt, PreparedStatements, account locking
- ✅ **Architecture**: Clean layering, separation of concerns
- ✅ **Quality**: 70%+ test coverage, refactored validation
- ✅ **Maintainability**: Documented, modular, extensible

**Ready for presentation**: All design decisions justified with technical rationale.
