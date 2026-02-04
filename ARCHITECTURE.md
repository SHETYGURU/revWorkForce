# System Architecture Documentation

This document provides a detailed breakdown of the RevWorkForce system architecture, analyzing every class, its role, and key operations.

## 🏛 Application Overview
RevWorkForce follows a **Layered Architecture** pattern, ensuring separation of duties between User Interaction (Menus), Business Logic (Services), and Data Persistence (DAOs).

```mermaid
classDiagram
    %% Relationships
    MainMenu ..> AuthService : Login
    MainMenu ..> AdminMenu : Navigates
    MainMenu ..> ManagerMenu : Navigates
    MainMenu ..> EmployeeMenu : Navigates
    
    AdminMenu ..> AdminService : Invokes logic
    ManagerMenu ..> ManagerService : Invokes logic
    EmployeeMenu ..> EmployeeService : Invokes logic
    
    AdminService --> EmployeeDAO : Uses
    AdminService --> AuditLogDAO : Uses
    AdminService --> SystemPolicyDAO : Uses
    
    ManagerService --> EmployeeDAO : Uses
    ManagerService --> LeaveDAO : Uses
    ManagerService --> PerformanceDAO : Uses
    
    EmployeeService --> EmployeeDAO : Uses
    EmployeeService --> LeaveDAO : Uses
    EmployeeService --> AttendanceDAO : Uses
    
    %% UI Layer (Menus)
    class MainMenu {
        +start()
        +handleLogin()
        +handleForgotPassword()
    }
    class AdminMenu {
        +display()
        +manageEmployees()
        +manageSystem()
        +viewReports()
    }
    class ManagerMenu {
        +display()
        +myTeam()
        +manageLeaves()
        +conductReviews()
    }
    class EmployeeMenu {
        +display()
        +viewProfile()
        +applyLeave()
        +markAttendance()
    }

    %% Service Layer
    class AuthService {
        +login(userId, password)
        +resetPassword(userId)
        +logout()
    }
    class AdminService {
        +addEmployee(empDetails)
        +updateEmployee(empDetails)
        +unlockAccount(empId)
        +viewAuditLogs()
        +configureSystem()
    }
    class ManagerService {
        +viewTeam(managerId)
        +processLeave(leaveId, status)
        +submitPerformanceReview(reviewId, rating)
        +assignGoal(empId, goal)
    }
    class EmployeeService {
        +viewProfile(empId)
        +updateProfile(details)
        +changePassword(old, new)
        +viewAnnouncements()
    }
    
    %% Data Access Layer (DAOs)
    class EmployeeDAO {
        +getEmployeeById(id)
        +insertEmployee(data)
        +updateProfile(data)
        +getReportees(mgrId)
        +lockAccount(id)
    }
    class LeaveDAO {
        +getLeaveBalances(empId)
        +applyLeave(data)
        +updateLeaveStatus(id, status)
        +getTeamLeaveRequests(mgrId)
    }
    class PerformanceDAO {
        +submitSelfReview(data)
        +submitManagerFeedback(data)
        +getTeamGoals(mgrId)
        +createGoal(data)
    }
    class AttendanceDAO {
        +checkIn(empId)
        +checkOut(empId)
        +getAttendanceHistory(empId)
    }
```

---

## 🧩 Detailed Class Roles & Responsibilities

### 1. Presentation Layer (Menus)
Files located in `com.revworkforce.menu` handle all user inputs and console display logic.

| Class | Role | Key Operations |
| :--- | :--- | :--- |
| **`MainMenu`** | **Entry Point** | • Application Bootstrap (`main` calls `start()`)<br>• User Login / Session Creation<br>• Password Recovery Flow |
| **`AdminMenu`** | **Admin UI** | • Employee CRUD Interface<br>• System Configuration Menus<br>• Audit Log Viewer |
| **`ManagerMenu`** | **Manager UI** | • Team Dashboard<br>• Leave Approval Console<br>• Performance Review Interface |
| **`EmployeeMenu`** | **Employee UI** | • Self-Service Portal<br>• Leave Application Form<br>• Attendance Marking<br>• Profile Updates |

### 2. Service Layer (Business Logic)
Files in `com.revworkforce.service` contain the core business rules, validations, and transaction orchestration.

