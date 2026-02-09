# RevWorkForce HRMS

**RevWorkForce** is an enterprise-grade, console-based Human Resource Management System (HRMS) engineered to streamline organizational workforce management. Built on a robust Java architecture with Oracle Database integration, it delivers a secure, scalable, and feature-rich platform for managing the complete employee lifecycle.

---

## 📋 Table of Contents
1.  [System Architecture](#-system-architecture)
2.  [Key Features & Modules](#-key-features--modules)
3.  [Technology Stack](#-technology-stack)
4.  [Database Schema & Design](#-database-schema--design)
5.  [Application Flow & Navigation](#-application-flow--navigation)
6.  [Security & Compliance](#-security--compliance)
7.  [Setup & Installation Guide](#-setup--installation-guide)
8.  [Testing & Quality Assurance](#-testing--quality-assurance)

---

## 🏗 System Architecture

RevWorkForce follows a layered architecture pattern to ensure separation of concerns, maintainability, and scalability.

For a detailed breakdown of class roles, responsibilities, and interaction flows, please refer to the **[Detailed Architecture Documentation](detailed_architecture.md)** and **[ARCHITECTURE.md](ARCHITECTURE.md)**.

```mermaid
graph TD
    User((User)) -->|Interacts| UI["Console Menus (com.revworkforce.menu)"]
    UI -->|Calls| Service["Service Layer (com.revworkforce.service)"]
    Service -->|Validates/Processes| Model["Domain Models (com.revworkforce.model)"]
    Service -->|Invokes| DAO["Data Access Layer (com.revworkforce.dao)"]
    DAO -->|JDBC| DB[(Oracle Database)]
    
    subgraph Utilities
    Util["Utils: DBConnection, InputUtil, ValidationUtil"]
    end
    
    Service -.-> Util
    DAO -.-> Util
```

### Components
- **Presentation Layer (`com.revworkforce.menu`)**: Handles user input and displays menus.
- **Service Layer (`com.revworkforce.service`)**: Contains business logic, validation rules, and transaction management.
- **Data Access Layer (`com.revworkforce.dao`)**: Manages direct database interactions using JDBC `PreparedStatement` for security.
- **Model Layer (`com.revworkforce.model`)**: POJOs representing database entities.

---

## 🚀 Key Features & Modules

### 1. Core HR Management
*   **Employee Onboarding**:
    *   **Automated ID Generation**: System-generated IDs based on roles (e.g., `MGR001`, `EMP004`).
    *   **Profile Management**: Captures extensive details including emergency contacts and department allocation.
*   **Role-Based Access Control (RBAC)**:
    *   **Admin**: Full system control, configuration, and user management.
    *   **Manager**: Team oversight, leave approvals, and performance reviews.
    *   **Employee**: Self-service for leaves, attendance, and profile updates.

### 2. Leave Management System
*   **Dynamic Leave Types**: Configurable leave types (Sick, Casual, Earned) via `LeaveType` model.
*   **Smart Balance Tracking**: Automatic deduction and balance verification logic in `LeaveService`.
*   **Approval Workflow**:
    *   Employees submit applications via `LeaveApplication`.
    *   Managers receive notifications for pending requests.
    *   Approvals/Rejections automatically update leave balances.

### 3. Performance Management
*   **Cycle-Based Reviews**: `PerformanceCycle` entity manages review periods (e.g., "Q1 2024").
*   **360-Degree Feedback**:
    *   **Self-Assessment**: Employees rate their own performance.
    *   **Manager Evaluation**: Managers provide ratings and detailed feedback.
*   **Goal Tracking**: `Goal` entity allows setting and tracking progress of specific objectives (#1 priority).

### 4. Attendance & Reporting
*   **Clock-In/Out**: Real-time attendance tracking with timestamp logging.
*   **Audit Logging**: `AuditService` records every critical action (LOGIN, UPDATE, DELETE) with IP tracking.
*   **Reports**: Administration reports for workforce analytics.

### 5. Communication Hub
*   **Announcements**: Admin-broadcasted messages visible to all employees.
*   **Notifications**: Targeted alerts for specific user actions (e.g., `Leave Approved`).

---

## 💻 Technology Stack

| Component | Technology | Version | Description |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 (LTS) / 24 | Core application logic (Lambda, Streams support). |
| **Database** | Oracle DB | 19c / 21c / 23c | Relational data persistence. |
| **Build Tool** | Maven | 3.8+ | Dependency management and build automation. |
| **Testing** | JUnit 5 | 5.9.2 | Unit testing framework. |
| **Mocking** | Mockito | 5.14.2 | Mocking framework for isolated unit tests. |
| **Logging** | Log4j2 | 2.22.0 | Asynchronous auditing and error logging. |
| **Security** | BCrypt | 0.4 | Password hashing and salt generation. |

---

## 🗄 Database Schema & Design

The database is normalized to 3NF to ensure data integrity. Validations and relationships are enforced at the database level.

For a complete Entity Relationship Diagram (ERD) and detailed schema explanation, please refer to **[DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)**.

### Key Entity Relationships
1.  **Employees ↔ Departments**: Many-to-One. One department has many employees.
2.  **Employees ↔ Roles**: Many-to-Many (managed via `employee_roles`).
3.  **Employees ↔ LeaveApplications**: One-to-Many. An employee can have multiple leave requests.
4.  **Employees ↔ PerformanceReviews**: One-to-Many. Reviews are linked to specific cycles.

### Core Tables
*   `EMPLOYEES`: Master table for user data.
*   `LEAVE_BALANCES`: Tracks remaining leaves per type per year.
*   `AUDIT_LOGS`: Security-critical table for immutable action history.
*   `SYSTEM_POLICIES`: Key-value store for dynamic system configuration.

---

## 📱 Application Flow & Navigation

The application uses specific Menu classes to guide users based on their role.

For comprehensive visualizations of user journeys, including Mermaid sequence diagrams for key processes (Login, Leave Approval, Performance Review, etc.), please refer to **[UseCase_ControlFlow.md](UseCase_ControlFlow.md)**.

### 1. Main Entry (`MainMenu.java`)
*   **Login**: Authenticates user credentials.
*   **Password Recovery**: "Forgot Password" flow using `SecurityQuestions`.

### 2. Admin Dashboard (`AdminMenu.java`)
*   `[1]` **User Management**: Add Employee, Update Roles, Unlock Accounts.
*   `[2]` **Department/Designation Config**: Manage foundational structures.
*   `[3]` **Leave Config**: Define new leave types.
*   `[4]` **Audit Logs**: View system-wide activity history.
*   `[5]` **System Policies**: Configure global settings.

### 3. Manager Dashboard (`ManagerMenu.java`)
*   `[1]` **My Team**: View direct reports and their details.
*   `[2]` **Leave Requests**: Approve/Reject pending applications.
*   `[3]` **Performance Reviews**: Conduct evaluations for team members.
*   `[4]` **Goal Management**: Assign goals to subordinates.

### 4. Employee Dashboard (`EmployeeMenu.java`)
*   `[1]` **My Profile**: View personal details and leave balances.
*   `[2]` **Apply Leave**: Submit new leave requests.
*   `[3]` **Attendance**: Mark daily attendance.
*   `[4]` **My Goals**: Update progress on assigned tasks.

---

## 🔐 Security & Compliance

### Authentication Security
*   **Hashing**: Passwords are **never** stored in plain text. We use `BCrypt` with salt to hash passwords.
*   **Account Locking**: Accounts are automatically locked after **3 failed login attempts**.
*   **Session Management**: `UserSession` ensures only one active user context per terminal instance.

### System Auditing
*   Every data modification (INSERT/UPDATE/DELETE) is intercepted by `AuditService`.
*   Logs include: `Actor ID`, `Action Type`, `Target Table`, `Old Value`, `New Value`, `Timestamp`.

---

## ⚙ Setup & Installation Guide

### Prerequisites
1.  **Java JDK 17+**
2.  **Oracle Database** (XE or Enterprise)
3.  **Maven**

### Installation Steps

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/your-org/revworkforce.git
    cd revworkforce
    ```

2.  **Database Configuration**
    *   Open `src/main/java/com/revworkforce/util/DBConnection.java`.
    *   Update the `URL`, `USER`, and `PASSWORD` constants.
    *   Execute SQL scripts in order:
        ```sql
        @schema.sql             -- Creates tables
        @insert_leave_types.sql -- Seeding leave types
        @data.sql               -- Seeding test users
        ```

3.  **Build the Project**
    ```bash
    mvn clean install
    ```

4.  **Run the Application**
    ```bash
    java -jar target/revworkforce-1.0-SNAPSHOT.jar
    ```

---

## 🧪 Testing & Quality Assurance

We use a robust testing strategy combining Unit Tests and Integration Tests.

For a detailed guide on our testing strategy, frameworks used, and how to execute tests, please refer to **[TESTING.md](TESTING.md)**.

### Running Tests
To execute the full test suite via Maven:
```bash
mvn test
```

### Key Test Classes
*   **`EmployeeDAOTest`**: Verifies database CRUD operations using **Mockito** to mock `DBConnection`.
*   **`DateUtilTest`**: Validates date calculation logic for leave duration and cycles.
*   **`AuthServiceTest`**: Tests login logic, account locking, and password hashing compatibility.

---

**© 2026 RevWorkForce Systems. All Rights Reserved.**
