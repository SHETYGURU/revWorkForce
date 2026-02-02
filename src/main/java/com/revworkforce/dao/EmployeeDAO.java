package com.revworkforce.dao;

import com.revworkforce.exception.AppException;
import com.revworkforce.model.Employee;
import com.revworkforce.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Data Access Object for Employee-related database operations.
 */
public class EmployeeDAO {

    public Employee getEmployeeById(String empId) throws Exception {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setEmployeeId(rs.getString("employee_id"));
                    emp.setFirstName(rs.getString("first_name"));
                    emp.setLastName(rs.getString("last_name"));
                    emp.setEmail(rs.getString("email"));
                    return emp;
                }
            }
        }
        return null;
    }

    public ResultSet getProfile(String empId) throws Exception {
        String sql = """
                    SELECT employee_id, first_name, last_name, email,
                           phone, address, emergency_contact, manager_id
                    FROM employees
                    WHERE employee_id = ?
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public ResultSet getReportees(String managerId) throws Exception {
        String sql = """
                    SELECT e.employee_id, e.first_name, e.last_name, e.email,
                           d.department_name, des.designation_name
                    FROM employees e
                    LEFT JOIN departments d ON e.department_id = d.department_id
                    LEFT JOIN designations des ON e.designation_id = des.designation_id
                    WHERE e.manager_id = ?
                      AND e.is_active = 1
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }

    public void updateProfile(String empId, String phone, String address, String emergency) throws Exception {
        String sql = """
                    UPDATE employees
                    SET phone = ?, address = ?, emergency_contact = ?
                    WHERE employee_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setString(2, address);
            ps.setString(3, emergency);
            ps.setString(4, empId);
            ps.executeUpdate();
        }
    }

    public void updateProfessionalDetails(String empId, String dept, String desig, double salary, String mgr)
            throws Exception {
        String sql = """
                    UPDATE employees
                    SET department_id = ?, designation_id = ?, salary = ?, manager_id = ?
                    WHERE employee_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dept);
            ps.setString(2, desig);
            ps.setDouble(3, salary);
            ps.setString(4, mgr);
            ps.setString(5, empId);
            ps.executeUpdate();
        }
    }

    public ResultSet getUpcomingBirthdays() throws Exception {
        String sql = """
                    SELECT first_name, date_of_birth
                    FROM employees
                    WHERE TO_CHAR(date_of_birth,'MMDD')
                          BETWEEN TO_CHAR(SYSDATE,'MMDD')
                          AND TO_CHAR(SYSDATE+30,'MMDD')
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getWorkAnniversaries() throws Exception {
        String sql = """
                    SELECT first_name, joining_date
                    FROM employees
                    WHERE TO_CHAR(joining_date,'MMDD')
                          BETWEEN TO_CHAR(SYSDATE,'MMDD')
                          AND TO_CHAR(SYSDATE+30,'MMDD')
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet searchEmployees(String keyword) throws Exception {
        String sql = """
                    SELECT e.employee_id, e.first_name, e.last_name, e.email,
                           d.department_name, des.designation_name
                    FROM employees e
                    LEFT JOIN departments d ON e.department_id = d.department_id
                    LEFT JOIN designations des ON e.designation_id = des.designation_id
                    WHERE LOWER(e.first_name) LIKE LOWER(?)
                       OR LOWER(e.last_name) LIKE LOWER(?)
                       OR LOWER(e.employee_id) LIKE LOWER(?)
                       OR LOWER(e.email) LIKE LOWER(?)
                       OR LOWER(d.department_name) LIKE LOWER(?)
                       OR LOWER(des.designation_name) LIKE LOWER(?)
                    ORDER BY e.employee_id
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        String searchPattern = "%" + keyword + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        ps.setString(3, searchPattern);
        ps.setString(4, searchPattern);
        ps.setString(5, searchPattern);
        ps.setString(6, searchPattern);
        return ps.executeQuery();
    }

    public String getNextId(String prefix) throws Exception {
        String sql = "SELECT MAX(employee_id) FROM employees WHERE employee_id LIKE ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maxId = rs.getString(1);
                    if (maxId != null && maxId.length() > 3) {
                        try {
                            int num = Integer.parseInt(maxId.substring(3));
                            return prefix + String.format("%03d", num + 1);
                        } catch (NumberFormatException e) {
                            // Fallback if parsing fails
                        }
                    }
                }
            }
        }
        return prefix + "001";
    }

    public boolean isEmailExists(String email) throws Exception {
        String sql = "SELECT 1 FROM employees WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isPhoneExists(String phone) throws Exception {
        String sql = "SELECT 1 FROM employees WHERE phone = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insertEmployee(String id, String firstName, String lastName, String email,
            String phone, String address, String emergencyContact, String dob,
            String dept, String desig,
            String manager, double salary, String joiningDate, String passwordHash) throws Exception {

        String sql = """
                    INSERT INTO employees
                    (employee_id, first_name, last_name, email, phone, address, emergency_contact,
                     date_of_birth, department_id, designation_id,
                     manager_id, salary, password_hash, is_active, joining_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'YYYY-MM-DD'),
                            ?, ?, ?, ?, ?, 1, TO_DATE(?,'YYYY-MM-DD'))
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, address);
            ps.setString(7, emergencyContact);
            ps.setString(8, dob);
            ps.setString(9, dept);
            ps.setString(10, desig);
            ps.setString(11, manager);
            ps.setDouble(12, salary);
            ps.setString(13, passwordHash);
            ps.setString(14, joiningDate);
            ps.executeUpdate();
        }
    }

    public ResultSet getAuthDetails(String empId) throws Exception {
        String sql = """
                    SELECT employee_id,
                           password_hash,
                           failed_login_attempts,
                           account_locked
                    FROM employees
                    WHERE employee_id = ?
                      AND is_active = 1
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public void recordSuccessfulLogin(String empId) throws Exception {
        String sql = """
                    UPDATE employees
                    SET failed_login_attempts = 0,
                        last_login = SYSTIMESTAMP
                    WHERE employee_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public void recordFailedLogin(String empId) throws Exception {
        String sql = """
                    UPDATE employees
                    SET failed_login_attempts = failed_login_attempts + 1
                    WHERE employee_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public void lockAccount(String empId) throws Exception {
        String sql = "UPDATE employees SET account_locked = 1 WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    // Implemented Methods

    public void toggleStatus(String empId) throws Exception {
        String sql = """
                    UPDATE employees
                    SET is_active = CASE WHEN is_active = 1 THEN 0 ELSE 1 END
                    WHERE employee_id = ?
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public void assignManager(String empId, String mgrId) throws Exception {
        String sql = "UPDATE employees SET manager_id = ? WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mgrId);
            ps.setString(2, empId);
            ps.executeUpdate();
        }
    }

    public void printAllEmployees() {
        String sql = "SELECT employee_id, first_name, email, is_active FROM employees ORDER BY employee_id";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- ALL EMPLOYEES ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") + " | " +
                                rs.getString("first_name") + " | " +
                                rs.getString("email") + " | Active: " +
                                (rs.getInt("is_active") == 1 ? "Yes" : "No"));
            }
        } catch (Exception e) {
            System.err.println("Error listing employees: " + e.getMessage());
        }
    }
}
