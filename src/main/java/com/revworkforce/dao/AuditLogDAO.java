/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for System Audit Logging.
 * Records critical actions to the database for security and tracking.
 * 
 * @author Gururaj Shetty
 */
public class AuditLogDAO {

    private static final Logger logger = LogManager.getLogger(AuditLogDAO.class);

    public void log(
            String employeeId,
            String action,
            String table,
            String recordId,
            String description) throws Exception {

        String sql = """
                    INSERT INTO audit_logs
                    (employee_id, action, table_name, record_id, new_value)
                    VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setString(2, action);
            ps.setString(3, table);
            ps.setString(4, recordId);
            ps.setString(5, description);
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves and prints the latest system audit logs.
     * Joins with Employee table to show readable names.
     */
    public void printAuditLogs() {

        String sql = """
                    SELECT a.employee_id, e.first_name, e.last_name, a.action, a.table_name,
                           a.record_id, a.new_value, a.created_at
                    FROM audit_logs a
                    LEFT JOIN employees e ON a.employee_id = e.employee_id
                    ORDER BY a.created_at DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- SYSTEM AUDIT LOGS ---");
            System.out.printf("%-10s | %-15s | %-10s | %-15s | %-10s | %-30s | %-20s%n",
                    "Emp ID", "Name", "Action", "Table", "Record ID", "Description", "Time");
            System.out.println("-".repeat(125));

            while (rs.next()) {
                String name = rs.getString("first_name") + " " + rs.getString("last_name");

                System.out.printf("%-10s | %-15s | %-10s | %-15s | %-10s | %-30s | %s%n",
                        rs.getString("employee_id"),
                        (name.equals("null null") ? "Unknown" : name),
                        rs.getString("action"),
                        rs.getString("table_name"),
                        rs.getString("record_id"),
                        rs.getString("new_value"),
                        rs.getTimestamp("created_at"));
            }
            System.out.println();
        } catch (Exception e) {
            logger.error("Unable to fetch audit logs: " + e.getMessage(), e);
        }
    }

}
