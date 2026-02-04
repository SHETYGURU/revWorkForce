package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.model.Employee;
import com.revworkforce.service.EmployeeService;
import com.revworkforce.service.LeaveService;
import com.revworkforce.service.NotificationService;
import com.revworkforce.service.PerformanceService;
import com.revworkforce.util.InputUtil;

/**
 * Menu UI for Employee Role.
 */
public class EmployeeMenu {

    public static void start() {

        while (true) {
            Employee emp = SessionContext.get();
            if (emp == null)
                return; // Session expired

            // Fetch unread count safely
            int unread = NotificationService.getUnreadCount(emp.getEmployeeId());

            System.out.println("\n=================================");
            System.out.println("       EMPLOYEE DASHBOARD        ");
            System.out.println("=================================");
            System.out.println("User:  " + emp.getFirstName() + " " + emp.getLastName());
            System.out.println("ID:    " + emp.getEmployeeId());
            System.out.println("Alerts: " + unread + " unread");
            System.out.println("---------------------------------");
            System.out.println("1.  View My Profile");
            System.out.println("2.  Update My Profile");
            System.out.println("3.  View Manager Info");
            System.out.println("---------------------------------");
            System.out.println("4.  Leave Balance");
            System.out.println("5.  Apply for Leave");
            System.out.println("6.  My Leave History");
            System.out.println("7.  Cancel Leave Request");
            System.out.println("8.  Holiday Calendar");
            System.out.println("---------------------------------");
            System.out.println("9.  Submit Self Review");
            System.out.println("10. Manage My Goals");
            System.out.println("11. View Feedback");
            System.out.println("---------------------------------");
            System.out.println("12. Upcoming Data (Birthdays/Anniversaries)");
            System.out.println("13. Company Announcements");
            System.out.println("14. Employee Directory");
            System.out.println("15. Notifications");
            System.out.println("16. Change Password");
            System.out.println("17. Logout");
            System.out.println("=================================");

            int choice = InputUtil.readInt("Select Option: ");

            switch (choice) {
                case 1 -> EmployeeService.viewProfile(emp.getEmployeeId());
                case 2 -> EmployeeService.updateProfile(emp.getEmployeeId());
                case 3 -> EmployeeService.viewManagerDetails(emp.getEmployeeId());

                case 4 -> LeaveService.viewLeaveBalance(emp.getEmployeeId());
                case 5 -> LeaveService.applyLeave(emp.getEmployeeId());
                case 6 -> LeaveService.viewMyLeaves(emp.getEmployeeId());
                case 7 -> LeaveService.cancelLeave(emp.getEmployeeId());
                case 8 -> LeaveService.viewHolidays();

                case 9 -> PerformanceService.submitSelfReview(emp.getEmployeeId());
                case 10 -> PerformanceService.manageGoals(emp.getEmployeeId());
                case 11 -> PerformanceService.viewManagerFeedback(emp.getEmployeeId());

                case 12 -> {
                    EmployeeService.viewUpcomingBirthdays();
                    EmployeeService.viewWorkAnniversaries();
                }
                case 13 -> EmployeeService.viewAnnouncements();
                case 14 -> EmployeeService.employeeDirectory();
                case 15 -> NotificationService.viewNotifications(emp.getEmployeeId());
                case 16 -> EmployeeService.changePassword(emp.getEmployeeId());

                case 17 -> {
                    System.out.println("Logging out...");
                    SessionContext.clear();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
