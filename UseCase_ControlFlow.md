# Use Case & Control Flow Documentation

## 1. Use Case Diagrams (by Role)

### 1.1 Admin Use Cases
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

### 1.2 Manager Use Cases
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

### 1.3 Employee Use Cases
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

---

## 2. Detailed Control Flows (Interaction Logic)

### 2.1 Login Control Flow
```mermaid
sequenceDiagram
    actor User
    participant MainMenu
    participant AuthService
    participant EmployeeDAO
    participant AuditService

    User->>MainMenu: Select "Login" (Input ID/Pass)
    MainMenu->>AuthService: login(id, password)
    AuthService->>EmployeeDAO: getAuthDetails(id)
    EmployeeDAO-->>AuthService: Return {hash, status, attempts}

    alt User Not Found
        AuthService-->>MainMenu: Return False (Log WARN)
    else Account Locked
        AuthService-->>MainMenu: Return False (Log WARN)
    else Password Invalid
        AuthService->>EmployeeDAO: recordFailedLogin()
        opt Attempts >= 3
            AuthService->>EmployeeDAO: lockAccount()
            AuthService->>AuditService: log("LOCKOUT")
        end
        AuthService-->>MainMenu: Return False
    else Password Valid
        AuthService->>EmployeeDAO: recordSuccessfulLogin()
        AuthService->>AuditService: log("LOGIN")
        AuthService-->>MainMenu: Return True (Session Set)
        MainMenu->>User: Route to Dashboard
    end
```

### 2.2 Leave Application Control Flow
```mermaid
sequenceDiagram
    actor Employee
    participant LeaveService
    participant LeaveDAO
    participant NotificationService
    participant AuditService

    Employee->>LeaveService: applyLeave(type, start, end, reason)
    LeaveService->>LeaveService: validateDates()
    LeaveService->>LeaveDAO: getLeaveBalances(empId)
    
    alt Insufficient Balance
        LeaveService-->>Employee: Error: "Insufficient Quota"
    else Valid Request
        LeaveService->>LeaveDAO: insertLeaveApplication(PENDING)
        LeaveService->>AuditService: log("APPLY_LEAVE")
        LeaveService->>NotificationService: createNotification(Manager)
        LeaveService-->>Employee: Success: "Application Pending"
    end
```

### 2.3 Leave Approval Control Flow
```mermaid
sequenceDiagram
    actor Manager
    participant ManagerService
    participant LeaveDAO
    participant NotificationService
    participant AuditService

    Manager->>ManagerService: processLeave(reqId, ACTION)
    ManagerService->>ManagerService: verifyAuthority()
    
    alt Action = APPROVE
        ManagerService->>LeaveDAO: updateLeaveStatus(APPROVED)
        ManagerService->>LeaveDAO: deductLeaveBalance()
        ManagerService->>AuditService: log("APPROVE_LEAVE")
    else Action = REJECT
        ManagerService->>LeaveDAO: updateLeaveStatus(REJECTED)
        ManagerService->>AuditService: log("REJECT_LEAVE")
    end
    
    ManagerService->>NotificationService: notifyEmployee(Status Update)
    ManagerService-->>Manager: "Update Successful"
```

### 2.4 Performance Review Control Flow
```mermaid
sequenceDiagram
    participant Admin
    participant PerfCycle
    actor Employee
    actor Manager
    participant PerfService
    participant PerfDAO

    Admin->>PerfCycle: Start New Cycle
    
    rect rgba(0, 0, 0, 0)
    Note right of Employee: Phase 1: Self-Review
    Employee->>PerfService: submitSelfReview()
    PerfService->>PerfDAO: update(Status=SUBMITTED)
    end
    
    rect rgba(0, 0, 0, 0)
    Note right of Manager: Phase 2: Manager Review
    Manager->>PerfService: viewSubmittedReviews()
    Manager->>PerfService: submitManagerFeedback()
    PerfService->>PerfDAO: update(Status=REVIEWED)
    PerfService->>AuditService: log("PERFORMANCE_REVIEW")
    end
```

### 2.5 Audit Logging System Flow
```mermaid
sequenceDiagram
    participant TriggeringService
    participant AuditService
    participant AuditLogDAO
    participant AuditTable

    TriggeringService->>AuditService: log(User, Action, OldVal, NewVal)
    AuditService->>AuditService: Capture Timestamp & IP
    AuditService->>AuditLogDAO: insertLog()
    AuditLogDAO->>AuditTable: INSERT INTO audit_logs
    Note right of AuditLogDAO: Failsafe: Errors logged to stderr
```

### 2.6 Employee Onboarding Flow (Admin)
```mermaid
sequenceDiagram
    actor Admin
    participant AdminService
    participant EmployeeDAO
    participant AuditService
    
    Admin->>AdminService: addEmployee()
    AdminService->>AdminService: Generate ID (MGR/EMP)
    
    loop Validations
        AdminService->>EmployeeDAO: isEmailExists()
        AdminService->>EmployeeDAO: isPhoneExists()
        AdminService->>AdminService: Validate DOB/Dept/Desig
    end
    
    AdminService->>AdminService: Hash Default Password ("password")
    AdminService->>EmployeeDAO: insertEmployee(details)
    AdminService->>AuditService: log("CREATE", "EMPLOYEES")
    AdminService-->>Admin: "Employee Added Successfully"
```

