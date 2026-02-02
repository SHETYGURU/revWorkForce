-- =============================================
-- RevWorkForce Master Data
-- Password for all users: "password"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- =============================================

-- 1. Departments --
INSERT INTO departments (department_name) VALUES ('Engineering');
INSERT INTO departments (department_name) VALUES ('Human Resources');
INSERT INTO departments (department_name) VALUES ('Sales');
INSERT INTO departments (department_name) VALUES ('Marketing');
INSERT INTO departments (department_name) VALUES ('Finance');

-- 2. Designations --
INSERT INTO designations (designation_name) VALUES ('Software Engineer');
INSERT INTO designations (designation_name) VALUES ('Senior Software Engineer');
INSERT INTO designations (designation_name) VALUES ('Tech Lead');
INSERT INTO designations (designation_name) VALUES ('Engineering Manager');
INSERT INTO designations (designation_name) VALUES ('HR Executive');
INSERT INTO designations (designation_name) VALUES ('HR Manager');
INSERT INTO designations (designation_name) VALUES ('Sales Associate');

-- 3. Roles --
INSERT INTO roles (role_name) VALUES ('Admin');
INSERT INTO roles (role_name) VALUES ('Manager');
INSERT INTO roles (role_name) VALUES ('Employee');

-- 4. Employees --
-- Admin (HR Manager)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, salary, password_hash)
VALUES ('ADMIN001', 'Admin', 'User', 'admin@revworkforce.com', '9999999999', 'HQ', 
       (SELECT department_id FROM departments WHERE department_name = 'Human Resources'),
       (SELECT designation_id FROM designations WHERE designation_name = 'HR Manager'),
       90000,
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Engineering Manager
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, salary, password_hash)
VALUES ('MGR001', 'John', 'Doe', 'john.doe@revworkforce.com', '9876543210', 'Tech Park',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Engineering Manager'),
       120000,
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Employee 1 (Reports to MGR001)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP001', 'Alice', 'Smith', 'alice.smith@revworkforce.com', '9123456780', 'City Center',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Software Engineer'),
       'MGR001',
       TO_DATE('2023-01-15', 'YYYY-MM-DD'),
       75000,
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Employee 2 (Reports to MGR001)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP002', 'Bob', 'Jones', 'bob.jones@revworkforce.com', '9123456781', 'Uptown',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Senior Software Engineer'),
       'MGR001',
       TO_DATE('2022-05-10', 'YYYY-MM-DD'),
       95000,
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Assigning Roles to Employees --
-- Admin Role
INSERT INTO employee_roles (employee_id, role_id)
VALUES ('ADMIN001', (SELECT role_id FROM roles WHERE role_name = 'Admin'));

-- Manager Role + Employee Role
INSERT INTO employee_roles (employee_id, role_id)
VALUES ('MGR001', (SELECT role_id FROM roles WHERE role_name = 'Manager'));
INSERT INTO employee_roles (employee_id, role_id)
VALUES ('MGR001', (SELECT role_id FROM roles WHERE role_name = 'Employee'));

-- Employee Role
INSERT INTO employee_roles (employee_id, role_id)
VALUES ('EMP001', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id)
VALUES ('EMP002', (SELECT role_id FROM roles WHERE role_name = 'Employee'));


-- 5. Leave Types --
INSERT INTO leave_types (leave_type_name, description) VALUES ('Casual Leave', 'For personal matters');
INSERT INTO leave_types (leave_type_name, description) VALUES ('Sick Leave', 'Medical leave');
INSERT INTO leave_types (leave_type_name, description) VALUES ('Privileged Leave', 'Earned usually after a year');

-- 6. Leave Balances (2025) --
INSERT INTO leave_balances (employee_id, leave_type_id, year, total_allocated, used_leaves, available_leaves)
VALUES ('EMP001', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave'), 2025, 12, 2, 10);
INSERT INTO leave_balances (employee_id, leave_type_id, year, total_allocated, used_leaves, available_leaves)
VALUES ('EMP001', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Sick Leave'), 2025, 10, 0, 10);

INSERT INTO leave_balances (employee_id, leave_type_id, year, total_allocated, used_leaves, available_leaves)
VALUES ('MGR001', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave'), 2025, 12, 5, 7);

-- 7. Leave Applications --
-- Pending Request
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status)
VALUES ('EMP001', 
        (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave'),
        TO_DATE('2025-06-10', 'YYYY-MM-DD'),
        TO_DATE('2025-06-12', 'YYYY-MM-DD'),
        3, 'Family vacation', 'PENDING');

-- Approved Request
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status, reviewed_by, reviewed_date)
VALUES ('EMP002',
        (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Sick Leave'),
        TO_DATE('2025-02-01', 'YYYY-MM-DD'),
        TO_DATE('2025-02-02', 'YYYY-MM-DD'),
        2, 'Fever', 'APPROVED', 'MGR001', CURRENT_TIMESTAMP);


-- 8. Holidays --
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('New Year', TO_DATE('2025-01-01', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Independence Day', TO_DATE('2025-08-15', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Christmas', TO_DATE('2025-12-25', 'YYYY-MM-DD'), 2025);

-- 9. Attendance --
INSERT INTO attendance (employee_id, attendance_date, check_in_time, status)
VALUES ('EMP001', CURRENT_DATE, CURRENT_TIMESTAMP, 'PRESENT');

-- 10. Performance Cycles --
INSERT INTO performance_cycles (year, start_date, end_date, status)
VALUES (2025, TO_DATE('2025-01-01', 'YYYY-MM-DD'), TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'ACTIVE');

-- 11. Goals --
INSERT INTO goals (employee_id, cycle_id, goal_description, deadline, priority, progress_percentage, status)
VALUES ('EMP001', 
        (SELECT cycle_id FROM performance_cycles WHERE year = 2025),
        'Complete Java Certification',
        TO_DATE('2025-09-30', 'YYYY-MM-DD'),
        'HIGH', 20, 'IN_PROGRESS');

-- 12. Announcements --
INSERT INTO announcements (title, content, posted_by, posted_date)
VALUES ('Welcome to RevWorkForce', 'We are excited to launch the new HRMS system!', 'ADMIN001', CURRENT_TIMESTAMP);

-- 13. System Policies --
INSERT INTO system_policies (policy_name, policy_value) VALUES ('WORK_HOURS', '9:00 AM - 6:00 PM');
INSERT INTO system_policies (policy_name, policy_value) VALUES ('Probation_Period', '6 Months');

COMMIT;
