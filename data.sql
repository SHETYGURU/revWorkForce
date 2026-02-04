-- =============================================
-- RevWorkForce Master Data (Exhaustive & Robust)
-- Password for all users: "password"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- =============================================

-- 1. Departments --
INSERT INTO departments (department_name) VALUES ('Engineering');
INSERT INTO departments (department_name) VALUES ('Human Resources');
INSERT INTO departments (department_name) VALUES ('Sales');
INSERT INTO departments (department_name) VALUES ('Marketing');
INSERT INTO departments (department_name) VALUES ('Finance');
INSERT INTO departments (department_name) VALUES ('Operations');
INSERT INTO departments (department_name) VALUES ('IT Support');

-- 2. Designations --
INSERT INTO designations (designation_name) VALUES ('Software Engineer');
INSERT INTO designations (designation_name) VALUES ('Senior Software Engineer');
INSERT INTO designations (designation_name) VALUES ('Tech Lead');
INSERT INTO designations (designation_name) VALUES ('Engineering Manager');
INSERT INTO designations (designation_name) VALUES ('HR Executive');
INSERT INTO designations (designation_name) VALUES ('HR Manager');
INSERT INTO designations (designation_name) VALUES ('Sales Associate');
INSERT INTO designations (designation_name) VALUES ('Sales Manager');
INSERT INTO designations (designation_name) VALUES ('Marketing Specialist');
INSERT INTO designations (designation_name) VALUES ('Financial Analyst');
INSERT INTO designations (designation_name) VALUES ('SysAdmin');
INSERT INTO designations (designation_name) VALUES ('Intern');

-- 3. Roles --
INSERT INTO roles (role_name) VALUES ('Admin');
INSERT INTO roles (role_name) VALUES ('Manager');
INSERT INTO roles (role_name) VALUES ('Employee');

-- 4. Employees --

-- === ADMINS ===
-- ID: ADMIN001 (HR Manager)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, salary, password_hash)
VALUES ('ADMIN001', 'Super', 'Admin', 'admin@revworkforce.com', '9990000001', 'HQ, First Floor',
       (SELECT department_id FROM departments WHERE department_name = 'Human Resources'),
       (SELECT designation_id FROM designations WHERE designation_name = 'HR Manager'),
       95000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- === MANAGERS ===

-- ID: MGR001 (Engineering Manager) - Has efficient team
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, salary, password_hash)
VALUES ('MGR001', 'John', 'Tech', 'john.tech@revworkforce.com', '9870000001', 'Silicon Valley, CA',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Engineering Manager'),
       140000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- ID: MGR002 (Sales Manager) - Has struggling team
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, salary, password_hash)
VALUES ('MGR002', 'Sarah', 'Sales', 'sarah.sales@revworkforce.com', '9870000002', 'Wall Street, NY',
       (SELECT department_id FROM departments WHERE department_name = 'Sales'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Sales Manager'),
       130000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- ID: MGR003 (Finance Manager) - Small team
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, salary, password_hash)
VALUES ('MGR003', 'Mike', 'Money', 'mike.money@revworkforce.com', '9870000003', 'Downtown, Chicago',
       (SELECT department_id FROM departments WHERE department_name = 'Finance'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Financial Analyst'), -- Acting Manager
       110000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- === EMPLOYEES (Engineering Team -> MGR001) ===

-- EMP001 (Star Performer)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP001', 'Alice', 'Coder', 'alice.coder@revworkforce.com', '9120000001', 'San Jose, CA',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Senior Software Engineer'),
       'MGR001', TO_DATE('2022-01-10', 'YYYY-MM-DD'), 100000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- EMP002 (New Joiner)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP002', 'Bob', 'Junior', 'bob.junior@revworkforce.com', '9120000002', 'Oakland, CA',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Software Engineer'),
       'MGR001', TO_DATE('2025-01-05', 'YYYY-MM-DD'), 70000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- EMP003 (Tech Lead)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP003', 'Charlie', 'Lead', 'charlie.lead@revworkforce.com', '9120000003', 'San Francisco, CA',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Tech Lead'),
       'MGR001', TO_DATE('2020-03-15', 'YYYY-MM-DD'), 125000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- === EMPLOYEES (Sales Team -> MGR002) ===

