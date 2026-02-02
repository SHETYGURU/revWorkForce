package com.revworkforce.service;

import com.revworkforce.context.SessionContext;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.model.Employee;
import com.revworkforce.util.PasswordUtil;

import java.sql.ResultSet;

/**
 * Service class for handling Authentication and Security.
 * Manages login, password verification, and account locking mechanisms.
 */
public class AuthService {

    private static final int MAX_ATTEMPTS = 3;
    private static final EmployeeDAO dao = new EmployeeDAO();

    /**
     * Authenticates a user based on Employee ID and Password.
     * Checks for account locks and tracks failed attempts.
     *
     * @param empId    The Employee ID.
     * @param password The raw password input.
     * @return true if login is successful, false otherwise.
     */
    public static boolean login(String empId, String password) {

        try {
            ResultSet rs = dao.getAuthDetails(empId);

            if (!rs.next()) {
                System.out.println("Invalid credentials");
                return false;
            }

            if (rs.getInt("account_locked") == 1) {
                System.out.println("Account is locked. Please contact admin.");
                return false;
            }

            String hash = rs.getString("password_hash");
            int failedAttempts = rs.getInt("failed_login_attempts");

            // Verify Password
            if (!PasswordUtil.verifyPassword(password, hash)) {
                handleFailedLogin(empId, failedAttempts);
                return false;
            }

            // SUCCESS
            dao.recordSuccessfulLogin(empId);
            Employee emp = dao.getEmployeeById(empId);
            SessionContext.set(emp);

            return true;

        } catch (Exception e) {
            System.err.println("System Error during login: " + e.getMessage());
            return false;
        }
    }

    /*
     * private helper to handle failed login logic
     */
    private static void handleFailedLogin(String empId, int currentFailures) throws Exception {
        dao.recordFailedLogin(empId);

        if (currentFailures + 1 >= MAX_ATTEMPTS) {
            dao.lockAccount(empId);
            System.out.println("Account locked due to multiple failed attempts.");
        } else {
            System.out.println("Invalid credentials");
            System.out.println("Attempts remaining: " + (MAX_ATTEMPTS - (currentFailures + 1)));
        }
    }
}
