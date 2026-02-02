package com.revworkforce.service;

import com.revworkforce.dao.AnnouncementDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.exception.AppException;
import com.revworkforce.util.InputUtil;

import java.sql.ResultSet;

/**
 * Service class for Employee-related operations.
 * Handles profile viewing, updates, directory search, and miscellaneous
 * employee features.
 */
public class EmployeeService {

    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final AnnouncementDAO announcementDAO = new AnnouncementDAO();

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
            System.err.println("Error fetching profile: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to update their contact information.
     *
     * @param empId The employee ID.
     */
    public static void updateProfile(String empId) {
        try {
            String phone = InputUtil.readString("New Phone: ");
            String address = InputUtil.readString("New Address: ");
            String emergency = InputUtil.readString("Emergency Contact: ");

            employeeDAO.updateProfile(empId, phone, address, emergency);
            AuditService.log(empId, "UPDATE", "EMPLOYEES", empId, "Profile updated");

            System.out.println("Profile updated successfully");
        } catch (Exception e) {
            System.err.println("Profile update failed: " + e.getMessage());
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
            System.err.println("Error fetching manager details: " + e.getMessage());
        }
    }

    /**
     * Displays upcoming birthdays of colleagues.
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
            System.err.println("Unable to fetch birthdays: " + e.getMessage());
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
            System.err.println("Unable to fetch anniversaries: " + e.getMessage());
        }
    }

    /**
     * Displays company announcements.
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
            System.err.println("Unable to fetch announcements: " + e.getMessage());
        }
    }

    /**
     * Searches for employees by name or ID.
     */
    public static void employeeDirectory() {
        String keyword = InputUtil.readString("Search by name, ID, or email: ");

        try {
            ResultSet rs = employeeDAO.searchEmployees(keyword);

            System.out.println("\n--- EMPLOYEE DIRECTORY ---");
            System.out.printf("%-10s | %-20s | %-25s | %-15s | %-15s%n", "ID", "Name", "Email", "Department",
                    "Designation");
            System.out.println(
                    "------------------------------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                String name = rs.getString("first_name") + " "
                        + (rs.getString("last_name") != null ? rs.getString("last_name") : "");
                System.out.printf("%-10s | %-20s | %-25s | %-15s | %-15s%n",
                        rs.getString("employee_id"),
                        name,
                        rs.getString("email"),
                        rs.getString("department_name") != null ? rs.getString("department_name") : "N/A",
                        rs.getString("designation_name") != null ? rs.getString("designation_name") : "N/A");
            }
            if (!found)
                System.out.println("No employees found matching: " + keyword);

        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }
}
