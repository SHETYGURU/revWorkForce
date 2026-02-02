package com.revworkforce.service;

import com.revworkforce.dao.AuditLogDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.util.DBConnection;
import com.revworkforce.util.InputUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Service class for Administrator operations.
 * Handles extensive user management and system configuration.
 */
public class AdminService {

    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final AuditLogDAO auditDAO = new AuditLogDAO();
    private static final com.revworkforce.dao.DepartmentDAO departmentDAO = new com.revworkforce.dao.DepartmentDAO();
    private static final com.revworkforce.dao.DesignationDAO designationDAO = new com.revworkforce.dao.DesignationDAO();

    /* ===================================================================================
       EMPLOYEE MANAGEMENT
       =================================================================================== */

    private static String getAdminId() {
        return com.revworkforce.context.SessionContext.get() != null 
               ? com.revworkforce.context.SessionContext.get().getEmployeeId() 
               : "ADMIN001"; // Fallback for safety
    }

    public static void addEmployee() {
        try {
            System.out.println("\n--- ADD NEW EMPLOYEE ---");
            
            // 1. Role & ID Generation
            String roleInput = InputUtil.readValidatedString(
                "Is this employee a Manager? (Y/N): ", 
                s -> s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("N"), 
                "Invalid input. Please enter 'Y' or 'N'."
            );
            String prefix = roleInput.equalsIgnoreCase("Y") ? "MGR" : "EMP";
            String id = employeeDAO.getNextId(prefix);
            System.out.println("Generated Employee ID: " + id);
            
            // 2. Personal Info
            String firstName = InputUtil.readValidatedString("First Name: ", s -> !s.isEmpty(), "First Name cannot be empty.");
            String lastName = InputUtil.readValidatedString("Last Name: ", s -> !s.isEmpty(), "Last Name cannot be empty.");
            
            // Email Validation & Uniqueness Check
            String email = InputUtil.readValidatedString("Email: ", input -> {
                if (!com.revworkforce.util.ValidationUtil.isValidEmail(input)) return "Invalid email format.";
                try {
                    if (employeeDAO.isEmailExists(input)) return "Email already exists.";
                } catch (Exception e) {
                    return "Error checking email uniqueness: " + e.getMessage();
                }
                return null;
            });

            // Phone Validation & Uniqueness Check
            String phone = InputUtil.readValidatedString("Phone: ", input -> {
                if (!com.revworkforce.util.ValidationUtil.isValidPhone(input)) return "Phone must be 10 digits.";
                try {
                    if (employeeDAO.isPhoneExists(input)) return "Phone number already exists.";
                } catch (Exception e) {
                    return "Error checking phone uniqueness: " + e.getMessage();
                }
                return null;
            });

            String address = InputUtil.readValidatedString("Address: ", s -> !s.isEmpty(), "Address cannot be empty.");
            String emergencyContact = InputUtil.readValidatedString("Emergency Contact: ", s -> !s.isEmpty(), "Emergency Contact cannot be empty.");
            
            // DOB Validation
            String dob = InputUtil.readValidatedString("DOB (YYYY-MM-DD): ", input -> {
                if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) return "Invalid date format. Use YYYY-MM-DD.";
                return null;
            });
            
            // 3. Joining Date
            System.out.println("Joining Date: 1. Today  2. Enter Manually");
            int dateChoice = InputUtil.readInt("Select Option: ");
            String joiningDate;
            if (dateChoice == 1) {
                joiningDate = java.time.LocalDate.now().toString();
            } else {
                joiningDate = InputUtil.readValidatedString("Enter Date (YYYY-MM-DD): ", input -> {
                    if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) return "Invalid date format. Use YYYY-MM-DD.";
                    return null;
                });
            }
            
            // 4. Professional Info
            departmentDAO.printDepartments();
            String dept = InputUtil.readValidatedString("Department ID: ", input -> {
                try {
                    if (departmentDAO.isDepartmentIdExists(input)) return null;
                    return "Invalid Department ID. Please choose from the list above.";
                } catch (Exception e) {
                    return "Error validating Department ID: " + e.getMessage();
                }
            });
            
            boolean isManagerRole = "MGR".equals(prefix);
            designationDAO.printDesignations(isManagerRole);
            String desig = InputUtil.readValidatedString("Designation ID: ", input -> {
                try {
                    if (!designationDAO.isDesignationIdExists(input)) {
                         return "Invalid Designation ID. Please choose from the list above.";
                    }
                    if (!designationDAO.isDesignationMatchRole(input, isManagerRole)) {
                        return "Invalid Designation for this role. Please choose a " + (isManagerRole ? "Manager" : "Non-Manager") + " designation.";
                    }
                    return null;
                } catch (Exception e) {
                    return "Error validating Designation ID: " + e.getMessage();
                }
            });
            