-- EMP004 (Mid-level)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP004', 'David', 'Dealer', 'david.dealer@revworkforce.com', '9120000004', 'Bronx, NY',
       (SELECT department_id FROM departments WHERE department_name = 'Sales'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Sales Associate'),
       'MGR002', TO_DATE('2023-06-01', 'YYYY-MM-DD'), 60000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- EMP005 (Underperformer)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP005', 'Eve', 'Slacker', 'eve.slacker@revworkforce.com', '9120000005', 'Queens, NY',
       (SELECT department_id FROM departments WHERE department_name = 'Sales'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Sales Associate'),
       'MGR002', TO_DATE('2024-01-01', 'YYYY-MM-DD'), 58000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- === EMPLOYEES (Finance -> MGR003) ===

-- EMP006
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP006', 'Frank', 'Count', 'frank.count@revworkforce.com', '9120000006', 'Loop, Chicago',
       (SELECT department_id FROM departments WHERE department_name = 'Finance'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Financial Analyst'),
       'MGR003', TO_DATE('2022-11-11', 'YYYY-MM-DD'), 85000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- === INACTIVE / FORMER EMPLOYEES ===

-- EMP007 (Resigned)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash, is_active)
VALUES ('EMP007', 'Grace', 'Gone', 'grace.gone@revworkforce.com', '9120000007', 'Austin, TX',
       (SELECT department_id FROM departments WHERE department_name = 'Operations'),
       (SELECT designation_id FROM designations WHERE designation_name = 'SysAdmin'),
       'MGR001', TO_DATE('2021-01-01', 'YYYY-MM-DD'), 80000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0);

-- === INTERNS ===

-- EMP008 (Intern -> MGR001)
INSERT INTO employees (employee_id, first_name, last_name, email, phone, address, department_id, designation_id, manager_id, joining_date, salary, password_hash)
VALUES ('EMP008', 'Harry', 'Potter', 'harry.intern@revworkforce.com', '9120000008', 'London, UK',
       (SELECT department_id FROM departments WHERE department_name = 'Engineering'),
       (SELECT designation_id FROM designations WHERE designation_name = 'Intern'),
       'MGR001', TO_DATE('2025-05-01', 'YYYY-MM-DD'), 30000, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');


-- 5. Employee Roles --
INSERT INTO employee_roles (employee_id, role_id) VALUES ('ADMIN001', (SELECT role_id FROM roles WHERE role_name = 'Admin'));

INSERT INTO employee_roles (employee_id, role_id) VALUES ('MGR001', (SELECT role_id FROM roles WHERE role_name = 'Manager'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('MGR001', (SELECT role_id FROM roles WHERE role_name = 'Employee'));

INSERT INTO employee_roles (employee_id, role_id) VALUES ('MGR002', (SELECT role_id FROM roles WHERE role_name = 'Manager'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('MGR002', (SELECT role_id FROM roles WHERE role_name = 'Employee'));

INSERT INTO employee_roles (employee_id, role_id) VALUES ('MGR003', (SELECT role_id FROM roles WHERE role_name = 'Manager'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('MGR003', (SELECT role_id FROM roles WHERE role_name = 'Employee'));

-- Bulk Employee Roles
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP001', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP002', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP003', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP004', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP005', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP006', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP007', (SELECT role_id FROM roles WHERE role_name = 'Employee'));
INSERT INTO employee_roles (employee_id, role_id) VALUES ('EMP008', (SELECT role_id FROM roles WHERE role_name = 'Employee'));


-- 6. Leave Types --
INSERT INTO leave_types (leave_type_name, description) VALUES ('Casual Leave', 'For personal matters');
INSERT INTO leave_types (leave_type_name, description) VALUES ('Sick Leave', 'Medical leave');
INSERT INTO leave_types (leave_type_name, description) VALUES ('Privileged Leave', 'Earned usually after a year');
INSERT INTO leave_types (leave_type_name, description) VALUES ('Maternity/Paternity', 'Parental leave');
INSERT INTO leave_types (leave_type_name, description) VALUES ('Unpaid Leave', 'Loss of Pay');

-- 7. Leave Balances (2025) --
-- Logic: Assign casual/sick to everyone manually for control
INSERT INTO leave_balances (employee_id, leave_type_id, year, total_allocated, used_leaves, available_leaves)
SELECT employee_id, (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave'), 2025, 12, 0, 12 FROM employees;

INSERT INTO leave_balances (employee_id, leave_type_id, year, total_allocated, used_leaves, available_leaves)
SELECT employee_id, (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Sick Leave'), 2025, 10, 0, 10 FROM employees;

-- Specific Adjustments
UPDATE leave_balances SET used_leaves = 5, available_leaves = 7 WHERE employee_id = 'EMP001' AND leave_type_id = (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave');
UPDATE leave_balances SET used_leaves = 10, available_leaves = 0 WHERE employee_id = 'EMP005' AND leave_type_id = (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Sick Leave');


-- 8. Leave Applications --

-- PENDING (EMP002 - New Joiner asking for leave)
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status)
VALUES ('EMP002', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Sick Leave'), 
        TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-03-02', 'YYYY-MM-DD'), 2, 'Flu', 'PENDING');

-- APPROVED (EMP001 - Past trip)
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status, reviewed_by, reviewed_date, manager_comments)
VALUES ('EMP001', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave'), 
        TO_DATE('2025-01-15', 'YYYY-MM-DD'), TO_DATE('2025-01-20', 'YYYY-MM-DD'), 5, 'Vacation', 'APPROVED', 'MGR001', SYSDATE-30, 'Enjoy!');

-- REJECTED (EMP005 - Frequent absentee)
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status, reviewed_by, reviewed_date, manager_comments)
VALUES ('EMP005', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Casual Leave'), 
        TO_DATE('2025-02-14', 'YYYY-MM-DD'), TO_DATE('2025-02-14', 'YYYY-MM-DD'), 1, 'Valentines Day', 'REJECTED', 'MGR002', SYSDATE-5, 'Critical project delivery date.');

-- APPROVED (MGR001 - Future Conference)
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status, reviewed_by, reviewed_date)
VALUES ('MGR001', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Privileged Leave'), 
        TO_DATE('2025-06-10', 'YYYY-MM-DD'), TO_DATE('2025-06-15', 'YYYY-MM-DD'), 5, 'Tech Conference', 'APPROVED', 'ADMIN001', SYSDATE-10);

-- PENDING (EMP003 - Long Leave)
INSERT INTO leave_applications (employee_id, leave_type_id, start_date, end_date, total_days, reason, status)
VALUES ('EMP003', (SELECT leave_type_id FROM leave_types WHERE leave_type_name = 'Maternity/Paternity'), 
        TO_DATE('2025-08-01', 'YYYY-MM-DD'), TO_DATE('2025-10-01', 'YYYY-MM-DD'), 60, 'Paternity Leave', 'PENDING');


-- 9. Holidays --
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('New Year', TO_DATE('2025-01-01', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('MLK Day', TO_DATE('2025-01-20', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Memorial Day', TO_DATE('2025-05-26', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Independence Day', TO_DATE('2025-07-04', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Labor Day', TO_DATE('2025-09-01', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Thanksgiving', TO_DATE('2025-11-27', 'YYYY-MM-DD'), 2025);
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES ('Christmas', TO_DATE('2025-12-25', 'YYYY-MM-DD'), 2025);

-- 10. Attendance --
-- Today
INSERT INTO attendance (employee_id, attendance_date, check_in_time, status) VALUES ('EMP001', SYSDATE, SYSTIMESTAMP - 0.35, 'PRESENT'); -- 8 AM
INSERT INTO attendance (employee_id, attendance_date, check_in_time, status) VALUES ('EMP002', SYSDATE, SYSTIMESTAMP - 0.30, 'PRESENT'); -- 9 AM
INSERT INTO attendance (employee_id, attendance_date, check_in_time, status) VALUES ('EMP003', SYSDATE, SYSTIMESTAMP - 0.25, 'LATE'); -- 10 AM
INSERT INTO attendance (employee_id, attendance_date, status) VALUES ('EMP004', SYSDATE, 'ABSENT');
INSERT INTO attendance (employee_id, attendance_date, check_in_time, status) VALUES ('MGR001', SYSDATE, SYSTIMESTAMP - 0.38, 'PRESENT');

-- Yesterday
INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status) 
VALUES ('EMP001', SYSDATE-1, SYSTIMESTAMP - 1.35, SYSTIMESTAMP - 0.9, 'PRESENT');

INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status) 
VALUES ('EMP002', SYSDATE-1, SYSTIMESTAMP - 1.30, SYSTIMESTAMP - 0.8, 'PRESENT');


-- 11. Performance Cycles --
INSERT INTO performance_cycles (year, start_date, end_date, status) VALUES (2024, TO_DATE('2024-01-01', 'YYYY-MM-DD'), TO_DATE('2024-12-31', 'YYYY-MM-DD'), 'COMPLETED');
INSERT INTO performance_cycles (year, start_date, end_date, status) VALUES (2025, TO_DATE('2025-01-01', 'YYYY-MM-DD'), TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'ACTIVE');


-- 12. Goals (2025) --
-- EMP001 (High Performer)
INSERT INTO goals (employee_id, cycle_id, goal_description, deadline, priority, progress_percentage, status)
VALUES ('EMP001', (SELECT cycle_id FROM performance_cycles WHERE year=2025), 'Lead Migration to Cloud', TO_DATE('2025-06-30', 'YYYY-MM-DD'), 'HIGH', 40, 'IN_PROGRESS');

INSERT INTO goals (employee_id, cycle_id, goal_description, deadline, priority, progress_percentage, status)
VALUES ('EMP001', (SELECT cycle_id FROM performance_cycles WHERE year=2025), 'Mentor 2 Juniors', TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'MEDIUM', 10, 'IN_PROGRESS');

-- EMP002 (Junior)
INSERT INTO goals (employee_id, cycle_id, goal_description, deadline, priority, progress_percentage, status)
VALUES ('EMP002', (SELECT cycle_id FROM performance_cycles WHERE year=2025), 'Complete Onboarding', TO_DATE('2025-02-28', 'YYYY-MM-DD'), 'HIGH', 90, 'IN_PROGRESS');

-- EMP005 (Underperformer)
INSERT INTO goals (employee_id, cycle_id, goal_description, deadline, priority, progress_percentage, status)
VALUES ('EMP005', (SELECT cycle_id FROM performance_cycles WHERE year=2025), 'Improve Sales Calls', TO_DATE('2025-03-30', 'YYYY-MM-DD'), 'URGENT', 0, 'NOT_STARTED');

-- MGR001
INSERT INTO goals (employee_id, cycle_id, goal_description, deadline, priority, progress_percentage, status)
VALUES ('MGR001', (SELECT cycle_id FROM performance_cycles WHERE year=2025), 'Reduce Dept Budget by 10%', TO_DATE('2025-12-31', 'YYYY-MM-DD'), 'HIGH', 25, 'IN_PROGRESS');


-- 13. Performance Reviews (2024 - Past Cycle) --
INSERT INTO performance_reviews (employee_id, cycle_id, key_deliverables, major_accomplishments, areas_of_improvement, self_assessment_rating, manager_feedback, manager_rating, status, submitted_date, reviewed_date, reviewed_by)
VALUES ('EMP001', (SELECT cycle_id FROM performance_cycles WHERE year=2024), 'Auth System', 'Zero bugs in prod', 'Delegation', 4.5, 'Exceptional year.', 4.7, 'REVIEWED', TO_DATE('2024-12-15', 'YYYY-MM-DD'), TO_DATE('2024-12-20', 'YYYY-MM-DD'), 'MGR001');

INSERT INTO performance_reviews (employee_id, cycle_id, key_deliverables, major_accomplishments, areas_of_improvement, self_assessment_rating, manager_feedback, manager_rating, status, submitted_date, reviewed_date, reviewed_by)
VALUES ('EMP003', (SELECT cycle_id FROM performance_cycles WHERE year=2024), 'Team Mgmt', 'Kept morale high', 'Technical depth', 4.0, 'Solid leadership.', 4.1, 'REVIEWED', TO_DATE('2024-12-10', 'YYYY-MM-DD'), TO_DATE('2024-12-22', 'YYYY-MM-DD'), 'MGR001');

-- 2025 Draft/Submitted
INSERT INTO performance_reviews (employee_id, cycle_id, key_deliverables, major_accomplishments, areas_of_improvement, self_assessment_rating, status, submitted_date)
VALUES ('EMP001', (SELECT cycle_id FROM performance_cycles WHERE year=2025), 'Q1 Goals met', 'Cloud migration started', 'None', 4.5, 'SUBMITTED', SYSDATE);


-- 14. Announcements --
INSERT INTO announcements (title, content, posted_by, posted_date)
VALUES ('System Maintenance', 'Server maintenance scheduled for Sunday 2 AM.', 'ADMIN001', SYSDATE-2);

INSERT INTO announcements (title, content, posted_by, posted_date)
VALUES ('New Holiday Policy', 'Additional optional holiday added to the calendar.', 'ADMIN001', SYSDATE-10);

INSERT INTO announcements (title, content, posted_by, posted_date)
VALUES ('Congratulations EMP001', 'Employee of the Month!', 'MGR001', SYSDATE-15);

INSERT INTO announcements (title, content, posted_by, posted_date)
VALUES ('Sales Target Hit', 'Q1 Sales targets exceeded. Pizza party on Friday.', 'MGR002', SYSDATE-1);

-- 15. Notifications --
INSERT INTO notifications (employee_id, notification_type, message, is_read, created_at)
VALUES ('EMP001', 'TASK_ASSIGNED', 'New Jira ticket assigned.', 0, SYSDATE);

INSERT INTO notifications (employee_id, notification_type, message, is_read, created_at)
VALUES ('EMP002', 'WELCOME', 'Welcome to the team!', 1, SYSDATE-20);

INSERT INTO notifications (employee_id, notification_type, message, is_read, created_at)
VALUES ('MGR001', 'APPROVAL_REQ', 'Leave request from EMP002 pending.', 0, SYSDATE);

-- 16. System Policies --
INSERT INTO system_policies (policy_name, policy_value) VALUES ('WORK_HOURS', '9:00 AM - 6:00 PM');
INSERT INTO system_policies (policy_name, policy_value) VALUES ('PROBATION_PERIOD', '6 Months');
INSERT INTO system_policies (policy_name, policy_value) VALUES ('NOTICE_PERIOD', '2 Months');
INSERT INTO system_policies (policy_name, policy_value) VALUES ('CASUAL_LEAVE_LIMIT', '12');
INSERT INTO system_policies (policy_name, policy_value) VALUES ('WFH_POLICY', 'Allowed 2 days a week');

-- 17. Security Questions --
INSERT INTO security_questions (question_text) VALUES ('What is your pet name?');
INSERT INTO security_questions (question_text) VALUES ('What is your mother''s maiden name?');
INSERT INTO security_questions (question_text) VALUES ('What was the name of your first school?');
INSERT INTO security_questions (question_text) VALUES ('What is your favorite food?');
INSERT INTO security_questions (question_text) VALUES ('What city were you born in?');

-- 18. Employee Security --
INSERT INTO employee_security (employee_id, question_id, answer_hash)
SELECT employee_id, 1, 'hashed_answer' FROM employees; 

-- 19. Audit Logs --
INSERT INTO audit_logs (employee_id, action, table_name, record_id, old_value, new_value, ip_address)
VALUES ('ADMIN001', 'CREATE', 'EMPLOYEES', 'EMP001', NULL, 'Created Employee Alice', '192.168.1.1');

INSERT INTO audit_logs (employee_id, action, table_name, record_id, old_value, new_value, ip_address)
VALUES ('MGR001', 'UPDATE', 'LEAVE_APPLICATIONS', '101', 'PENDING', 'APPROVED', '192.168.1.50');

COMMIT;
