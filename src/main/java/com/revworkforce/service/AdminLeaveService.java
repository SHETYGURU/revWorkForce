/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.LeavePolicyDAO;
import com.revworkforce.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service for Admin Leave Management tasks.
 * Handles configuration of leave types, quota assignments, and leave reporting.
 * 
 * @author Gururaj Shetty
 */
public class AdminLeaveService {

    private static final Logger logger = LogManager.getLogger(AdminLeaveService.class);

    private static LeavePolicyDAO policyDAO = new LeavePolicyDAO();
    private static LeaveDAO leaveDAO = new LeaveDAO();

    private static String getAdminId() {
        return com.revworkforce.context.SessionContext.get() != null
                ? com.revworkforce.context.SessionContext.get().getEmployeeId()
                : "ADMIN001"; // Fallback
    }

    /**
     * Configures a new Leave Type in the system.
     * Defines parameters like maximum days allowed and carry-forward policies.
     */
    public static void configureLeaveTypes() {
        System.out.println("\n--- CONFIGURE LEAVE TYPE ---");
        String name = InputUtil.readString("Leave Type Name (e.g. SICK_LEAVE): ");
        int max = InputUtil.readInt("Max leaves per year: ");
        String carryInput = InputUtil.readString("Carry forward allowed? (Y/N): ");
        boolean carry = carryInput.equalsIgnoreCase("Y");

        try {
            policyDAO.createLeaveType(name, max, carry);
            AuditService.log(getAdminId(), "CREATE", "LEAVE_TYPES", name, "Leave type created: " + name);
            System.out.println("Leave type '" + name + "' configured successfully.");
        } catch (Exception e) {
            logger.error("Failed to configure leave type: " + e.getMessage(), e);
        }
    }

    /**
     * Assigns leave quotas to specific employees for a given year.
     * Overwrites existing quotas if re-run for the same type/year.
     */
    public static void assignLeaveQuotas() {
        System.out.println("\n--- ASSIGN LEAVE QUOTA ---");
        String empId = InputUtil.readString("Employee ID: ");
        int leaveType = InputUtil.readInt("Leave Type ID: ");
        int year = InputUtil.readInt("Year (e.g. 2024): ");
        int quota = InputUtil.readInt("Quota Amount: ");

        try {
            leaveDAO.assignLeaveQuota(empId, leaveType, year, quota);
            AuditService.log(getAdminId(), "ASSIGN", "LEAVE_BALANCES", empId,
                    "Quota assigned: Type=" + leaveType + ", Val=" + quota);
            System.out.println("Leave quota assigned successfully.");
        } catch (Exception e) {
            logger.error("Failed to assign quota: " + e.getMessage(), e);
        }
    }

    public static void adjustLeaveBalance() {
        System.out.println("\n--- ADJUST LEAVE BALANCE ---");
        System.out.println("Note: This will overwrite the existing balance.");
        assignLeaveQuotas(); // Re-use logic or implement specific update logic
    }

    /**
     * Revokes an already approved leave request.
     * This is an administrative override for exceptional circumstances.
     */
    public static void revokeLeave() {
        System.out.println("\n--- REVOKE APPROVED LEAVE ---");
        int leaveId = InputUtil.readInt("Enter Leave Application ID: ");
        String reason = InputUtil.readString("Revocation Reason: ");

        try {
            leaveDAO.updateLeaveStatus(leaveId, getAdminId(), "REVOKED", reason);
            AuditService.log(getAdminId(), "REVOKE", "LEAVE_APPLICATIONS", String.valueOf(leaveId),
                    "Revoked by Admin. Reason: " + reason);
            System.out.println("Leave application " + leaveId + " has been REVOKED.");
        } catch (Exception e) {
            logger.error("Failed to revoke leave: " + e.getMessage(), e);
        }
    }

    /**
     * Displays statistical data on leave usage across the organization.
     */
    public static void viewLeaveStatistics() {
        try {
            System.out.println("\n--- LEAVE STATISTICS ---");
            java.sql.ResultSet rs = leaveDAO.getLeaveStatistics();
            System.out.printf("%-15s | %-10s%n", "STATUS", "COUNT");
            System.out.println("-----------------------------");
            while (rs.next()) {
                System.out.printf("%-15s | %-10d%n",
                        rs.getString("status"),
                        rs.getInt("count"));
            }
        } catch (Exception e) {
            logger.error("Failed to load statistics: " + e.getMessage(), e);
        }
    }

    public static void leaveReportsMenu() {
        while (true) {
            System.out.println("\n--- LEAVE REPORTS ---");
            System.out.println("1. System-wide Statistics");
            System.out.println("2. Department-wise Report");
            System.out.println("3. Employee-wise Report");
            System.out.println("4. Back");

            int choice = InputUtil.readInt("Select Option: ");

            try {
                switch (choice) {
                    case 1 -> viewLeaveStatistics();
                    case 2 -> {
                        System.out.println("\n--- DEPARTMENT LEAVE REPORT ---");
                        java.sql.ResultSet rs = leaveDAO.getDepartmentLeaveReport();
                        System.out.printf("%-20s | %-20s | %-15s | %-10s%n", "Department", "Employee", "Leave Type",
                                "Days");
                        System.out
                                .println("--------------------------------------------------------------------------");
                        while (rs.next()) {
                            System.out.printf("%-20s | %-20s | %-15s | %-10d%n",
                                    rs.getString("department_name"),
                                    rs.getString("emp_name"),
                                    rs.getString("leave_type_name"),
                                    rs.getInt("days_taken") // Check if this is int or double in DB, int usually ok for
                                                            // count
                            );
                        }
                    }
                    case 3 -> {
                        String empId = InputUtil.readString("Enter Employee ID: ");
                        System.out.println("\n--- EMPLOYEE LEAVE HISTORY ---");
                        java.sql.ResultSet rs = leaveDAO.getEmployeeLeaveReport(empId);
                        System.out.printf("%-15s | %-12s | %-12s | %-10s%n", "Type", "Start", "End", "Status");
                        System.out.println("-----------------------------------------------------------");
                        while (rs.next()) {
                            System.out.printf("%-15s | %-12s | %-12s | %-10s%n",
                                    rs.getString("leave_type_name"),
                                    rs.getDate("start_date"),
                                    rs.getDate("end_date"),
                                    rs.getString("status"));
                        }
                    }
                    case 4 -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                logger.error("Report generation failed: " + e.getMessage(), e);
            }
        }
    }
}
