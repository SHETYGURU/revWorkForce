/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.AttendanceDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.PerformanceDAO;
import com.revworkforce.util.MessageConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

/**
 * Service class for Manager-specific operations.
 * Operations include Team Management, Leave Approvals, Performance Reviews, and
 * Goal Setting.
 * 
 * @author Gururaj Shetty
 */
public class ManagerService {

    private static final Logger logger = LogManager.getLogger(ManagerService.class);

    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static LeaveDAO leaveDAO = new LeaveDAO();
    private static AttendanceDAO attendanceDAO = new AttendanceDAO();
    private static PerformanceDAO performanceDAO = new PerformanceDAO();

    /*
     * =============================================================================
     * ======
     * TEAM MANAGEMENT
     * =============================================================================
     * ======
     */

    /**
     * View all direct reportees.
     * 
     * @param managerId Manager's Employee ID.
     */
    public static void viewTeam(String managerId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = employeeDAO.getReportees(managerId);
            System.out.println("\n--- MY TEAM ---");

            for (java.util.Map<String, Object> row : list) {
                System.out.println(
                        row.get("employee_id") + " | " +
                                row.get("first_name") + " "
                                + (row.get("last_name") != null ? row.get("last_name") : "") + " | " +
                                row.get("designation_name") + " | " +
                                row.get("department_name") + " | " +
                                row.get("email"));
            }
        } catch (Exception e) {
            logger.error("Failed to fetch team: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch team. " + e.getMessage());
        }
    }

    /**
     * View details of a specific team member.
     * 
     * @param employeeId Member's Employee ID.
     */
    public static void viewTeamMemberProfile(String employeeId) {
        // Re-use logic from EmployeeService
        EmployeeService.viewProfile(employeeId);
    }

    /**
     * View basic details (ID and Name) of direct reportees.
     * 
     * @param managerId Manager's Employee ID.
     */
    public static void viewTeamBasic(String managerId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = employeeDAO.getReportees(managerId);
            System.out.println("\n--- TEAM MEMBERS ---");
            System.out.printf("%-10s | %-20s%n", "ID", "Name");
            System.out.println("---------------------------------");

            for (java.util.Map<String, Object> row : list) {
                String name = row.get("first_name") + " " +
                        (row.get("last_name") != null ? row.get("last_name") : "");
                System.out.printf("%-10s | %-20s%n", row.get("employee_id"), name);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch team: " + e.getMessage(), e);
            System.out.println("Error: Failed to fetch team. " + e.getMessage());
        }
    }

    /**
     * Check if an employee is a direct report of the manager.
     * 
     * @param managerId Manager's Employee ID.
     * @param empId     Employee ID to check.
     * @return true if associated, false otherwise.
     */
    public static boolean isTeamMember(String managerId, String empId) {
        try {
            return employeeDAO.isReportee(managerId, empId);
        } catch (Exception e) {
            logger.error("Error in viewTeamDirectory", e);
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /*
     * =============================================================================
     * ======
     * LEAVE MANAGEMENT
     * =============================================================================
     * ======
     */

    /**
     * View pending leave requests from team members.
     * 
     * @param managerId Manager's Employee ID.
     */
    public static void viewTeamLeaveRequests(String managerId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = leaveDAO.getTeamLeaveRequests(managerId);
            System.out.println("\n--- TEAM LEAVE REQUESTS ---");

            if (list.isEmpty()) {
                System.out.println("No pending requests.");
            } else {
                for (java.util.Map<String, Object> row : list) {
                    String name = row.get("first_name") + " "
                            + (row.get("last_name") != null ? row.get("last_name") : "");
                    String dept = row.get("department_name") != null ? (String) row.get("department_name") : "N/A";

                    System.out.println(
                            "ID: " + row.get("leave_application_id") + " | " +
                                    "Emp: " + row.get("employee_id") + " (" + name + ", " + dept + ") | " +
                                    row.get("start_date") + " -> " +
                                    row.get("end_date") + " | Status: " + row.get("status"));
                }
            }

        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "leave requests: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch leave requests. " + e.getMessage());
        }
    }

    /**
     * Checks if a specific leave request belongs to a member of the manager's team
     * and is currently in a pending state.
     * 
     * @param managerId The Manager's ID.
     * @param leaveId   The Leave Application ID.
     * @return true if the leave is pending and belongs to a reportee.
     */
    public static boolean isPendingLeave(String managerId, int leaveId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = leaveDAO.getTeamLeaveRequests(managerId);
            for (java.util.Map<String, Object> row : list) {
                if ((int) row.get("leave_application_id") == leaveId) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Error checking reportee relationship", e);
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Processes a leave request by updating its status (Approve/Reject) and logging
     * the action.
     * Sends a notification to the employee regarding the decision.
     * 
     * @param managerId The Manager processing the request.
     * @param leaveId   The Leave Request ID.
     * @param status    The new status (APPROVED/REJECTED).
     * @param comments  Optional comments providing reasoning.
     */
    public static void processLeave(String managerId, int leaveId, String status, String comments) {
        try {
            String targetEmpId = leaveDAO.getEmployeeIdForLeave(leaveId); // Get target employee

            leaveDAO.updateLeaveStatus(leaveId, managerId, status, comments);

            if (targetEmpId != null) {
                com.revworkforce.service.NotificationService.notifyLeaveUpdate(targetEmpId, status);
            }

            AuditService.log(
                    managerId,
                    status,
                    "LEAVE_APPLICATIONS",
                    String.valueOf(leaveId),
                    "Manager processed leave");

            logger.info("Leave request {} processed by manager {}. Status: {}", leaveId, managerId, status);

            System.out.println("Leave " + status + " successfully");

        } catch (Exception e) {
            logger.error("Leave processing failed: " + e.getMessage(), e);
            System.out.println("Error: Leave processing failed. " + e.getMessage());
        }
    }

    public static void viewTeamLeaveCalendar(String managerId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = leaveDAO.getTeamLeaveCalendar(managerId);

            System.out.println("\n--- TEAM LEAVE CALENDAR ---");
            for (java.util.Map<String, Object> row : list) {
                System.out.println(
                        row.get("employee_id") +
                                " | " + row.get("leave_type_name") +
                                " | " + row.get("start_date") +
                                " -> " + row.get("end_date"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "team leave calendar: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch team leave calendar. " + e.getMessage());
        }
    }

    public static void revokeApprovedLeave(String managerId, int leaveId, String reason) {
        try {
            // Verify if leave is approved and belongs to team
            // (Simplified check: assuming ID is sufficient and trusted or checked below)

            // Re-verify it is capable of being revoked (should be approved)
            // Using generic update for now, ideally strictly check status

            leaveDAO.updateLeaveStatus(leaveId, managerId, "REVOKED", reason);

            String targetEmpId = leaveDAO.getEmployeeIdForLeave(leaveId);
            if (targetEmpId != null) {
                com.revworkforce.service.NotificationService.notifyLeaveUpdate(targetEmpId, "REVOKED");
            }

            AuditService.log(
                    managerId,
                    "REVOKE",
                    "LEAVE_APPLICATIONS",
                    String.valueOf(leaveId),
                    "Manager revoked approved leave. Reason: " + reason);

            logger.info("Approved leave {} revoked by manager {}. Reason: {}", leaveId, managerId, reason);

            System.out.println("Leave revoked successfully.");

        } catch (Exception e) {
            logger.error("Failed to revoke leave: " + e.getMessage(), e);
            System.out.println("Error: Failed to revoke leave. " + e.getMessage());
        }
    }

    public static void viewEmployeeLeaveCalendar(String empId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = leaveDAO.getApprovedLeavesForEmployee(empId);

            System.out.println("\n--- LEAVE CALENDAR: " + empId + " ---");
            for (java.util.Map<String, Object> row : list) {
                System.out.println(
                        row.get("leave_type_name") + " | " +
                                row.get("start_date") + " -> " +
                                row.get("end_date"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "employee leave calendar: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch employee leave calendar. " + e.getMessage());
        }
    }

    public static void viewTeamLeaveBalances(String managerId) {
        try {
            java.util.List<java.util.Map<String, Object>> list = leaveDAO.getTeamLeaveBalances(managerId);

            System.out.println("\n--- TEAM LEAVE BALANCES ---");
            for (java.util.Map<String, Object> row : list) {
                System.out.println(
                        row.get("employee_id") +
                                " | " + row.get("leave_type_name") +
                                " | Total: " + row.get("total_allocated") +
                                " | Used: " + row.get("used_leaves") +
                                " | Available: " + row.get("available_leaves"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "team leave balances: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch team leave balances. " + e.getMessage());
        }
    }

    /*
     * =============================================================================
     * ======
     * ATTENDANCE & PERFORMANCE
     * =============================================================================
     * ======
     */

    public static void viewTeamAttendance(String managerId) {
        try {
            ResultSet rs = attendanceDAO.getTeamAttendanceSummary(managerId);
            System.out.println("\n--- TEAM ATTENDANCE SUMMARY ---");

            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") +
                                " | Present: " + rs.getInt("present_days") +
                                " | Absent: " + rs.getInt("absent_days"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "attendance: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch attendance. " + e.getMessage());
        }
    }

    public static void viewTeamPerformance(String managerId) {
        try {
            ResultSet rs = performanceDAO.getTeamReviews(managerId);
            System.out.println("\n--- TEAM PERFORMANCE REVIEWS ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("review_id") + " | " +
                                rs.getString("employee_id") + " | " +
                                rs.getString("status"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "performance data: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch performance data. " + e.getMessage());
        }
    }

    /**
     * Submits a performance review for a team member.
     * Updates the rating and feedback, and notifies the employee.
     * 
     * @param managerId The Manager submitting the review.
     * @param reviewId  The Review ID.
     * @param feedback  Detailed qualitative feedback.
     * @param rating    Quantitative rating (1-5).
     */
    public static void submitPerformanceReview(String managerId, int reviewId, String feedback, int rating) {
        try {
            String targetEmpId = performanceDAO.getEmployeeIdForReview(reviewId);

            // Validation: Ensure the review belongs to an employee managed by this manager
            if (targetEmpId == null) {
                System.out.println("Error: Review ID not found.");
                return;
            }
            if (!employeeDAO.isReportee(managerId, targetEmpId)) {
                System.out.println("Error: You can only review your own team members.");
                return;
            }

            performanceDAO.submitManagerFeedback(reviewId, feedback, rating);

            if (targetEmpId != null) {
                com.revworkforce.service.NotificationService.notifyPerformanceFeedback(targetEmpId);
            }

            AuditService.log(
                    managerId,
                    "REVIEW",
                    "PERFORMANCE_REVIEWS",
                    String.valueOf(reviewId),
                    "Manager submitted feedback");

            logger.info("Performance review {} submitted by manager {}", reviewId, managerId);

            System.out.println("Performance review submitted");

        } catch (Exception e) {
            logger.error("Performance review failed: " + e.getMessage(), e);
            System.out.println("Error: Performance review failed. " + e.getMessage());
        }
    }

    public static void viewTeamGoals(String managerId) {
        try {
            ResultSet rs = performanceDAO.getTeamGoals(managerId);

            System.out.println("\n--- TEAM GOALS ---");
            System.out.println("ID | Employee ID | Priority | Progress | Deadline");
            System.out.println("---------------------------------------------------------");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("goal_id") +
                                " | " + rs.getString("employee_id") +
                                " | " + rs.getString("priority") +
                                " | Progress: " + rs.getInt("progress_percentage") + "%" +
                                " | Deadline: " + rs.getDate("deadline"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "team goals: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch team goals. " + e.getMessage());
        }
    }

    public static void viewGoalCompletionSummary(String managerId) {
        try {
            ResultSet rs = performanceDAO.getGoalCompletionSummary(managerId);

            System.out.println("\n--- GOAL COMPLETION SUMMARY ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") +
                                " | Completed: " + rs.getInt("completed_goals") +
                                " / " + rs.getInt("total_goals"));
            }
        } catch (Exception e) {
            logger.error(MessageConstants.UNABLE_TO_FETCH_PREFIX + "goal summary: " + e.getMessage(), e);
            System.out.println("Error: Unable to fetch goal summary. " + e.getMessage());
        }
    }
}
