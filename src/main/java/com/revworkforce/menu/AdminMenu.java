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
            System.out.println("\n=================================");
            System.out.println("         ADMIN DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("1.  Add New Employee");
            System.out.println("2.  Update Employee Details");
            System.out.println("3.  View All Employees");
            System.out.println("4.  Search Employees");
            System.out.println("5.  Assign/Change Manager");
            System.out.println("6.  Activate/Deactivate User");
            System.out.println("7.  Unlock User Account");
            System.out.println("---------------------------------");
            System.out.println("8.  Configure Leave Types");
            System.out.println("9.  Assign Leave Quotas");
            System.out.println("10. Adjust Leave Balance (Manual)");
            System.out.println("11. Revoke Approved Leave");
            System.out.println("12. Configure Holidays");
            System.out.println("---------------------------------");
            System.out.println("13. Manage Departments");
            System.out.println("14. Manage Designations");
            System.out.println("15. Performance Cycle Config");
            System.out.println("16. System Policies");
            System.out.println("17. View System Audit Logs");
            System.out.println("---------------------------------");
            System.out.println("18. Logout");
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

                // Leave Configuration
                case 8 -> AdminService.configureLeaveTypes();
                case 9 -> AdminService.assignLeaveQuotas();
                case 10 -> AdminService.adjustLeaveBalance();
                case 11 -> AdminService.revokeLeave();
                case 12 -> AdminService.configureHolidays();

                // System Configuration
                case 13 -> AdminService.manageDepartments();
                case 14 -> AdminService.manageDesignations();
                case 15 -> AdminService.configurePerformanceCycles();
                case 16 -> AdminService.manageSystemPolicies();
                case 17 -> AdminService.viewAuditLogs();

                case 18 -> {
                    System.out.println("Logging out...");
                    SessionContext.clear();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
