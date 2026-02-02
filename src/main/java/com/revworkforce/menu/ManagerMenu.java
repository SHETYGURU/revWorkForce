package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.model.Employee;
import com.revworkforce.service.ManagerService;
import com.revworkforce.service.ReportService;
import com.revworkforce.util.InputUtil;

/**
 * Menu UI for Manager Role.
 * Extends Employee capabilities with Team Management features.
 */
public class ManagerMenu {

    public static void start() {

        Employee mgr = SessionContext.get();

        while (true) {
            System.out.println("\n=================================");
            System.out.println("        MANAGER DASHBOARD        ");
            System.out.println("=================================");
            System.out.println("User: " + mgr.getFirstName() + " " + mgr.getLastName());
            System.out.println("---------------------------------");
            System.out.println("1.  My Team (Direct Reports)");
            System.out.println("2.  View Team Profiles");
            System.out.println("---------------------------------");
            System.out.println("3.  View Team Leave Requests");
            System.out.println("4.  Approve / Reject Leave");
            System.out.println("5.  View Team Leave Balances");
            System.out.println("6.  Team Leave Calendar");
            System.out.println("---------------------------------");
            System.out.println("7.  Team Attendance Summary");
            System.out.println("8.  Team Goals & Progress");
            System.out.println("9.  Goal Completion Stats");
            System.out.println("10. Review Team Performance");
            System.out.println("11. Performance Summary Report");
            System.out.println("---------------------------------");
            System.out.println("12. Logout");
            System.out.println("=================================");

            int choice = InputUtil.readInt("Select Option: ");

            switch (choice) {
                case 1 -> ManagerService.viewTeam(mgr.getEmployeeId());
                case 2 -> {
                    String empId = InputUtil.readString("Enter Employee ID: ");
                    ManagerService.viewTeamMemberProfile(empId);
                }

                case 3 -> ManagerService.viewTeamLeaveRequests(mgr.getEmployeeId());
                case 4 -> {
                    int leaveId = InputUtil.readInt("Enter Leave ID to Process: ");
                    String decision = InputUtil.readString("Decision (A=Approve / R=Reject): ");
                    String comments = InputUtil.readString("Reason/Comments: ");

                    String status = decision.equalsIgnoreCase("A") ? "APPROVED" : "REJECTED";
                    ManagerService.processLeave(mgr.getEmployeeId(), leaveId, status, comments);
                }
                case 5 -> ManagerService.viewTeamLeaveBalances(mgr.getEmployeeId());
                case 6 -> ManagerService.viewTeamLeaveCalendar(mgr.getEmployeeId());

                case 7 -> ManagerService.viewTeamAttendance(mgr.getEmployeeId());
                case 8 -> ManagerService.viewTeamGoals(mgr.getEmployeeId());
                case 9 -> ManagerService.viewGoalCompletionSummary(mgr.getEmployeeId());
                
                case 10 -> ManagerService.viewTeamPerformance(mgr.getEmployeeId());
                case 11 -> ReportService.teamPerformanceSummary(mgr.getEmployeeId());

                case 12 -> {
                    System.out.println("Logging out...");
                    SessionContext.clear();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
