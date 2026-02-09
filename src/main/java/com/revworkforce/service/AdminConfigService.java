package com.revworkforce.service;

import com.revworkforce.dao.*;
import com.revworkforce.util.InputUtil;

import java.sql.Date;

public class AdminConfigService {

    private static DepartmentDAO departmentDAO = new DepartmentDAO();
    private static DesignationDAO designationDAO = new DesignationDAO();
    private static PerformanceCycleDAO cycleDAO = new PerformanceCycleDAO();
    private static SystemPolicyDAO policyDAO = new SystemPolicyDAO();
    private static HolidayDAO holidayDAO = new HolidayDAO();

    private static String getAdminId() {
        return com.revworkforce.context.SessionContext.get() != null
                ? com.revworkforce.context.SessionContext.get().getEmployeeId()
                : "ADMIN001"; // Fallback
    }

    public static void manageDepartments() {
        while (true) {
            System.out.println("\n--- MANAGE DEPARTMENTS ---");
            System.out.println("1. Add Department");
            System.out.println("2. View All Departments");
            System.out.println("3. Update Department");
            System.out.println("4. Delete Department");
            System.out.println("5. Back");

            int choice = InputUtil.readInt("Select Option: ");

            try {
                switch (choice) {
                    case 1 -> {
                        String name = InputUtil.readString("New Department Name: ");
                        departmentDAO.addDepartment(name);
                        AuditService.log(getAdminId(), "CREATE", "DEPARTMENTS", "Name", name, "Department added");
                        System.out.println("Department added successfully.");
                    }
                    case 2 -> {
                        System.out.println("\n--- ALL DEPARTMENTS ---");
                        try (java.sql.ResultSet rs = departmentDAO.getAllDepartments();
                                java.sql.Statement stmt = rs.getStatement();
                                java.sql.Connection con = stmt.getConnection()) {

                            System.out.printf("%-15s %-30s%n", "ID", "NAME");
                            System.out.println("---------------------------------------------");
                            while (rs.next()) {
                                System.out.printf("%-15s %-30s%n",
                                        rs.getString("department_id"),
                                        rs.getString("department_name"));
                            }
                        }
                    }
                    case 3 -> {
                        String id = InputUtil.readString("Department ID to Update: ");
                        String name = InputUtil.readString("New Department Name: ");
                        departmentDAO.updateDepartment(id, name);
                        AuditService.log(getAdminId(), "UPDATE", "DEPARTMENTS", "Name", id,
                                "Department name changed to " + name);
                        System.out.println("Department updated successfully.");
                    }
                    case 4 -> {
                        String id = InputUtil.readString("Department ID to Delete: ");
                        departmentDAO.deleteDepartment(id);
                        AuditService.log(getAdminId(), "DELETE", "DEPARTMENTS", "All", id, "Department deleted");
                        System.out.println("Department deleted successfully.");
                    }
                    case 5 -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Operation failed: " + e.getMessage());
            }
        }
    }

    public static void manageDesignations() {
        while (true) {
            System.out.println("\n--- MANAGE DESIGNATIONS ---");
            System.out.println("1. Add Designation");
            System.out.println("2. View All Designations");
            System.out.println("3. Update Designation");
            System.out.println("4. Delete Designation");
            System.out.println("5. Back");

            int choice = InputUtil.readInt("Select Option: ");

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.println("\n--- ADD DESIGNATION ---");
                        String name = InputUtil.readString("New Designation Name: ");
                        designationDAO.addDesignation(name);
                        AuditService.log(getAdminId(), "CREATE", "DESIGNATIONS", "Name", name, "Designation added");
                        System.out.println("Designation added successfully.");
                    }
                    case 2 -> {
                        System.out.println("\n--- ALL DESIGNATIONS ---");
                        try (java.sql.ResultSet rs = designationDAO.getAllDesignations();
                                java.sql.Statement stmt = rs.getStatement();
                                java.sql.Connection con = stmt.getConnection()) {

                            System.out.printf("%-15s %-30s%n", "ID", "NAME");
                            System.out.println("---------------------------------------------");
                            while (rs.next()) {
                                System.out.printf("%-15s %-30s%n",
                                        rs.getString("designation_id"),
                                        rs.getString("designation_name"));
                            }
                        }
                    }
                    case 3 -> {
                        System.out.println("\n--- UPDATE DESIGNATION ---");
                        String id = InputUtil.readString("Designation ID to Update: ");
                        String name = InputUtil.readString("New Designation Name: ");
                        designationDAO.updateDesignation(id, name);
                        AuditService.log(getAdminId(), "UPDATE", "DESIGNATIONS", "Name", id,
                                "Designation name changed to " + name);
                        System.out.println("Designation updated successfully.");
                    }
                    case 4 -> {
                        System.out.println("\n--- DELETE DESIGNATION ---");
                        String id = InputUtil.readString("Designation ID to Delete: ");
                        designationDAO.deleteDesignation(id);
                        AuditService.log(getAdminId(), "DELETE", "DESIGNATIONS", "All", id, "Designation deleted");
                        System.out.println("Designation deleted successfully.");
                    }
                    case 5 -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Operation failed: " + e.getMessage());
            }
        }
    }

    public static void configurePerformanceCycles() {
        try {
            System.out.println("\n--- CONFIGURE PERFORMANCE CYCLE ---");
            String name = InputUtil.readString("Cycle Name (e.g. 2024 Appraisal): ");
            Date start = Date.valueOf(InputUtil.readString("Start Date (YYYY-MM-DD): "));
            Date end = Date.valueOf(InputUtil.readString("End Date (YYYY-MM-DD): "));

            cycleDAO.createCycle(name, start, end);
            AuditService.log(getAdminId(), "CREATE", "PERFORMANCE_CYCLES", "Name/Dates", name,
                    "Performance cycle created");
            System.out.println("Performance cycle configured successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format.");
        } catch (Exception e) {
            System.err.println("Failed to create cycle: " + e.getMessage());
        }
    }

    public static void manageSystemPolicies() {
        while (true) {
            System.out.println("\n--- MANAGE SYSTEM POLICIES ---");
            System.out.println("1. Add/Update Policy");
            System.out.println("2. View All Policies");
            System.out.println("3. Delete Policy");
            System.out.println("4. Back");

            int choice = InputUtil.readInt("Select Option: ");

            try {
                switch (choice) {
                    case 1 -> {
                        String key = InputUtil.readString("Policy Name (Key): ");
                        String value = InputUtil.readString("Policy Value: ");
                        policyDAO.updatePolicy(key, value);
                        AuditService.log(getAdminId(), "UPDATE", "SYSTEM_POLICIES", "Value", key,
                                "Policy updated/created");
                        System.out.println("Policy saved successfully.");
                    }
                    case 2 -> {
                        policyDAO.printPolicies();
                    }
                    case 3 -> {
                        policyDAO.printPolicies();
                        String key = InputUtil.readString("Policy Name to Delete: ");
                        policyDAO.deletePolicy(key);
                        AuditService.log(getAdminId(), "DELETE", "SYSTEM_POLICIES", "All", key, "Policy deleted");
                        System.out.println("Policy deleted successfully.");
                    }
                    case 4 -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Operation failed: " + e.getMessage());
            }
        }
    }

    public static void configureHolidays() {
        try {
            System.out.println("\n--- ADD HOLIDAY ---");
            String name = InputUtil.readString("Holiday Name: ");

            Date date = null;
            int year = 0;

            while (date == null) {
                try {
                    String dateStr = InputUtil.readString("Date (YYYY-MM-DD): ");
                    Date tempDate = Date.valueOf(dateStr);
                    java.time.LocalDate localDate = tempDate.toLocalDate();

                    if (localDate.isBefore(java.time.LocalDate.now())) {
                        System.out.println("Error: Cannot configure holiday in the past. Please try again.");
                    } else {
                        date = tempDate;
                        year = localDate.getYear();
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                }
            }

            holidayDAO.addHoliday(name, date, year);
            AuditService.log(getAdminId(), "CREATE", "HOLIDAYS", "Date", name, "Holiday added: " + date);
            System.out.println("Holiday configured successfully.");
        } catch (Exception e) {
            System.err.println("Failed to add holiday: " + e.getMessage());
        }
    }
}
