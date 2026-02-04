-- Insert default leave types if they don't exist
INSERT INTO leave_types (leave_type_name, description)
SELECT 'CL', 'Casual Leave' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM leave_types WHERE leave_type_name = 'CL');

INSERT INTO leave_types (leave_type_name, description)
SELECT 'SL', 'Sick Leave' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM leave_types WHERE leave_type_name = 'SL');

INSERT INTO leave_types (leave_type_name, description)
SELECT 'PL', 'Paid Leave' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM leave_types WHERE leave_type_name = 'PL');

INSERT INTO leave_types (leave_type_name, description)
SELECT 'Privilege Leave', 'Privilege Leave' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM leave_types WHERE leave_type_name = 'Privilege Leave');

COMMIT;