            String mgr = "";
            if (!prefix.equals("MGR")) { 
                 mgr = InputUtil.readString("Manager ID (Press Enter to skip): ");
            }
            
            String salaryInput = InputUtil.readValidatedString("Salary: ", s -> {
                try {
                    Double.parseDouble(s);
                    return true;
                } catch(NumberFormatException e) {
                    return false;
                }
            }, "Invalid salary. Please enter a number.");
            double salary = Double.parseDouble(salaryInput);
    
            // 5. Default Password
            String passwordHash = com.revworkforce.util.PasswordUtil.hashPassword("password");
            
            employeeDAO.insertEmployee(
                    id, firstName, lastName, email, phone, address, emergencyContact, dob,
                    dept, desig, mgr, salary, joiningDate, passwordHash
            );
            
            AuditService.log(getAdminId(), "CREATE", "EMPLOYEES", id, "Admin onboarding completed");
            System.out.println("Employee " + firstName + " (" + id + ") added successfully. Default password is 'password'.");
            
        } catch (Exception e) {
             System.err.println("Failed to add employee: " + e.getMessage());
        }
    }

    public static void updateEmployee() {
        try {
            System.out.println("\n--- UPDATE EMPLOYEE ---");
            String id = InputUtil.readString("Employee ID to Update: ");
            
            System.out.println("1. Update Contact Info (Phone, Address)");
            System.out.println("2. Update Professional Info (Dept, Desig, Salary)");
            int choice = InputUtil.readInt("Select Option: ");

            if (choice == 1) {
                String phone = InputUtil.readValidatedString("New Phone: ", input -> {
                    if (!com.revworkforce.util.ValidationUtil.isValidPhone(input)) return "Phone must be 10 digits.";
                    try {
                        if (employeeDAO.isPhoneExists(input)) return "Phone number already exists.";
                    } catch (Exception e) {
                        return "Error checking phone uniqueness: " + e.getMessage();
                    }
                    return null;
                });
                
                String addr = InputUtil.readValidatedString("New Address: ", s -> !s.isEmpty(), "Address cannot be empty.");
                String emg = InputUtil.readValidatedString("New Emergency Contact: ", s -> !s.isEmpty(), "Emergency Contact cannot be empty.");
                
                employeeDAO.updateProfile(id, phone, addr, emg);
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Phone", id, "Updated Phone");
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Address", id, "Updated Address");
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Emergency", id, "Updated Emergency Contact");
                System.out.println("Contact info updated successfully");

            } else if (choice == 2) {
                String dept = InputUtil.readString("New Department ID: ");
                String desig = InputUtil.readString("New Designation ID: ");
                String mgr = InputUtil.readString("New Manager ID: ");
                double salary = Double.parseDouble(InputUtil.readString("New Salary: "));
                
                employeeDAO.updateProfessionalDetails(id, dept, desig, salary, mgr);
                AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", "Dept/Desig", id, "Updated professional details");
                System.out.println("Professional info updated successfully");
            } else {
                System.out.println("Invalid option.");
            }
            
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public static void viewAllEmployees() {
        employeeDAO.printAllEmployees(); 
    }

    public static void toggleEmployeeStatus() {
        try {
            String empId = InputUtil.readString("Employee ID to Toggle: ");
            employeeDAO.toggleStatus(empId);
            AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", empId, "Status toggled");
            System.out.println("Employee status updated.");
        } catch (Exception e) {
             System.err.println("Failed to toggle status: " + e.getMessage());
        }
    }

    public static void assignManager() {
        try {
            String empId = InputUtil.readString("Employee ID: ");
            String mgrId = InputUtil.readString("New Manager ID: ");
            employeeDAO.assignManager(empId, mgrId);
            AuditService.log(getAdminId(), "UPDATE", "EMPLOYEES", empId, "Manager changed to " + mgrId);
            System.out.println("Manager assigned successfully.");
        } catch (Exception e) {
            System.err.println("Failed to assign manager: " + e.getMessage());
        }
    }

    public static void searchEmployees() {
        EmployeeService.employeeDirectory();
    }

    public static void unlockEmployeeAccount() {
        String empId = InputUtil.readString("Employee ID to unlock: ");
        try {
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
                     AuditService.log(getAdminId(),"UNLOCK","EMPLOYEES", empId,"Account unlocked");
                     System.out.println("Account unlocked successfully.");
                } else {
                    System.out.println("Employee not found.");
                }
            }
        } catch (Exception e) {
            System.err.println("Unlock failed: " + e.getMessage());
        }
    }

    /* ===================================================================================
       SYSTEM CONFIGURATION (Delegates)
       =================================================================================== */

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