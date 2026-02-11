/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.AnnouncementDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.util.InputUtil;
import com.revworkforce.util.MessageConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

/**
 * Service class for Employee-related operations.
 * Handles profile viewing, updates, directory search, and miscellaneous
 * employee features.
 * 
 * @author Gururaj Shetty
 */
public class EmployeeService {

    private static final Logger logger = LogManager.getLogger(EmployeeService.class);

    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static AnnouncementDAO announcementDAO = new AnnouncementDAO();

    /**
     * Displays the full profile of an employee.
     *
     * @param empId The employee ID to view.
     */
    public static void viewProfile(String empId) {
        try {
            ResultSet rs = employeeDAO.getProfile(empId);
            if (rs.next()) {
                System.out.println("\n--- PROFILE ---");
                System.out.println("ID: " + rs.getString("employee_id"));
                System.out.println("Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("Emergency Contact: " + rs.getString("emergency_contact"));
                System.out.println("Manager ID: " + rs.getString("manager_id"));
            } else {
                System.out.println("Employee not found.");
            }
        } catch (Exception e) {
            logger.error("Error fetching profile: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch profile. " + e.getMessage());
        }
    }

    /**
     * Prompts the user to update their contact information.
     *
     * @param empId The employee ID.
     */
    public static void updateProfile(String empId) {
        try {
            // Fetch current details
            ResultSet rs = employeeDAO.getProfile(empId);
            String currentPhone = "";
            String currentAddress = "";
            String currentEmergency = "";

            if (rs.next()) {
                currentPhone = rs.getString("phone");
                currentAddress = rs.getString("address");
                currentEmergency = rs.getString("emergency_contact");
            }

            // Handle nulls for display
            if (currentPhone == null)
                currentPhone = "";
            if (currentAddress == null)
                currentAddress = "";
            if (currentEmergency == null)
                currentEmergency = "";

            System.out.println("Enter new details (press Enter to keep existing value):");

            String phoneInput = InputUtil.readString("Phone [" + currentPhone + "]: ");
            String newPhone = phoneInput.isEmpty() ? currentPhone : phoneInput;

            String addressInput = InputUtil.readString("Address [" + currentAddress + "]: ");
            String newAddress = addressInput.isEmpty() ? currentAddress : addressInput;

            String emergencyInput = InputUtil.readString("Emergency Contact [" + currentEmergency + "]: ");
            String newEmergency = emergencyInput.isEmpty() ? currentEmergency : emergencyInput;

            employeeDAO.updateProfile(empId, newPhone, newAddress, newEmergency);
            AuditService.log(empId, "UPDATE", "EMPLOYEES", empId, "Profile updated");

            System.out.println("Profile updated successfully");
        } catch (Exception e) {
            logger.error("Profile update failed: " + e.getMessage(), e);
            System.out.println("Error: Profile update failed. " + e.getMessage());
        }
    }

    /**
     * Prompts the user to change their password securely.
     * Requires current password verification before allowing the update.
     *
     * @param empId The employee ID of the user changing the password.
     */
    public static void changePassword(String empId) {
        try {
            System.out.println("\n--- CHANGE PASSWORD ---");
            String oldPass = InputUtil.readString("Current Password: ");
            String newPass = InputUtil.readString("New Password: ");
            String confirmPass = InputUtil.readString("Confirm New Password: ");

            if (!newPass.equals(confirmPass)) {
                System.out.println("New passwords do not match.");
                return;
            }

            if (newPass.isEmpty()) {
                System.out.println("Password cannot be empty.");
                return;
            }

            // Call AuthService to handle verification and update
            boolean success = AuthService.changePassword(empId, oldPass, newPass);

            if (success) {
                System.out.println("Password changed successfully.");
                AuditService.log(empId, "UPDATE", "EMPLOYEES", empId, "Password changed");
            } else {
                // AuthService prints specific errors (incorrect old password)
            }

        } catch (Exception e) {
            logger.error("Password change failed: " + e.getMessage(), e);
            System.out.println("Error: Password change failed. " + e.getMessage());
        }
    }

    /**
     * Displays details of the employee's reporting manager.
     *
     * @param empId The employee ID.
     */
    public static void viewManagerDetails(String empId) {
        try {
            // 1. Get Employee's Manager ID
            String managerId = null;
            ResultSet empRs = employeeDAO.getProfile(empId);
            if (empRs.next()) {
                managerId = empRs.getString("manager_id");
            }

            if (managerId == null || managerId.isEmpty()) {
                System.out.println("\nNo reporting manager assigned.");
                return;
            }

            // 2. Get Manager's Profile
            ResultSet mgrRs = employeeDAO.getProfile(managerId);
            if (mgrRs.next()) {
                System.out.println("\n--- MANAGER DETAILS ---");
                System.out.println("Name: " + mgrRs.getString("first_name") + " " + mgrRs.getString("last_name"));
                System.out.println("Email: " + mgrRs.getString("email"));
                System.out.println("Phone: " + mgrRs.getString("phone"));
            } else {
                System.out.println("Manager details not found.");
            }

        } catch (Exception e) {
            logger.error("Error fetching manager details: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch manager details. " + e.getMessage());
        }
    }

    /**
     * Displays upcoming birthdays of colleagues within the next 30 days.
     * Helps in fostering team morale and engagement.
     */
    public static void viewUpcomingBirthdays() {
        try {
            ResultSet rs = employeeDAO.getUpcomingBirthdays();

            System.out.println("\n--- UPCOMING BIRTHDAYS (Next 30 Days) ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("first_name") +
                                " | DOB: " + rs.getDate("date_of_birth") // Ideally format MM-DD
                );
            }
            if (!found)
                System.out.println("No upcoming birthdays.");

        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "birthdays: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch upcoming birthdays. " + e.getMessage());
        }
    }

    /**
     * Displays work anniversaries occurring in the next 30 days.
     */
    public static void viewWorkAnniversaries() {
        try {
            ResultSet rs = employeeDAO.getWorkAnniversaries();

            System.out.println("\n--- WORK ANNIVERSARIES (Next 30 Days) ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("first_name") +
                                " | Joined: " + rs.getDate("joining_date"));
            }
            if (!found)
                System.out.println("No upcoming anniversaries.");

        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "anniversaries: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch work anniversaries. " + e.getMessage());
        }
    }

    /**
     * Displays general company announcements sorted by date.
     */
    public static void viewAnnouncements() {
        try {
            ResultSet rs = announcementDAO.getAllAnnouncements();

            System.out.println("\n--- ANNOUNCEMENTS ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Date: " + rs.getDate("posted_date"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Content: " + rs.getString("content"));
                System.out.println("-------------------------");
            }
            if (!found)
                System.out.println("No announcements.");

        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "announcements: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch announcements. " + e.getMessage());
        }
    }

    /**
     * Searches for employees in the directory by name, ID, or email.
     * Displays a tabulated list of matching results.
     */
    /**
     * Searches for employees in the directory by name, ID, or email.
     * Displays a tabulated list of matching results.
     */
    public static void employeeDirectory() {
        String keyword = InputUtil.readString("Enter Name, ID, or Email to search: ");
        try {
            java.util.List<java.util.Map<String, Object>> list = employeeDAO.searchEmployees(keyword);

            if (list.isEmpty()) { // Check if list is empty
                System.out.println("No matching employees found.");
            } else {
                System.out.println("\n--- EMPLOYEE DIRECTORY ---");
                for (java.util.Map<String, Object> row : list) {
                    System.out.println(
                            row.get("employee_id") + " | " +
                                    row.get("first_name") + " "
                                    + (row.get("last_name") != null ? row.get("last_name") : "") + " | " +
                                    row.get("email") + " | " +
                                    row.get("department_name") + " | " +
                                    row.get("designation_name"));
                }
            }
        } catch (Exception e) {
            logger.error("Error searching employees: " + e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
        }
    }
}
