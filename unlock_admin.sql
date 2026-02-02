-- Unlock Admin Account
UPDATE employees 
SET account_locked = 0, 
    failed_login_attempts = 0 
WHERE employee_id = 'ADMIN001';

COMMIT;
