/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.AuditLogDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.util.DBConnection;
import com.revworkforce.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.revworkforce.model.Employee;

/**
 * Service class for Administrator operations.
 * Handles extensive user management, system configuration, and high-level
 * overrides.
 * 
 * @author Gururaj Shetty
 */
public class AdminService {

    private static final Logger logger = LogManager.getLogger(AdminService.class);

    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static AuditLogDAO auditDAO = new AuditLogDAO();
    private static com.revworkforce.dao.DepartmentDAO departmentDAO = new com.revworkforce.dao.DepartmentDAO();
    private static com.revworkforce.dao.DesignationDAO designationDAO = new com.revworkforce.dao.DesignationDAO();

    /*
     * =============================================================================
     * ======
     * EMPLOYEE MANAGEMENT
     * =============================================================================
     * ======
     */

    private static String getAdminId() {
        return com.revworkforce.context.SessionContext.get() != null
                ? com.revworkforce.context.SessionContext.get().getEmployeeId()
                : "ADMIN001"; // Fallback for safety
    }

    /**
     * Initiates the employee onboarding process.
     * Collects details via modular prompts to create a new employee record.
     */
    public static void addEmployee() {
        try {
            System.out.println("\n--- ADD NEW EMPLOYEE ---");
            Employee emp = new Employee();

            // 1. Role & ID Generation
            boolean isManager = promptRole();
            String prefix = isManager ? "MGR" : "EMP";
            emp.setEmployeeId(employeeDAO.getNextId(prefix));
            System.out.println("Generated Employee ID: " + emp.getEmployeeId());

            // 2. Personal Info
            promptPersonalInfo(emp);

            // 3. Joining Date
            emp.setJoiningDate(promptJoiningDate());

            // 4. Professional Info
            promptProfessionalInfo(emp, isManager);

            // 5. Default Password & Salary
            emp.setPasswordHash(com.revworkforce.util.PasswordUtil.hashPassword("password"));
            emp.setSalary(promptSalary());

            employeeDAO.insertEmployee(emp);

            AuditService.log(getAdminId(), "CREATE", "EMPLOYEES", emp.getEmployeeId(), "Admin onboarding completed");
            logger.info("New employee onboarding completed: {} ({})", emp.getFirstName(), emp.getEmployeeId());
            System.out.println(
                    "Employee " + emp.getFirstName() + " (" + emp.getEmployeeId()
                            + ") added successfully. Default password is 'password'.");

        } catch (Exception e) {
            logger.error("Failed to add employee: " + e.getMessage(), e);
            System.out.println("Error: Failed to add employee. " + e.getMessage());
        }
    }

    private static boolean promptRole() {
        String roleInput = InputUtil.readValidatedString(
                "Is this employee a Manager? (Y/N): ",
                s -> s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("N"),
                "Invalid input. Please enter 'Y' or 'N'.");
        return roleInput.equalsIgnoreCase("Y");
    }

    private static void promptPersonalInfo(Employee emp) {
        emp.setFirstName(
                InputUtil.readValidatedString("First Name: ", s -> !s.isEmpty(), "First Name cannot be empty."));
        emp.setLastName(InputUtil.readValidatedString("Last Name: ", s -> !s.isEmpty(), "Last Name cannot be empty."));

        emp.setEmail(InputUtil.readValidatedString("Email: ", AdminService::validateEmail));

        emp.setPhone(InputUtil.readValidatedString("Phone: ", AdminService::validatePhone));

        emp.setAddress(InputUtil.readValidatedString("Address: ", s -> !s.isEmpty(), "Address cannot be empty."));
        emp.setEmergencyContact(InputUtil.readValidatedString("Emergency Contact: ", s -> !s.isEmpty(),
                "Emergency Contact cannot be empty."));

        String dobStr = InputUtil.readValidatedString("DOB (YYYY-MM-DD): ", AdminService::validateDateFormat);
        emp.setDateOfBirth(java.sql.Date.valueOf(dobStr));
    }

    private static java.sql.Date promptJoiningDate() {
        System.out.println("Joining Date: 1. Today  2. Enter Manually");
        int dateChoice = InputUtil.readInt("Select Option: ");
        if (dateChoice == 1) {
            return java.sql.Date.valueOf(java.time.LocalDate.now());
        } else {
            String dateStr = InputUtil.readValidatedString("Enter Date (YYYY-MM-DD): ",
                    AdminService::validateDateFormat);
            return java.sql.Date.valueOf(dateStr);
        }
    }

