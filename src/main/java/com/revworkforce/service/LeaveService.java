/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.util.InputUtil;
import com.revworkforce.util.MessageConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.ResultSet;

/**
 * Service class for Leave Management operations.
 * Handles viewing balances, applying for leave, leave cancellation, and
 * calendar views.
 * 
 * @author Gururaj Shetty
 */
public class LeaveService {

    private static final Logger logger = LogManager.getLogger(LeaveService.class);

    private static LeaveDAO dao = new LeaveDAO();

    /**
     * View leave balance for the logged-in employee.
     *
     * @param empId Employee ID.
     */
    public static void viewLeaveBalance(String empId) {
        try {
            ResultSet rs = dao.getLeaveBalances(empId);
            System.out.println("\n--- LEAVE BALANCE ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        rs.getString("leave_type_name") +
                                " | Total: " + rs.getInt("total_allocated") +
                                " | Used: " + rs.getInt("used_leaves") +
                                " | Available: " + rs.getInt("available_leaves"));
            }
            if (!found) {
                System.out.println("No leave balance records found.");
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "leave balance: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch leave balance. " + e.getMessage());
        }
    }

    /**
     * Applies for a new leave request.
     * Prompts user for Leave Type, Dates, and Reason.
     * Validates date logic (End Date >= Start Date).
     *
     * @param empId Employee ID of the applicant.
     */
    public static void applyLeave(String empId) {
        try {
            // Suggest implementing a 'View Leave Types' here first for better UX
            // For now, prompt ID directly as per existing flow
            int leaveType = InputUtil.readInt("Leave Type ID: ");
            Date start = Date.valueOf(InputUtil.readString("Start Date (YYYY-MM-DD): "));
            Date end = Date.valueOf(InputUtil.readString("End Date (YYYY-MM-DD): "));
            String reason = InputUtil.readString("Reason: ");

            // Basic validation
            if (end.before(start)) {
                System.out.println("Error: End date cannot be before start date.");
                return;
            }

            dao.applyLeave(empId, leaveType, start, end, reason);
            AuditService.log(empId, "CREATE", "LEAVE_APPLICATIONS", "NEW", "Leave applied");

            logger.info("Leave applied by employee {}. Type: {}, Start: {}, End: {}", empId, leaveType, start, end);

            System.out.println("Leave applied successfully. Status: PENDING");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        } catch (Exception e) {
            logger.error("Leave application failed: " + e.getMessage(), e);
            System.out.println("Error: Leave application failed. " + e.getMessage());
        }
    }

    /**
     * View all leaves applied by the employee.
     *
     * @param empId Employee ID.
     */
    public static void viewMyLeaves(String empId) {
        try {
            ResultSet rs = dao.getMyLeaves(empId);
            System.out.println("\n--- MY LEAVES ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                        "ID: " + rs.getInt("leave_application_id") +
                                " | " + rs.getDate("start_date") +
                                " -> " + rs.getDate("end_date") +
                                " | " + rs.getString("status"));
            }
            if (!found) {
                System.out.println("No leave applications found.");
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "leaves: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch leaves. " + e.getMessage());
        }
    }

    /**
     * Cancels a pending leave request.
     * Allows employees to withdraw their application before approval.
     *
     * @param empId Employee ID.
     */
    public static void cancelLeave(String empId) {
        int leaveId = InputUtil.readInt("Enter Leave Application ID to cancel: ");
        try {
            dao.cancelLeave(leaveId, empId);
            AuditService.log(empId, "CANCEL", "LEAVE_APPLICATIONS", String.valueOf(leaveId), "Leave cancelled");
            logger.info("Leave application {} cancelled by employee {}", leaveId, empId);
            System.out.println("Leave cancelled (if it was pending).");
        } catch (Exception e) {
            logger.error("Cancel failed: " + e.getMessage(), e);
            System.out.println("Error: Cancel failed. " + e.getMessage());
        }
    }

    /**
     * View company holiday calendar.
     */
    public static void viewHolidays() {
        System.out.println("\n--- HOLIDAY CALENDAR ---");
        // Needs HolidayDAO implementation - simply print stub for now or SQL query
        // Ideally: dao.getHolidays()
        System.out.println("feature coming soon...");
    }
}