| Class | Role | Key Operations |
| :--- | :--- | :--- |
| **`AuthService`** | **Security** | • **`login()`**: Validates credentials using BCrypt.<br>• **`logout()`**: Clears `SessionContext`.<br>• **`forcePasswordReset()`**: Triggers flow for first-time users. |
| **`AdminService`** | **Admin Logic** | • **`addEmployee()`**: Validates unique email/phone, generates ID, calls DAO.<br>• **`unlockAccount()`**: Resets failed login counters.<br>• **`configureLeaveTypes()`**: Adds new leave categories dynamically. |
| **`ManagerService`** | **Manager Logic** | • **`viewTeam()`**: Fetches hierarchy-based reportee list.<br>• **`processLeave()`**: Validates permissions before approving leaves.<br>• **`submitReview()`**: Calculates final ratings and commits feedback. |
| **`EmployeeService`** | **User Logic** | • **`viewProfile()`**: Fetches sensitive data only for the owner.<br>• **`viewBirthday()`**: Employee engagement features.<br>• **`changePassword()`**: Enforces password complexity policies. |
| **`LeaveService`** | **Leave Logic** | • **`calculateDuration()`**: Accounts for weekends/holidays.<br>• **`checkBalance()`**: Ensures sufficient quota before application. |
| **`AuditService`** | **Auditing** | • **`logAction()`**: Asynchronously writes events to `AUDIT_LOGS` table. |

### 3. Data Access Layer (DAOs)
Files in `com.revworkforce.dao` handle direct database interactions using JDBC. All SQL queries are parameterized to prevent Injection.

| Class | Role | Key Operations |
| :--- | :--- | :--- |
| **`EmployeeDAO`** | **User Data** | • `SELECT` by ID/Email<br>• `INSERT` new hires<br>• `UPDATE` passwords & profile fields. |
| **`LeaveDAO`** | **Leave Data** | • Manage `LEAVE_BALANCES` and `LEAVE_APPLICATIONS`.<br>• Transactional integrity during balance updates. |
| **`PerformanceDAO`** | **Reviews** | • CRUD for `PERFORMANCE_REVIEWS` and `GOALS`.<br>• Aggregation queries for team performance stats. |
| **`AuditLogDAO`** | **Logging** | • Insert-only operations for security logs.<br>• Read-only access for Admin reports. |
| **`DBConnection`** | **Infrastructure** | • Manages JDBC Connection Pool (Singleton pattern). |

### 4. Utilities & Context
Cross-cutting concerns used throughout the application.

| Class | Role | Key Operations |
| :--- | :--- | :--- |
| **`SessionContext`** | **State** | • Stores currently `loggedInUser` object.<br>• Provides global access to current user identity. |
| **`PasswordUtil`** | **Security** | • `hash()`: Generates BCrypt hash.<br>• `check()`: Verifies plain text against hash. |
| **`ValidationUtil`** | **Helpers** | • Regex checks for Email, Phone, and Date formats. |

---

## 🔄 Interaction Flow Examples

### A. Leave Application Flow
```mermaid
sequenceDiagram
    actor Emp as Employee
    participant Menu as EmployeeMenu
    participant Svc as LeaveService
    participant DAO as LeaveDAO
    participant DB as Database

    Emp->>Menu: Selects "Apply Leave"
    Menu->>Emp: Asks Dates & Reason
    Emp->>Menu: Enters Details
    Menu->>Svc: applyLeave(dates, reason)
    Svc->>Svc: checkBalance()
    Svc->>Svc: validateDates()
    Svc->>DAO: insertApplication()
    DAO->>DB: INSERT INTO leave_applications
    DB-->>DAO: Success
    DAO-->>Svc: ID Generated
    Svc-->>Menu: Application Submitted
    Menu-->>Emp: "Leave Applied Successfully"
```

### B. Manager Approval Flow
```mermaid
sequenceDiagram
    actor Mgr as Manager
    participant Menu as ManagerMenu
    participant Svc as ManagerService
    participant DAO as LeaveDAO
    participant DB as Database

    Mgr->>Menu: Selects "Leave Requests"
    Menu->>Svc: getPendingLeaves(mgrId)
    Svc->>DAO: getTeamLeaveRequests(mgrId)
    DAO->>DB: SELECT * FROM leaves WHERE manager_id = ?
    DB-->>DAO: List<Leave>
    DAO-->>Svc: List<Leave>
    Svc-->>Menu: Displays List
    
    Mgr->>Menu: Approves Leave #101
    Menu->>Svc: processLeave(101, "APPROVED")
    Svc->>DAO: updateStatus(101, "APPROVED")
    Svc->>DAO: deductBalance(empId, days)
    DAO->>DB: UPDATE / UPDATE balances
    DB-->>DAO: Success
    Svc-->>Menu: "Leave Approved"
```