    private static void promptProfessionalInfo(Employee emp, boolean isManagerRole) {
        // Display list of all departments so the user knows valid IDs (e.g., 5 | HR)
        departmentDAO.printDepartments();
        String deptStr = InputUtil.readValidatedString("Department ID: ", AdminService::validateDepartment);
        emp.setDepartmentId(Integer.parseInt(deptStr));

        // Display list of designations compatible with the selected role (Manager vs
        // Employee)
        designationDAO.printDesignations(isManagerRole);
        String desigStr = InputUtil.readValidatedString("Designation ID: ",
                input -> validateDesignation(input, isManagerRole));
        emp.setDesignationId(Integer.parseInt(desigStr));

        // For non-Manager employees, assign a reporting manager
        if (!"MGR".equals(emp.getEmployeeId().substring(0, 3))) {
            // Show all employees to help select a Manager ID
            employeeDAO.printAllEmployees();
            String mgr = InputUtil.readString("Manager ID (Press Enter to skip): ");
            // Validate manager existence if provided
            if (!mgr.isEmpty()) {
                try {
                    while (!mgr.isEmpty() && !employeeDAO.isEmployeeExists(mgr)) {
                        System.out.println("Error: Manager ID not found.");
                        mgr = InputUtil.readString("Manager ID (Press Enter to skip): ");
                    }
                } catch (Exception e) {
                    System.out.println("Error validating manager: " + e.getMessage());
                }
            }
            emp.setManagerId(mgr.isEmpty() ? null : mgr);
        }
    }

