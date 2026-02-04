package com.revworkforce.service;

import com.revworkforce.dao.AttendanceDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.PerformanceDAO;
import com.revworkforce.util.InputUtil;

import java.sql.ResultSet;

/**
 * Service class for Manager-specific operations.
 * Operations include Team Management, Leave Approvals, and Performance Reviews.
 */
public class ManagerService {

    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final LeaveDAO leaveDAO = new LeaveDAO();
    private static final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private static final PerformanceDAO performanceDAO = new PerformanceDAO();

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
            ResultSet rs = employeeDAO.getReportees(managerId);
            System.out.println("\n--- MY TEAM ---");

            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") + " | " +
                                rs.getString("first_name") + " "
                                + (rs.getString("last_name") != null ? rs.getString("last_name") : "") + " | " +
                                rs.getString("designation_name") + " | " +
                                rs.getString("department_name") + " | " +
                                rs.getString("email"));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch team: " + e.getMessage());
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
            ResultSet rs = employeeDAO.getReportees(managerId);
            System.out.println("\n--- TEAM MEMBERS ---");
            System.out.printf("%-10s | %-20s%n", "ID", "Name");
            System.out.println("---------------------------------");

            while (rs.next()) {
                String name = rs.getString("first_name") + " " +
                        (rs.getString("last_name") != null ? rs.getString("last_name") : "");
                System.out.printf("%-10s | %-20s%n", rs.getString("employee_id"), name);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch team: " + e.getMessage());
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
            e.printStackTrace();
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
            ResultSet rs = leaveDAO.getTeamLeaveRequests(managerId);
            System.out.println("\n--- TEAM LEAVE REQUESTS ---");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String name = rs.getString("first_name") + " "
                        + (rs.getString("last_name") != null ? rs.getString("last_name") : "");
                String dept = rs.getString("department_name") != null ? rs.getString("department_name") : "N/A";

                System.out.println(
                        "ID: " + rs.getInt("leave_application_id") + " | " +
                                "Emp: " + rs.getString("employee_id") + " (" + name + ", " + dept + ") | " +
                                rs.getDate("start_date") + " -> " +
                                rs.getDate("end_date") + " | Status: " + rs.getString("status"));
            }
            if (!found)
                System.out.println("No pending requests.");

        } catch (Exception e) {
            System.err.println("Unable to fetch leave requests: " + e.getMessage());
        }
    }

    /**
     * Check if a leave request is pending and belongs to the manager's team.
     */
    public static boolean isPendingLeave(String managerId, int leaveId) {
        try {
            ResultSet rs = leaveDAO.getTeamLeaveRequests(managerId);
            while (rs.next()) {
                if (rs.getInt("leave_application_id") == leaveId) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Process a leave request (Approve/Reject).
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

            System.out.println("Leave " + status + " successfully");

        } catch (Exception e) {
            System.err.println("Leave processing failed: " + e.getMessage());
        }
    }

    public static void viewTeamLeaveCalendar(String managerId) {
        try {
            ResultSet rs = leaveDAO.getTeamLeaveCalendar(managerId);

            System.out.println("\n--- TEAM LEAVE CALENDAR ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") +
                                " | " + rs.getString("leave_type_name") +
                                " | " + rs.getDate("start_date") +
                                " -> " + rs.getDate("end_date"));
            }
        } catch (Exception e) {
            System.err.println("Unable to fetch team leave calendar: " + e.getMessage());
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

            System.out.println("Leave revoked successfully.");

        } catch (Exception e) {
            System.err.println("Failed to revoke leave: " + e.getMessage());
        }
    }

    public static void viewEmployeeLeaveCalendar(String empId) {
        try {
            ResultSet rs = leaveDAO.getApprovedLeavesForEmployee(empId);

            System.out.println("\n--- LEAVE CALENDAR: " + empId + " ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("leave_type_name") + " | " +
                                rs.getDate("start_date") + " -> " +
                                rs.getDate("end_date"));
            }
        } catch (Exception e) {
            System.err.println("Unable to fetch employee leave calendar: " + e.getMessage());
        }
    }

    public static void viewTeamLeaveBalances(String managerId) {
        try {
            ResultSet rs = leaveDAO.getTeamLeaveBalances(managerId);

            System.out.println("\n--- TEAM LEAVE BALANCES ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") +
                                " | " + rs.getString("leave_type_name") +
                                " | Total: " + rs.getInt("total_allocated") +
                                " | Used: " + rs.getInt("used_leaves") +
                                " | Available: " + rs.getInt("available_leaves"));
            }
        } catch (Exception e) {
            System.err.println("Unable to fetch team leave balances: " + e.getMessage());
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
            System.err.println("Unable to fetch attendance: " + e.getMessage());
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
            System.err.println("Unable to fetch performance data: " + e.getMessage());
        }
    }

    public static void submitPerformanceReview(String managerId, int reviewId, String feedback, int rating) {
        try {
            String targetEmpId = performanceDAO.getEmployeeIdForReview(reviewId);

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

            System.out.println("Performance review submitted");

        } catch (Exception e) {
            System.err.println("Performance review failed: " + e.getMessage());
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
            System.err.println("Unable to fetch team goals: " + e.getMessage());
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
            System.err.println("Unable to fetch goal summary: " + e.getMessage());
        }
    }
}
