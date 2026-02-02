package com.revworkforce.menu;

import com.revworkforce.context.SessionContext;
import com.revworkforce.model.Employee;
import com.revworkforce.service.AuthService;
import com.revworkforce.util.InputUtil;
import com.revworkforce.dao.RoleDAO;

/**
 * Main application entry point menu.
 * Handles initial user authentication.
 */
public class MainMenu {

    public static void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   WELCOME TO REV WORKFORCE     ");
            System.out.println("=================================");
            System.out.println("1.  Login");
            System.out.println("2.  Exit System");
            System.out.println("=================================");

            int choice = InputUtil.readInt("Select Option: ");

            switch (choice) {
                case 1 -> login();
                case 2 -> {
                    System.out.println("Exiting system. Goodbye!");
                    InputUtil.close(); 
                    System.exit(0);
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void login() {
        System.out.println("\n--- LOGIN ---");
        String empId = InputUtil.readString("Employee ID: ");
        String password = InputUtil.readString("Password: ");

        if (empId.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Employee ID and Password cannot be empty.");
            return;
        }

        if (AuthService.login(empId, password)) {
            // Navigation Logic based on Role
            Employee emp = SessionContext.get();
            if (emp != null) {
                // Fetch Role - For simplicity, we query role or infer here.
                // Assuming we can get role from DB or it was populated.
                // Refetching role for navigation
                try {
                   String role = new RoleDAO().getEmployeeRole(emp.getEmployeeId());
                   System.out.println("Welcome, " + emp.getFirstName() + " (" + role + ")");
                   
                   switch (role.toUpperCase()) {
                       case "ADMIN" -> AdminMenu.start();
                       case "MANAGER" -> ManagerMenu.start();
                       case "EMPLOYEE" -> EmployeeMenu.start();
                       default -> System.out.println("Error: No valid menu for role: " + role);
                   }
                } catch (Exception e) {
                   System.out.println("Error fetching user role: " + e.getMessage());
                }
            }
        }
    }
}