    private static Double promptSalary() {
        String salaryInput = InputUtil.readValidatedString("Salary: ", s -> {
            try {
                double val = Double.parseDouble(s);
                return val >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }, "Invalid salary. Please enter a positive number.");
        return Double.parseDouble(salaryInput);
    }

    /*
     * =============================================================================
     * ======
     * VALIDATION METHODS
     * =============================================================================
     * ======
     */

    /**
     * Validates email format and uniqueness.
     * 
     * @param input Email to validate
     * @return Error message if invalid, null if valid
     */
    static String validateEmail(String input) {
        if (!com.revworkforce.util.ValidationUtil.isValidEmail(input))
            return "Invalid email format.";
        try {
            if (employeeDAO.isEmailExists(input))
                return "Email already exists.";
        } catch (Exception e) {
            return "Error checking email uniqueness: " + e.getMessage();
        }
        return null;
    }

    /**
     * Validates phone format and uniqueness.
     * 
     * @param input Phone to validate
     * @return Error message if invalid, null if valid
     */
    static String validatePhone(String input) {
        if (!com.revworkforce.util.ValidationUtil.isValidPhone(input))
            return "Phone must be 10 digits.";
        try {
            if (employeeDAO.isPhoneExists(input))
                return "Phone number already exists.";
        } catch (Exception e) {
            return "Error checking phone uniqueness: " + e.getMessage();
        }
        return null;
    }

    /**
     * Validates date format (YYYY-MM-DD).
     * 
     * @param input Date string to validate
     * @return Error message if invalid, null if valid
     */
    static String validateDateFormat(String input) {
        if (!input.matches("\\d{4}-\\d{2}-\\d{2}"))
            return "Invalid date format. Use YYYY-MM-DD.";
        return null;
    }

    /**
     * Validates department ID exists.
     * 
     * @param input Department ID to validate
     * @return Error message if invalid, null if valid
     */
    static String validateDepartment(String input) {
        try {
            if (departmentDAO.isDepartmentIdExists(input))
                return null;
            return "Invalid Department ID.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Validates designation ID and role match.
     * 
     * @param input         Designation ID to validate
     * @param isManagerRole Whether this is for a manager role
     * @return Error message if invalid, null if valid
     */
    static String validateDesignation(String input, boolean isManagerRole) {
        try {
            if (!designationDAO.isDesignationIdExists(input))
                return "Invalid Designation ID.";
            if (!designationDAO.isDesignationMatchRole(input, isManagerRole))
                return "Invalid Designation for role.";
            return null;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 
     * Updates an existing employee's details.
     * Allows modification of contact information and professional details
     * (Department, Designation, Salary).
     */
    public static void updateEmployee() {
        try {
            System.out.println("\n--- UPDATE EMPLOYEE ---");
            employeeDAO.printAllEmployees();
            String id = InputUtil.readString("Employee ID to Update: ");

            if (!employeeDAO.isEmployeeExists(id)) {
                System.out.println("Error: Employee ID not found.");
                return;
            }

            System.out.println("1. Update Contact Info (Phone, Address)");
            System.out.println("2. Update Professional Info (Dept, Desig, Salary)");
            int choice = InputUtil.readInt("Select Option: ");

            if (choice == 1) {
                String phone = InputUtil.readValidatedString("New Phone: ", AdminService::validatePhone);

                String addr = InputUtil.readValidatedString("New Address: ", s -> !s.isEmpty(),
                        "Address cannot be empty.");
                String emg = InputUtil.readValidatedString("New Emergency Contact: ", s -> !s.isEmpty(),
                        "Emergency Contact cannot be empty.");

                employeeDAO.updateProfile(id, phone, addr, emg);
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Phone", id, "Updated Phone");
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Emergency", id, "Updated Emergency Contact");
                logger.info("Employee contact info updated for: {}", id);
                System.out.println("Contact info updated successfully");

            } else if (choice == 2) {
                // UI Helper: Show available Departments before asking for ID
                departmentDAO.printDepartments();
                String dept = InputUtil.readValidatedString("New Department ID: ", AdminService::validateDepartment);

                // Determine if upgrading to manager or not for designation check
                // For simplicity, we assume role based on current ID or ask?
                // Better: Check current role. Assuming ID prefix logic for now or prompt.
                // Simplified: validating designation existence only here to avoid complex role
                // logic re-fetch

                // UI Helper: Show available Designations
                designationDAO.printDesignations();
                String desig = InputUtil.readString("New Designation ID: ");
                if (!designationDAO.isDesignationIdExists(desig)) {
                    System.out.println("Error: Invalid Designation ID.");
                    return;
                }

                // UI Helper: Show all employees to facilitate Manager selection
                employeeDAO.printAllEmployees();
                String mgr = InputUtil.readString("New Manager ID (Press Enter to skip): ");
                if (!mgr.isEmpty() && !employeeDAO.isEmployeeExists(mgr)) {
                    System.out.println("Error: Manager ID not found.");
                    return;
                }

                String salaryStr = InputUtil.readValidatedString("New Salary: ", s -> {
                    try {
                        return Double.parseDouble(s) >= 0;
                    } catch (Exception e) {
                        return false;
                    }
                }, "Invalid Salary");
                double salary = Double.parseDouble(salaryStr);

                employeeDAO.updateProfessionalDetails(id, dept, desig, salary, mgr.isEmpty() ? null : mgr);
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Dept/Desig", id, "Updated professional details");

                logger.info("Employee professional details updated for: {}", id);
                System.out.println("Professional info updated successfully");
            } else {
                System.out.println("Invalid option.");
            }

        } catch (Exception e) {
            logger.error("Update failed: " + e.getMessage(), e);
            System.out.println("Error: Update failed. " + e.getMessage());
        }
    }

    public static void viewAllEmployees() {
        employeeDAO.printAllEmployees();
    }

    public static void toggleEmployeeStatus() {
        try {
            System.out.println("\n--- TOGGLE EMPLOYEE STATUS ---");
            employeeDAO.printAllEmployees();
            String empId = InputUtil.readString("Employee ID to Toggle: ");
            if (!employeeDAO.isEmployeeExists(empId)) {
                System.out.println("Error: Employee ID not found.");
                return;
            }

            employeeDAO.toggleStatus(empId);
            AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", empId, "Status toggled");
            logger.info("Employee status toggled for: {}", empId);
            System.out.println("Employee status updated.");
        } catch (Exception e) {
            logger.error("Failed to toggle status: " + e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Assigns or changes the reporting manager for an employee.
     */
    public static void assignManager() {
        try {
            System.out.println("\n--- ASSIGN MANAGER ---");
            employeeDAO.printAllEmployees();
            String empId = InputUtil.readString("Employee ID: ");
            if (!employeeDAO.isEmployeeExists(empId)) {
                System.out.println("Error: Employee ID not found.");
                return;
            }

            String mgrId = InputUtil.readString("New Manager ID: ");
            if (!employeeDAO.isEmployeeExists(mgrId)) {
                System.out.println("Error: Manager ID not found.");
                return;
            }

            if (empId.equals(mgrId)) {
                System.out.println("Error: Cannot assign employee as their own manager.");
                return;
            }

            employeeDAO.assignManager(empId, mgrId);
            AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", empId, "Manager changed to " + mgrId);
            logger.info("Manager assigned for employee {}: New Manager {}", empId, mgrId);
            System.out.println("Manager assigned successfully.");
        } catch (Exception e) {
            logger.error("Failed to assign manager: " + e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void searchEmployees() {
        EmployeeService.employeeDirectory();
    }

    /**
     * Unlocks an employee's account by resetting failed login attempts.
     * This is an administrative override for security lockouts.
     */
    public static void unlockEmployeeAccount() {
        System.out.println("\n--- UNLOCK ACCOUNT ---");
        employeeDAO.printAllEmployees();
        String empId = InputUtil.readString("Employee ID to unlock: ");
        try {
            if (!employeeDAO.isEmployeeExists(empId)) {
                System.out.println("Error: Employee ID not found.");
                return;
            }

            String sql = """
                        UPDATE employees
                        SET account_locked = 0,
                            failed_login_attempts = 0
                        WHERE employee_id = ?
                    """;
            try (Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, empId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    AuditService.log(getAdminId(), "UNLOCK", "EMPLOYEES", empId, "Account unlocked");
                    logger.info("Account unlocked for employee: {}", empId);
                    System.out.println("Account unlocked successfully.");
                } else {
                    System.out.println("Employee not found.");
                }
            }
        } catch (Exception e) {
            logger.error("Unlock failed: " + e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Resets an employee's password to a new value provided by the admin.
     * The new password is immediately hashed before storage.
     */
    public static void resetUserPassword() {
        System.out.println("\n--- RESET PASSWORD ---");
        employeeDAO.printAllEmployees();
        String empId = InputUtil.readString("Employee ID to Reset Password: ");
        try {
            if (!employeeDAO.isEmployeeExists(empId)) {
                System.out.println("Error: Employee ID not found.");
                return;
            }

            System.out.println("Enter new password:");
            String newPass = InputUtil.readString("New Password: ");
            if (newPass.isEmpty()) {
                System.out.println("Password cannot be empty.");
                return;
            }

            String hash = com.revworkforce.util.PasswordUtil.hashPassword(newPass);
            boolean updated = employeeDAO.updatePassword(empId, hash);

            if (updated) {
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", empId, "Password reset by Admin");
                logger.info("Password reset by Admin for employee: {}", empId);
                System.out.println("Password reset successfully.");
            } else {
                System.out.println("Employee ID not found during update.");
            }

        } catch (Exception e) {
            logger.error("Password reset failed: " + e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /*
     * =============================================================================
     * ======
     * SYSTEM CONFIGURATION (Delegates)
     * =============================================================================
     * ======
     */

    public static void configureLeaveTypes() {
        AdminLeaveService.configureLeaveTypes();
    }

    public static void assignLeaveQuotas() {
        AdminLeaveService.assignLeaveQuotas();
    }

    public static void adjustLeaveBalance() {
        AdminLeaveService.adjustLeaveBalance();
    }

    public static void revokeLeave() {
        AdminLeaveService.revokeLeave();
    }

    public static void leaveReports() {
        AdminLeaveService.leaveReportsMenu();
    }

    public static void configureHolidays() {
        AdminConfigService.configureHolidays();
    }

    public static void manageDepartments() {
        AdminConfigService.manageDepartments();
    }

    public static void manageDesignations() {
        AdminConfigService.manageDesignations();
    }

    public static void configurePerformanceCycles() {
        AdminConfigService.configurePerformanceCycles();
    }

    public static void manageSystemPolicies() {
        AdminConfigService.manageSystemPolicies();
    }

    public static void viewAuditLogs() {
        auditDAO.printAuditLogs();
    }
}