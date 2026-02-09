# RevWorkForce Design Documentation

## 1. Architecture Overview
The application follows a standard layered architecture for a Java Console Application, utilizing the DAO (Data Access Object) pattern for database interactions.

### Architecture Diagram
```mermaid
graph TD
    subgraph Presentation_Layer [Console UI]
        Main[Main Entry Point]
        MainMenu[MainMenu Login]
        AdminMenu[Admin Dashboard]
        MgrMenu[Manager Dashboard]
        EmpMenu[Employee Dashboard]
    end

    subgraph Service_Layer [Business Logic]
        AuthService
        AdminService
        ManagerService
        EmployeeService
        LeaveService
        PerfService[PerformanceService]
        AuditService
    end

    subgraph Data_Access_Layer [Persistence]
        EmpDAO[EmployeeDAO]
        LeaveDAO
        DeptDAO[DepartmentDAO]
        PerfDAO[PerformanceDAO]
        AuditDAO[AuditLogDAO]
        DBConn[DBConnection]
    end

    subgraph Database [Oracle DB]
        Tables[(Tables: employees, leaves, audit_logs...)]
    end

    Main --> MainMenu
    MainMenu --> AuthService
    MainMenu -.-> AdminMenu
    MainMenu -.-> MgrMenu
    MainMenu -.-> EmpMenu

    AdminMenu --> AdminService
    MgrMenu --> ManagerService
    EmpMenu --> EmployeeService
    EmpMenu --> LeaveService
    EmpMenu --> PerfService

    AdminService --> EmpDAO
    AdminService --> DeptDAO
    AdminService --> AuditService
    
    ManagerService --> LeaveDAO
    ManagerService --> PerfDAO
    ManagerService --> EmpDAO
    
    EmployeeService --> EmpDAO
    LeaveService --> LeaveDAO
    
    AuthService --> EmpDAO
    AuditService --> AuditDAO
    
    EmpDAO --> DBConn
    LeaveDAO --> DBConn
    AuditDAO --> DBConn
    
    DBConn --> Tables
```

## 2. Use Case Diagrams

### 2.1 Admin Use Cases
```mermaid
graph LR
    Admin((Admin))

    subgraph Employee_Management
        U1(Add New Employee)
        U2(Update Details)
        U3(View/Search Employees)
        U4(Assign Manager)
        U5(Activate/Unlock User)
        U6(Reset Password)
    end

    subgraph Start_Leave_Config
        U7(Config Leave Types/Quotas)
        U8(Adjust/Revoke Leave)
        U9(Leave Reports/Holidays)
    end

    subgraph System_Config
        U10(Manage Depts/Designations)
        U11(Perf Cycle Config)
        U12(System Policies)
        U13(View Audit Logs)
    end
    
    subgraph Admin_Actions
        U14(View Notifications)
        U15(Run Daily Job)
        U16(Change Password)
    end

    Admin --> Employee_Management
    Admin --> Start_Leave_Config
    Admin --> System_Config
    Admin --> Admin_Actions
```

### 2.2 Manager Use Cases
```mermaid
graph LR
    Manager((Manager))

    subgraph Team_Management
        M1(My Team / Profiles)
        M2(Team Attendance)
        M3(Team Goals/Progress)
    end

    subgraph Leave_Management
        M4(Approve/Reject Leave)
        M5(Revoke Approved Leave)
        M6(Team Balances/Calendar)
    end

    subgraph Performance_Mgmt
        M7(Review Team Performance)
        M8(Perf Summary Report)
    end

    subgraph Manager_Actions
        M9(View Notifications)
        M10(Change Password)
    end

    Manager --> Team_Management
    Manager --> Leave_Management
    Manager --> Performance_Mgmt
    Manager --> Manager_Actions
```

### 2.3 Employee Use Cases
```mermaid
graph LR
    Employee((Employee))

    subgraph Profile_Info
        E1(View/Update Profile)
        E2(View Manager Info)
        E3(Directory/Announcements)
    end

    subgraph Leave_Mgmt
        E4(View Balance/History)
        E5(Apply/Cancel Leave)
        E6(Holiday Calendar)
    end

    subgraph Performance
        E7(Submit Self Review)
        E8(Manage Goals)
        E9(View Feedback)
    end

    subgraph Actions
        E10(View Notifications)
        E11(Change Password)
        E12(Upcoming Birthdays)
    end

    Employee --> Profile_Info
    Employee --> Leave_Mgmt
    Employee --> Performance
    Employee --> Actions
```

## 3. Class Roles & Responsibilities

