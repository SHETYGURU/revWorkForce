package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for checking Roles.
 * 
 * @author Gururaj Shetty
 */
public class RoleDAO {

    /**
     * Retrieves the primary role for an employee.
     * Hierarchy: ADMIN > MANAGER > EMPLOYEE.
     * 
     * @param empId The employee ID.
     * @return The determined role name (e.g., "Admin", "Manager", "Employee").
     * @throws Exception If database access fails.
     */
    public String getEmployeeRole(String empId) throws Exception {
        String sql = """
                    SELECT r.role_name
                    FROM roles r
                    JOIN employee_roles er ON r.role_id = er.role_id
                    WHERE er.employee_id = ?
                """;

        List<String> roles = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("role_name"));
                }
            }
        }

        if (roles.isEmpty()) {
            return "Employee"; // Default fallback
        }

        // Determine highest privilege
        if (roles.stream().anyMatch(r -> r.equalsIgnoreCase("Admin"))) {
            return "Admin";
        }
        if (roles.stream().anyMatch(r -> r.equalsIgnoreCase("Manager"))) {
            return "Manager";
        }

        return "Employee";
    }
}
