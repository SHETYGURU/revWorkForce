-- =============================================
-- RevWorkForce Database Schema
-- RESET & RECREATE SCRIPT
-- =============================================

-- =============================================
-- 1. DROP EXISTING TABLES (Cleanup)
-- =============================================
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE audit_logs CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE system_policies CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE employee_security CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE security_questions CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE notifications CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE announcements CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE goals CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE performance_reviews CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE performance_cycles CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE attendance CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE holidays CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE leave_applications CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE leave_balances CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE leave_types CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE employee_roles CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE employees CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE roles CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE designations CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE departments CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

-- =============================================
-- 2. CREATE TABLES
-- =============================================

-- Core Lookup Tables
CREATE TABLE departments (
    department_id NUMBER GENERATED ALWAYS AS IDENTITY,
    department_name VARCHAR2(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT pk_departments PRIMARY KEY (department_id),
    CONSTRAINT uk_department_name UNIQUE (department_name)
);

CREATE TABLE designations (
    designation_id NUMBER GENERATED ALWAYS AS IDENTITY,
    designation_name VARCHAR2(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT pk_designations PRIMARY KEY (designation_id)
);

CREATE TABLE roles (
    role_id NUMBER GENERATED ALWAYS AS IDENTITY,
    role_name VARCHAR2(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_roles PRIMARY KEY (role_id),
    CONSTRAINT uk_role_name UNIQUE (role_name)
);

-- Employee Core
CREATE TABLE employees (
    employee_id VARCHAR2(20),
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50),
    email VARCHAR2(100) NOT NULL,
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
    failed_login_attempts NUMBER DEFAULT 0,
    account_locked NUMBER(1) DEFAULT 0,
    last_login TIMESTAMP,

    CONSTRAINT pk_employees PRIMARY KEY (employee_id),
    CONSTRAINT uk_employee_email UNIQUE (email),
    CONSTRAINT emp_fk_department FOREIGN KEY (department_id) REFERENCES departments(department_id),
    CONSTRAINT emp_fk_designation FOREIGN KEY (designation_id) REFERENCES designations(designation_id),
    CONSTRAINT emp_fk_manager FOREIGN KEY (manager_id) REFERENCES employees(employee_id)
);

CREATE TABLE employee_roles (
    employee_role_id NUMBER GENERATED ALWAYS AS IDENTITY,
    employee_id VARCHAR2(20) NOT NULL,
    role_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_employee_roles PRIMARY KEY (employee_role_id),
    CONSTRAINT fk_er_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT fk_er_role FOREIGN KEY (role_id) REFERENCES roles(role_id),
    CONSTRAINT uk_employee_role UNIQUE (employee_id, role_id)
);

CREATE TABLE leave_types (
    leave_type_id NUMBER GENERATED ALWAYS AS IDENTITY,
    leave_type_name VARCHAR2(50) NOT NULL,
    description VARCHAR2(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_leave_types PRIMARY KEY (leave_type_id),
    CONSTRAINT uk_leave_type_name UNIQUE (leave_type_name)
);

CREATE TABLE leave_balances (
    leave_balance_id NUMBER GENERATED ALWAYS AS IDENTITY,
    employee_id VARCHAR2(20) NOT NULL,
    leave_type_id NUMBER NOT NULL,
    year NUMBER(4) NOT NULL,
    total_allocated NUMBER(3),
    used_leaves NUMBER(3),
    available_leaves NUMBER(3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT pk_leave_balances PRIMARY KEY (leave_balance_id),
    CONSTRAINT lb_fk_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT lb_fk_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(leave_type_id),
    CONSTRAINT lb_uk_emp_type_year UNIQUE (employee_id, leave_type_id, year)
);

CREATE TABLE leave_applications (
    leave_application_id NUMBER GENERATED ALWAYS AS IDENTITY,
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

    CONSTRAINT pk_leave_applications PRIMARY KEY (leave_application_id),
    CONSTRAINT la_fk_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT la_fk_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types(leave_type_id),
    CONSTRAINT la_fk_reviewer FOREIGN KEY (reviewed_by) REFERENCES employees(employee_id)
);

CREATE TABLE holidays (
    holiday_id NUMBER GENERATED ALWAYS AS IDENTITY,
    holiday_name VARCHAR2(100),
    holiday_date DATE NOT NULL,
    year NUMBER(4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_holidays PRIMARY KEY (holiday_id)
);

CREATE TABLE attendance (
    attendance_id NUMBER GENERATED ALWAYS AS IDENTITY,
    employee_id VARCHAR2(20) NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_attendance PRIMARY KEY (attendance_id),
    CONSTRAINT fk_att_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT uk_attendance UNIQUE (employee_id, attendance_date)
);

CREATE TABLE performance_cycles (
    cycle_id NUMBER GENERATED ALWAYS AS IDENTITY,
    year NUMBER(4) NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR2(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_perf_cycles PRIMARY KEY (cycle_id),
    CONSTRAINT uk_cycle_year UNIQUE (year)
);

CREATE TABLE performance_reviews (
    review_id NUMBER GENERATED ALWAYS AS IDENTITY,
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

    CONSTRAINT pk_perf_reviews PRIMARY KEY (review_id),
    CONSTRAINT pr_fk_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT pr_fk_cycle FOREIGN KEY (cycle_id) REFERENCES performance_cycles(cycle_id),
    CONSTRAINT pr_fk_reviewer FOREIGN KEY (reviewed_by) REFERENCES employees(employee_id)
);

CREATE TABLE goals (
    goal_id NUMBER GENERATED ALWAYS AS IDENTITY,
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

    CONSTRAINT pk_goals PRIMARY KEY (goal_id),
    CONSTRAINT fk_goal_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT fk_goal_cycle FOREIGN KEY (cycle_id) REFERENCES performance_cycles(cycle_id)
);

CREATE TABLE announcements (
    announcement_id NUMBER GENERATED ALWAYS AS IDENTITY,
    title VARCHAR2(200),
    content CLOB,
    posted_by VARCHAR2(20),
    posted_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_announcements PRIMARY KEY (announcement_id),
    CONSTRAINT fk_announcement_emp FOREIGN KEY (posted_by) REFERENCES employees(employee_id)
);

CREATE TABLE notifications (
    notification_id NUMBER GENERATED ALWAYS AS IDENTITY,
    employee_id VARCHAR2(20),
    notification_type VARCHAR2(50),
    message VARCHAR2(1000),
    is_read NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,

    CONSTRAINT pk_notifications PRIMARY KEY (notification_id),
    CONSTRAINT notif_fk_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

CREATE TABLE security_questions (
    question_id NUMBER GENERATED ALWAYS AS IDENTITY,
    question_text VARCHAR2(200) NOT NULL,
    
    CONSTRAINT pk_security_questions PRIMARY KEY (question_id)
);

CREATE TABLE employee_security (
    employee_security_id NUMBER GENERATED ALWAYS AS IDENTITY,
    employee_id VARCHAR2(20) NOT NULL,
    question_id NUMBER NOT NULL,
    answer_hash VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_employee_security PRIMARY KEY (employee_security_id),
    CONSTRAINT fk_es_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT fk_es_question FOREIGN KEY (question_id) REFERENCES security_questions(question_id)
);

CREATE TABLE system_policies (
    policy_id NUMBER GENERATED ALWAYS AS IDENTITY,
    policy_name VARCHAR2(100) NOT NULL,
    policy_value CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT pk_system_policies PRIMARY KEY (policy_id),
    CONSTRAINT uk_policy_name UNIQUE (policy_name)
);

CREATE TABLE audit_logs (
    log_id NUMBER GENERATED ALWAYS AS IDENTITY,
    employee_id VARCHAR2(20),
    action VARCHAR2(100),
    table_name VARCHAR2(50),
    column_name VARCHAR2(100),
    record_id VARCHAR2(50),
    old_value CLOB,
    new_value CLOB,
    ip_address VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_logs PRIMARY KEY (log_id),
    CONSTRAINT fk_audit_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);
