package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.service.AdminService;
import com.revworkforce.util.InputUtil;

/**
 * Menu UI for Administrator Role.
 * Provides system-wide configuration and employee management.
 */
public class AdminMenu {

    public static void start() {

        while (true) {
            com.revworkforce.model.Employee admin = SessionContext.get();
            if (admin == null)
                return;

            System.out.println("\n=================================");
            System.out.println("         ADMIN DASHBOARD         ");
            System.out.println("=================================");

            int unread = com.revworkforce.service.NotificationService.getUnreadCount(admin.getEmployeeId());
            if (unread > 0)
                System.out.println("Alerts: " + unread + " unread");

            System.out.println("1.  Add New Employee");
            System.out.println("2.  Update Employee Details");
            System.out.println("3.  View All Employees");
            System.out.println("4.  Search Employees");
            System.out.println("5.  Assign/Change Manager");
            System.out.println("6.  Activate/Deactivate User");
            System.out.println("7.  Unlock User Account");
            System.out.println("8.  Reset Employee Password");
            System.out.println("---------------------------------");
            System.out.println("9.  Configure Leave Types");
            System.out.println("10. Assign Leave Quotas");
            System.out.println("11. Adjust Leave Balance (Manual)");
            System.out.println("12. Revoke Approved Leave");
            System.out.println("13. Configure Holidays");
            System.out.println("14. Leave Reports");
            System.out.println("---------------------------------");
            System.out.println("15. Manage Departments");
            System.out.println("16. Manage Designations");
            System.out.println("17. Performance Cycle Config");
            System.out.println("18. System Policies");
            System.out.println("19. View System Audit Logs");
            System.out.println("---------------------------------");
            System.out.println("20. View My Notifications");
            System.out.println("21. Run Daily Notification Job");
            System.out.println("22. Change Admin Password");
            System.out.println("23. Logout");
            System.out.println("=================================");

            int choice = InputUtil.readInt("Select Option: ");

            switch (choice) {
                // Employee Management
                case 1 -> AdminService.addEmployee();
                case 2 -> AdminService.updateEmployee();
                case 3 -> AdminService.viewAllEmployees();
                case 4 -> AdminService.searchEmployees();
                case 5 -> AdminService.assignManager();
                case 6 -> AdminService.toggleEmployeeStatus();
                case 7 -> AdminService.unlockEmployeeAccount();
                case 8 -> AdminService.resetUserPassword();

                // Leave Configuration
                case 9 -> AdminService.configureLeaveTypes();
                case 10 -> AdminService.assignLeaveQuotas();
                case 11 -> AdminService.adjustLeaveBalance();
                case 12 -> AdminService.revokeLeave();
                case 13 -> AdminService.configureHolidays();
                case 14 -> AdminService.leaveReports();

                // System Configuration
                case 15 -> AdminService.manageDepartments();
                case 16 -> AdminService.manageDesignations();
                case 17 -> AdminService.configurePerformanceCycles();
                case 18 -> AdminService.manageSystemPolicies();
                case 19 -> AdminService.viewAuditLogs();

                // Admin Actions
                case 20 -> com.revworkforce.service.NotificationService.viewNotifications(admin.getEmployeeId());
                case 21 -> com.revworkforce.service.NotificationService.generateDailyNotifications();
                case 22 -> com.revworkforce.service.EmployeeService.changePassword(admin.getEmployeeId());

                case 23 -> {
                    System.out.println("Logging out...");
                    SessionContext.clear();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
