/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.context.SessionContext;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.model.Employee;
import com.revworkforce.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

/**
 * Service class for handling Authentication and Security.
 * Manages login, password verification, account locking, and session
 * initialization.
 * 
 * @author Gururaj Shetty
 */
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private static final int MAX_ATTEMPTS = 3;
    private static EmployeeDAO dao = new EmployeeDAO();

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
                logger.warn("Failed login attempt - User not found: {}", empId);
                return false;
            }

            if (rs.getInt("account_locked") == 1) {
                System.out.println("Account is locked. Please contact admin.");
                logger.warn("Failed login attempt - Account Locked: {}", empId);
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
            logger.info("User logged in successfully: {}", empId);
            AuditService.log(empId, "LOGIN", "SESSION", "N/A", "User logged in successfully");

            // Show Unread Notifications
            int unreadCount = NotificationService.getUnreadCount(empId);
            if (unreadCount > 0) {
                System.out.println("\n**************************************************");
                System.out.println(" NOTICE: You have " + unreadCount + " unread notification(s).");
                System.out.println("**************************************************\n");
            }

            return true;

        } catch (Exception e) {
            logger.error("System Error during login: " + e.getMessage(), e);
            logger.error("System Error during login for user: {}", empId, e);
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
            logger.warn("Account locked due to max failed attempts: {}", empId);
            AuditService.log(empId, "LOCKOUT", "ACCESS_CONTROL", "N/A", "Account locked due to max failed attempts");
        } else {
            System.out.println("Invalid credentials");
            System.out.println("Attempts remaining: " + (MAX_ATTEMPTS - (currentFailures + 1)));
            logger.warn("Failed login attempt - Incorrect Password: {}", empId);
        }
    }

    /**
     * Changes the password for a logged-in user.
     * Enforces security by verifying the old password before allowing a change.
     *
     * @param empId       The employee ID.
     * @param oldPassword The current password provided by the user.
     * @param newPassword The new password to be set.
     * @return true if change successful, false otherwise.
     */
    public static boolean changePassword(String empId, String oldPassword, String newPassword) {
        try {
            // 1. Verify Old Password
            ResultSet rs = dao.getAuthDetails(empId);
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (!PasswordUtil.verifyPassword(oldPassword, hash)) {
                    System.out.println("Incorrect current password.");
                    logger.warn("Password change failed - Incorrect old password: {}", empId);
                    return false;
                }

                // 2. Hash New Password
                String newHash = PasswordUtil.hashPassword(newPassword);

                // 3. Update Password
                dao.updatePassword(empId, newHash);
                logger.info("Password changed successfully for user: {}", empId);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error changing password: " + e.getMessage(), e);
            logger.error("Error changing password for user: {}", empId, e);
            return false;
        }
    }

    /**
     * Initiates the Forgot Password recovery flow.
     * prompts for Employee ID and Security Answer to reset the password.
     */
    public static void forgotPasswordFlow() {
        try {
            System.out.println("\n--- FORGOT PASSWORD RECOVERY ---");
            String empId = com.revworkforce.util.InputUtil.readString("Enter Employee ID: ");

            // Fetch Security Question
            ResultSet rs = dao.getSecurityDetails(empId);
            if (rs.next()) {
                String question = rs.getString("question_text");
                String storedAnswerHash = rs.getString("answer_hash");

                System.out.println("Security Question: " + question);
                String answer = com.revworkforce.util.InputUtil.readString("Answer: ");

                // Verify Answer
                if (PasswordUtil.verifyPassword(answer, storedAnswerHash)) {
                    System.out.println("Answer Correct!");

                    String newPass = com.revworkforce.util.InputUtil.readString("Enter New Password: ");
                    String confirmPass = com.revworkforce.util.InputUtil.readString("Confirm New Password: ");

                    if (!newPass.equals(confirmPass)) {
                        System.out.println("Passwords do not match. Recovery failed.");
                        logger.warn("Password recovery failed - Passwords do not match: {}", empId);
                        return;
                    }

                    String newHash = PasswordUtil.hashPassword(newPass);
                    dao.updatePassword(empId, newHash);
                    System.out.println("Password reset successfully. Please login.");
                    logger.info("Password recovered/reset successfully for user: {}", empId);
                    AuditService.log(empId, "RECOVER", "ACCESS_CONTROL", "N/A",
                            "Password recovered via security question");
                } else {
                    System.out.println("Incorrect Answer.");
                    logger.warn("Password recovery failed - Incorrect Security Answer: {}", empId);
                }

            } else {
                System.out.println("No security details found for this user. Please contact Admin.");
                logger.warn("Password recovery failed - No security details found: {}", empId);
            }

        } catch (Exception e) {
            logger.error("Error during password recovery: " + e.getMessage(), e);
            logger.error("Error during password recovery flow", e);
        }
    }
}
