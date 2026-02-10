package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.model.Employee;
import com.revworkforce.service.ManagerService;
import com.revworkforce.service.ReportService;
import com.revworkforce.util.InputUtil;

/**
 * Menu UI for Manager Role.
 * Extends capabilities with Team Management, Leave Approval, and Performance
 * Reviews.
 * 
 * @author Gururaj Shetty
 */
public class ManagerMenu {

    public static void start() {

        while (true) {
            Employee mgr = SessionContext.get();
            if (mgr == null)
                return;

            System.out.println("\n=================================");
            System.out.println("        MANAGER DASHBOARD        ");
            System.out.println("=================================");
            System.out.println("User: " + mgr.getFirstName() + " " + mgr.getLastName());

            int unread = com.revworkforce.service.NotificationService.getUnreadCount(mgr.getEmployeeId());
            if (unread > 0)
                System.out.println("Alerts: " + unread + " unread"); // Display alerts

            System.out.println("---------------------------------");
            System.out.println("1.  My Team (Direct Reports)");
            System.out.println("2.  View Team Profiles");
            System.out.println("---------------------------------");
            System.out.println("3.  Manage Leave Requests (Approve/Reject)");
            System.out.println("4.  Revoke Approved Leave");
            System.out.println("5.  View Team Leave Balances");
            System.out.println("6.  Team Leave Calendar");
            System.out.println("---------------------------------");
            System.out.println("---------------------------------");
            System.out.println("7.  Team Attendance Summary");
            System.out.println("8.  Team Goals & Progress");
            System.out.println("9.  Goal Completion Stats");
            System.out.println("10. Review Team Performance");
            System.out.println("11. Performance Summary Report");
            System.out.println("---------------------------------");
            System.out.println("12. View Notifications");
            System.out.println("13. Change Password");
            System.out.println("14. Logout");
            System.out.println("=================================");

            int choice = InputUtil.readInt("Select Option: ");

            switch (choice) {
                case 1 -> ManagerService.viewTeam(mgr.getEmployeeId());
                case 2 -> {
                    ManagerService.viewTeamBasic(mgr.getEmployeeId());
                    while (true) {
                        String empId = InputUtil.readString("Enter Employee ID: ");
                        if (ManagerService.isTeamMember(mgr.getEmployeeId(), empId)) {
                            ManagerService.viewTeamMemberProfile(empId);
                            break;
                        } else {
                            System.out.println("Invalid Employee ID. Please select from the list above.");
                        }
                    }
                }

                case 3 -> {
                    while (true) {
                        ManagerService.viewTeamLeaveRequests(mgr.getEmployeeId());
                        System.out.println("Enter Leave ID to Process (or 0 to Go Back)");

                        int leaveId = 0;
                        while (true) {
                            leaveId = InputUtil.readInt("Leave ID: ");
                            if (leaveId == 0)
                                break;
                            if (ManagerService.isPendingLeave(mgr.getEmployeeId(), leaveId)) {
                                break;
                            } else {
                                System.out.println("Invalid Leave ID. Please select a valid ID from the list.");
                            }
                        }

                        if (leaveId == 0)
                            break;

                        String decision = "";
                        while (true) {
                            decision = InputUtil.readString("Decision (A=Approve / R=Reject): ");
                            if (decision.equalsIgnoreCase("A") || decision.equalsIgnoreCase("R")) {
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter 'A' for Approve or 'R' for Reject.");
                            }
                        }

                        String comments = InputUtil.readString("Reason/Comments: ");
                        String status = decision.equalsIgnoreCase("A") ? "APPROVED" : "REJECTED";
                        ManagerService.processLeave(mgr.getEmployeeId(), leaveId, status, comments);
                    }
                }

                case 4 -> {
                    ManagerService.viewTeamLeaveCalendar(mgr.getEmployeeId()); // Show calendar to help identify
                    System.out.println("Enter Leave ID to Revoke (or 0 to Cancel):");
                    int leaveId = InputUtil.readInt("Leave ID: ");
                    if (leaveId != 0) {
                        String reason = InputUtil.readString("Reason for Revocation: ");
                        ManagerService.revokeApprovedLeave(mgr.getEmployeeId(), leaveId, reason);
                    }
                }

                case 5 -> ManagerService.viewTeamLeaveBalances(mgr.getEmployeeId());
                case 6 -> {
                    ManagerService.viewTeamLeaveCalendar(mgr.getEmployeeId());
                    System.out.println("Enter Employee ID to filter (or 0 to Back)");
                    while (true) {
                        String filterId = InputUtil.readString("Employee ID: ");
                        if (filterId.equals("0"))
                            break;

                        if (ManagerService.isTeamMember(mgr.getEmployeeId(), filterId)) {
                            ManagerService.viewEmployeeLeaveCalendar(filterId);
                        } else {
                            System.out.println("Invalid Employee ID. Please select a valid team member.");
                        }
                    }
                }

                case 7 -> ManagerService.viewTeamAttendance(mgr.getEmployeeId());
                case 8 -> ManagerService.viewTeamGoals(mgr.getEmployeeId());
                case 9 -> ManagerService.viewGoalCompletionSummary(mgr.getEmployeeId());

                case 10 -> {
                    ManagerService.viewTeamPerformance(mgr.getEmployeeId());
                    System.out.println("\n(Enter 0 to Go Back)");
                    int revId = InputUtil.readInt("Enter Review ID to Submit Feedback: ");
                    if (revId != 0) {
                        String feedback = InputUtil.readString("Feedback: ");
                        int rating = InputUtil.readInt("Rating (1-5): ");
                        ManagerService.submitPerformanceReview(mgr.getEmployeeId(), revId, feedback, rating);
                    }
                }
                case 11 -> ReportService.teamPerformanceSummary(mgr.getEmployeeId());

                case 12 -> com.revworkforce.service.NotificationService.viewNotifications(mgr.getEmployeeId());
                case 13 -> com.revworkforce.service.EmployeeService.changePassword(mgr.getEmployeeId());

                case 14 -> {
                    System.out.println("Logging out...");
                    SessionContext.clear();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