### Presentation Layer (Menus)
| Class | Responsibility |
|-------|----------------|
| `Main` | Application entry point. Initializes DB connection and acts as the bootstrap. |
| `MainMenu` | Handles initial user authentication (Login, Forgot Password) and routes users to their role-specific dashboard. |
| `AdminMenu` | UI for Administrators. Provides options for system configuration, employee onboarding, and audit viewing. |
| `ManagerMenu` | UI for Managers. Focuses on team oversight, leave approvals, and performance reviews. |
| `EmployeeMenu` | UI for standard Employees. Allows profile viewing, leave application, and self-performance tracking. |

### Service Layer (Business Logic)
| Class | Responsibility |
|-------|----------------|
| `AuthService` | Manages authentication, session handling, password hashing/verification, and account locking. |
| `AdminService` | Aggregates admin operations: onboarding employees, managing master data (departments, designations), and viewing system logs. |
| `ManagerService` | Logic for team management: fetching direct reports, processing leave requests, and handling performance reviews. |
| `EmployeeService` | basic employee operations: profile view/update, password change, and directory search. |
| `LeaveService` | Manages leave logic: applying for leave, calculating balances, and validating dates. |
| `PerformanceService`| Handles the performance review cycle, self-reviews, and goal management. |
| `AuditService` | Centralized service for logging critical system actions to the `audit_logs` table. |
| `NotificationService`| Manages system alerts and notifications (birthdays, leave status updates). |

### Data Access Layer (DAO)
| Class | Responsibility |
|-------|----------------|
| `EmployeeDAO` | CRUD operations for `employees` table. Handles profile fetching and credential verification. |
| `LeaveDAO` | CRUD operations for `leave_applications` and `leave_balances`. |
| `AuditLogDAO` | Inserts and reads from `audit_logs`. |
| `DepartmentDAO` | Manages `departments` master data. |
| `DesignationDAO`  | Manages `designations` master data. |
| `PerformanceDAO` | Handles `performance_reviews` and `goals`. |
| `DBConnection` | Manages the JDBC connection to the Oracle database. |

## 4. Detailed User Flows

### A. Admin Flow: Adding a New Employee
**Scenario:** An Admin logs in and onboards a new hire.

1.  **Start:** Admin selects "Add New Employee" from `AdminMenu`.
2.  **Input:** System prompts for:
    *   Manager Status (creates ID prefix `MGR` or `EMP`) -> Generates unique ID (e.g., `EMP005`).
    *   Personal Details: Name, Email, Phone, Address.
    *   **Validation:** Service checks if Email/Phone already exists using `EmployeeDAO`.
    *   Professional Details: Department, Designation, Manager ID, Salary.
    *   **Validation:** Verifies referenced IDs exist.
3.  **Processing:**
    *   `AdminService` generates a default password hash (default: "password").
    *   Calls `EmployeeDAO.insertEmployee()`.
    *   Calls `AuditService.log()` to record the "CREATE" action.
4.  **End:** Success message displayed. Employee can now log in.

### B. Manager Flow: Approving Leave
**Scenario:** A Manager reviews and approves a pending leave request from a direct report.

1.  **Start:** Manager selects "Manage Leave Requests" from `ManagerMenu`.
2.  **View:** System calls `ManagerService.viewTeamLeaveRequests()`.
    *   `LeaveDAO` fetches requests where `emp_id` is in the manager's team AND status is `PENDING`.
3.  **Action:** Manager enters the `Leave ID` to process.
    *   System validates the ID belongs to their team.
4.  **Decision:** Manager chooses Access (A) or Reject (R) and enters comments.
5.  **Processing:**
    *   `ManagerService.processLeave()` is called.
    *   Updates leave status in DB via `LeaveDAO`.
    *   Calls `NotificationService` to alert the employee.
    *   Calls `AuditService.log()` to record the decision.
6.  **End:** Success message displayed.

### C. Employee Flow: Applying for Leave
**Scenario:** An employee applies for sick leave.

1.  **Start:** Employee selects "Apply for Leave" from `EmployeeMenu`.
2.  **Input:**
    *   Leave Type ID (e.g., 1 for Sick Leave).
    *   Start Date and End Date.
    *   Reason.
3.  **Validation:** `LeaveService` checks if End Date is after Start Date.
4.  **Processing:**
    *   `LeaveService.applyLeave()` is called.
    *   `LeaveDAO` inserts a new record with status `PENDING`.
    *   Calls `AuditService.log()` to record the application.
5.  **End:** Success message "Leave applied successfully. Status: PENDING".
