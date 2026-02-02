-- =============================================
-- RevWorkForce Database Schema
-- =============================================

-- 1. Core Lookup Tables
CREATE TABLE departments (
    department_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    department_name VARCHAR2(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE designations (
    designation_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    designation_name VARCHAR2(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE roles (
    role_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_name VARCHAR2(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Employee Table (Core)
CREATE TABLE employees (
    employee_id VARCHAR2(20) PRIMARY KEY,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50),
    email VARCHAR2(100) UNIQUE NOT NULL,
    phone VARCHAR2(15),
    address VARCHAR2(200),
    emergency_contact VARCHAR2(100),
    date_of_birth DATE,
    department_id NUMBER,
    designation_id NUMBER,
    manager_id VARCHAR2(20),
    joining_date DATE,
    salary NUMBER(10,2),
    password_hash VARCHAR2(255) NOT NULL,
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    -- Security Columns (Added via Alter previously)
    failed_login_attempts NUMBER DEFAULT 0,
    account_locked NUMBER(1) DEFAULT 0,
    last_login TIMESTAMP,

    CONSTRAINT emp_fk_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id),

    CONSTRAINT emp_fk_designation
        FOREIGN KEY (designation_id)
        REFERENCES designations(designation_id),

    CONSTRAINT emp_fk_manager_self
        FOREIGN KEY (manager_id)
        REFERENCES employees(employee_id)
);

CREATE TABLE employee_roles (
    employee_role_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    role_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_er_employee FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT fk_er_role FOREIGN KEY (role_id)
        REFERENCES roles(role_id),

    CONSTRAINT uk_employee_role UNIQUE (employee_id, role_id)
);

-- 3. Leave Management
CREATE TABLE leave_types (
    leave_type_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    leave_type_name VARCHAR2(50) UNIQUE NOT NULL,
    description VARCHAR2(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE leave_balances (
    leave_balance_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    leave_type_id NUMBER NOT NULL,
    year NUMBER(4) NOT NULL,
    total_allocated NUMBER(3),
    used_leaves NUMBER(3),
    available_leaves NUMBER(3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT lb_fk_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT lb_fk_leave_type
        FOREIGN KEY (leave_type_id)
        REFERENCES leave_types(leave_type_id),

    CONSTRAINT lb_uk_emp_type_year
        UNIQUE (employee_id, leave_type_id, year)
);

CREATE TABLE leave_applications (
    leave_application_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    leave_type_id NUMBER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days NUMBER(3),
    reason VARCHAR2(500),
    status VARCHAR2(20),
    manager_comments VARCHAR2(500),
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_date TIMESTAMP,
    reviewed_by VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT la_fk_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT la_fk_leave_type
        FOREIGN KEY (leave_type_id)
        REFERENCES leave_types(leave_type_id),

    CONSTRAINT la_fk_reviewer_employee
        FOREIGN KEY (reviewed_by)
        REFERENCES employees(employee_id)
);

CREATE TABLE holidays (
    holiday_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    holiday_name VARCHAR2(100),
    holiday_date DATE NOT NULL,
    year NUMBER(4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Attendance & Performance
CREATE TABLE attendance (
    attendance_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_att_employee FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT uk_attendance UNIQUE (employee_id, attendance_date)
);

CREATE TABLE performance_cycles (
    cycle_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    year NUMBER(4) UNIQUE NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE performance_reviews (
    review_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    cycle_id NUMBER NOT NULL,
    key_deliverables CLOB,
    major_accomplishments CLOB,
    areas_of_improvement CLOB,
    self_assessment_rating NUMBER(2,1),
    manager_feedback CLOB,
    manager_rating NUMBER(2,1),
    status VARCHAR2(20),
    submitted_date TIMESTAMP,
    reviewed_date TIMESTAMP,
    reviewed_by VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT pr_fk_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT pr_fk_cycle
        FOREIGN KEY (cycle_id)
        REFERENCES performance_cycles(cycle_id),

    CONSTRAINT pr_fk_reviewer_employee
        FOREIGN KEY (reviewed_by)
        REFERENCES employees(employee_id)
);

CREATE TABLE goals (
    goal_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    cycle_id NUMBER NOT NULL,
    goal_description VARCHAR2(1000),
    deadline DATE,
    priority VARCHAR2(20),
    success_metrics VARCHAR2(500),
    progress_percentage NUMBER(3),
    status VARCHAR2(20),
    manager_comments VARCHAR2(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_goal_employee FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT fk_goal_cycle FOREIGN KEY (cycle_id)
        REFERENCES performance_cycles(cycle_id)
);

-- 5. System & Utilities
CREATE TABLE announcements (
    announcement_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR2(200),
    content CLOB,
    posted_by VARCHAR2(20),
    posted_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_announcement_employee FOREIGN KEY (posted_by)
        REFERENCES employees(employee_id)
);

CREATE TABLE notifications (
    notification_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20),
    notification_type VARCHAR2(50),
    message VARCHAR2(1000),
    is_read NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,

    CONSTRAINT notif_fk_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
);

CREATE TABLE security_questions (
    question_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_text VARCHAR2(200) NOT NULL
);

CREATE TABLE employee_security (
    employee_security_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20) NOT NULL,
    question_id NUMBER NOT NULL,
    answer_hash VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_es_employee FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT fk_es_question FOREIGN KEY (question_id)
        REFERENCES security_questions(question_id)
);

CREATE TABLE system_policies (
    policy_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    policy_name VARCHAR2(100) UNIQUE NOT NULL,
    policy_value CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE audit_logs (
    log_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id VARCHAR2(20),
    action VARCHAR2(100),
    table_name VARCHAR2(50),
    column_name VARCHAR2(100),
    record_id VARCHAR2(50),
    old_value CLOB,
    new_value CLOB,
    ip_address VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_employee FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id)
);
