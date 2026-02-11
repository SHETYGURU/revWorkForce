/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.dao;

import com.revworkforce.model.Employee;
import com.revworkforce.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Employee-related database operations.
 * Handles CRUD operations, profile retrieval, and team management queries.
 * 
 * @author Gururaj Shetty
 */
public class EmployeeDAO {

    private static final Logger logger = LogManager.getLogger(EmployeeDAO.class);

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

    /**
     * Retrieves a list of employees reporting to a specific manager.
     * Includes department and designation details for display.
     *
     * @param managerId The Manager's Employee ID.
     * @return ResultSet containing reportee details.
     * @throws Exception if a database access error occurs.
     */
    public List<Map<String, Object>> getReportees(String managerId) throws Exception {
        String sql = """
                    SELECT e.employee_id, e.first_name, e.last_name, e.email,
                           d.department_name, des.designation_name
                    FROM employees e
                    LEFT JOIN departments d ON e.department_id = d.department_id
                    LEFT JOIN designations des ON e.designation_id = des.designation_id
                    WHERE e.manager_id = ?
                      AND e.is_active = 1
                """;

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employee_id", rs.getString("employee_id"));
                    row.put("first_name", rs.getString("first_name"));
                    row.put("last_name", rs.getString("last_name"));
                    row.put("email", rs.getString("email"));
                    row.put("department_name", rs.getString("department_name"));
                    row.put("designation_name", rs.getString("designation_name"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public boolean isReportee(String managerId, String empId) throws Exception {
        String sql = "SELECT 1 FROM employees WHERE manager_id = ? AND employee_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, managerId);
            ps.setString(2, empId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Updates an employee's personal contact information.
     *
     * @param empId     The Employee ID.
     * @param phone     New phone number.
     * @param address   New address.
     * @param emergency New emergency contact.
     * @throws Exception if update fails.
     */
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
                    SELECT employee_id, first_name, date_of_birth
                    FROM employees
                    WHERE TO_CHAR(date_of_birth,'MMDD')
                          BETWEEN TO_CHAR(SYSDATE,'MMDD')
                          AND TO_CHAR(SYSDATE+30,'MMDD')
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getBirthdaysToday() throws Exception {
        String sql = """
                    SELECT employee_id, first_name
                    FROM employees
                    WHERE TO_CHAR(date_of_birth,'MMDD') = TO_CHAR(SYSDATE,'MMDD')
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getWorkAnniversaries() throws Exception {
        String sql = """
                    SELECT employee_id, first_name, joining_date
                    FROM employees
                    WHERE TO_CHAR(joining_date,'MMDD')
                          BETWEEN TO_CHAR(SYSDATE,'MMDD')
                          AND TO_CHAR(SYSDATE+30,'MMDD')
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getWorkAnniversariesToday() throws Exception {
        String sql = """
                    SELECT employee_id, first_name, joining_date
                    FROM employees
                    WHERE TO_CHAR(joining_date,'MMDD') = TO_CHAR(SYSDATE,'MMDD')
                      AND TO_CHAR(joining_date,'YYYY') != TO_CHAR(SYSDATE,'YYYY')
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    /**
     * Searches for employees matching a keyword across multiple fields (Name, ID,
     * Email, Dept, Desig).
     * Uses LIKE operators for flexible matching.
     *
     * @param keyword The search term.
     * @return ResultSet containing matching employee records.
     * @throws Exception if query fails.
     */
    public List<Map<String, Object>> searchEmployees(String keyword) throws Exception {
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

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            ps.setString(6, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employee_id", rs.getString("employee_id"));
                    row.put("first_name", rs.getString("first_name"));
                    row.put("last_name", rs.getString("last_name"));
                    row.put("email", rs.getString("email"));
                    row.put("department_name", rs.getString("department_name"));
                    row.put("designation_name", rs.getString("designation_name"));
                    list.add(row);
                }
            }
        }
        return list;
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

    public void insertEmployee(Employee emp) throws Exception {
        String sql = """
                    INSERT INTO employees
                    (employee_id, first_name, last_name, email, phone, address, emergency_contact,
                     date_of_birth, department_id, designation_id,
                     manager_id, salary, password_hash, is_active, joining_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getEmployeeId());
            ps.setString(2, emp.getFirstName());
            ps.setString(3, emp.getLastName());
            ps.setString(4, emp.getEmail());
            ps.setString(5, emp.getPhone());
            ps.setString(6, emp.getAddress());
            ps.setString(7, emp.getEmergencyContact());
            ps.setDate(8, emp.getDateOfBirth());

            if (emp.getDepartmentId() != null)
                ps.setInt(9, emp.getDepartmentId());
            else
                ps.setNull(9, java.sql.Types.INTEGER);
            if (emp.getDesignationId() != null)
                ps.setInt(10, emp.getDesignationId());
            else
                ps.setNull(10, java.sql.Types.INTEGER);

            ps.setString(11, emp.getManagerId());
            if (emp.getSalary() != null)
                ps.setDouble(12, emp.getSalary());
            else
                ps.setNull(12, java.sql.Types.DOUBLE);
            ps.setString(13, emp.getPasswordHash());
            ps.setDate(14, emp.getJoiningDate());

            ps.executeUpdate();
        }
    }

    public Map<String, Object> getAuthDetails(String empId) throws Exception {
        String sql = """
                    SELECT employee_id,
                           password_hash,
                           failed_login_attempts,
                           account_locked
                    FROM employees
                    WHERE employee_id = ?
                      AND is_active = 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> details = new HashMap<>();
                    details.put("employee_id", rs.getString("employee_id"));
                    details.put("password_hash", rs.getString("password_hash"));
                    details.put("failed_login_attempts", rs.getInt("failed_login_attempts"));
                    details.put("account_locked", rs.getInt("account_locked"));
                    return details;
                }
            }
        }
        return null; // Not found
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

    /**
     * Locks an employee's account to prevent further login attempts.
     * Typically reused after excessive failed login attempts.
     *
     * @param empId The Employee ID.
     * @throws Exception if update fails.
     */
    public void lockAccount(String empId) throws Exception {
        String sql = "UPDATE employees SET account_locked = 1 WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public boolean updatePassword(String empId, String newHash) throws Exception {
        String sql = "UPDATE employees SET password_hash = ? WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, empId);
            return ps.executeUpdate() > 0;
        }
    }

    public Map<String, Object> getSecurityDetails(String empId) throws Exception {
        String sql = """
                    SELECT q.question_text, es.answer_hash
                    FROM employee_security es
                    JOIN security_questions q ON es.question_id = q.question_id
                    WHERE es.employee_id = ?
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> details = new HashMap<>();
                    details.put("question_text", rs.getString("question_text"));
                    details.put("answer_hash", rs.getString("answer_hash"));
                    return details;
                }
            }
        }
        return null; // Not found
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
            logger.error("Error listing employees: " + e.getMessage(), e);
        }
    }

    public boolean isEmployeeExists(String empId) throws Exception {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