### 2.7 Attendance Check-In Flow
```mermaid
sequenceDiagram
    actor Employee
    participant AttendanceService
    participant AttendanceDAO
    participant AuditService
    
    Employee->>AttendanceService: checkIn(empId)
    AttendanceService->>AttendanceDAO: hasCheckedIn(Today)
    
    alt Already Checked In
        AttendanceService-->>Employee: "Already checked in today"
    else First Check-In
        AttendanceService->>AttendanceDAO: checkIn()
        AttendanceService->>AuditService: log("CREATE", "ATTENDANCE")
        AttendanceService-->>Employee: "Check-in Successful"
    end
```

### 2.8 Password Recovery Flow
```mermaid
sequenceDiagram
    actor User
    participant AuthService
    participant EmployeeDAO
    participant AuditService
    
    User->>AuthService: forgotPasswordFlow()
    AuthService->>EmployeeDAO: getSecurityDetails(empId)
    
    alt No Security Details
        AuthService-->>User: "Contact Admin"
    else Details Found
        AuthService-->>User: Show Security Question
        User->>AuthService: Submit Answer
        AuthService->>AuthService: verifyPassword(Answer, Hash)
        
        alt Incorrect Answer
            AuthService-->>User: "Incorrect Answer"
        else Correct Answer
            User->>AuthService: Set New Password
            AuthService->>EmployeeDAO: updatePassword(NewHash)
            AuthService->>AuditService: log("RECOVER", "ACCESS_CONTROL")
            AuthService-->>User: "Password Reset Successful"
        end
    end
```

### 2.9 Profile Update Flow
```mermaid
sequenceDiagram
    actor Employee
    participant EmployeeService
    participant EmployeeDAO
    participant AuditService
    
    Employee->>EmployeeService: updateProfile()
    EmployeeService->>EmployeeDAO: getProfile()
    EmployeeService-->>Employee: Show Current Details
    
    Employee->>EmployeeService: Input New Phone/Address
    EmployeeService->>EmployeeDAO: updateProfile(NewDetails)
    EmployeeService->>AuditService: log("UPDATE", "EMPLOYEES")
    EmployeeService-->>Employee: "Profile Updated"
```

### 2.10 Goal Management Flow
```mermaid
sequenceDiagram
    actor Employee
    participant PerformanceService
    participant PerformanceDAO
    participant AuditService
    
    Employee->>PerformanceService: manageGoals(empId)
    PerformanceService->>PerformanceDAO: getMyGoals(empId)
    PerformanceService-->>Employee: Display Goals & Progress
    
    Employee->>PerformanceService: Select Goal & Update Progress %
    PerformanceService->>PerformanceDAO: updateGoalProgress(GoalID, %)
    PerformanceService->>AuditService: log("UPDATE", "GOALS")
    PerformanceService-->>Employee: "Goal Updated Successfully"
```

### 2.11 System Configuration Flow (Admin Generic)
```mermaid
sequenceDiagram
    actor Admin
    participant AdminConfigService
    participant GenericDAO
    participant AuditService
    
    Note over GenericDAO: DepartmentDAO / DesignationDAO / PolicyDAO
    
    Admin->>AdminConfigService: manage[Entity]()
    AdminConfigService->>GenericDAO: getAll[Entities]()
    AdminConfigService-->>Admin: Show List
    
    Admin->>AdminConfigService: Select Action (Add/Update/Delete)
    AdminConfigService->>GenericDAO: executeAction(Data)
    AdminConfigService->>AuditService: log("CREATE/UPDATE/DELETE", "ENTITY_TABLE")
    AdminConfigService-->>Admin: "Operation Successful"
```

### 2.12 Manager Revoke Leave Flow
```mermaid
sequenceDiagram
    actor Manager
    participant ManagerService
    participant LeaveDAO
    participant NotificationService
    participant AuditService
    
    Manager->>ManagerService: revokeApprovedLeave(leaveId, reason)
    ManagerService->>LeaveDAO: updateLeaveStatus(REVOKED)
    ManagerService->>NotificationService: notifyLeaveUpdate(Employee, "REVOKED")
    ManagerService->>AuditService: log("REVOKE", "LEAVE_APPLICATIONS")
    ManagerService-->>Manager: "Leave Revoked Successfully"
```

### 2.13 Admin Leave Quota Assignment Flow
```mermaid
sequenceDiagram
    actor Admin
    participant AdminLeaveService
    participant LeaveDAO
    participant AuditService
    
    Admin->>AdminLeaveService: assignLeaveQuotas(empId, type, year, quota)
    AdminLeaveService->>LeaveDAO: assignLeaveQuota()
    LeaveDAO-->>AdminLeaveService: Quota Created/Updated
    
    AdminLeaveService->>AuditService: log("ASSIGN", "LEAVE_BALANCES")
    AdminLeaveService-->>Admin: "Leave Quota Assigned Successfully"
```
