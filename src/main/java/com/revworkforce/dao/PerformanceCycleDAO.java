package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.*;

/**
 * DAO for managing Performance Cycles.
 * Handles creation and retrieval of appraisal cycles.
 * 
 * @author Gururaj Shetty
 */
public class PerformanceCycleDAO {

    public void createCycle(
            String cycleName,
            Date startDate,
            Date endDate) throws Exception {

        String sql = """
                    INSERT INTO performance_cycles
                    (cycle_name, start_date, end_date, is_active)
                    VALUES (?, ?, ?, 1)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cycleName);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.executeUpdate();
        }
    }

    public void closeCycle(int cycleId) throws Exception {

        String sql = """
                    UPDATE performance_cycles
                    SET is_active = 0
                    WHERE cycle_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cycleId);
            ps.executeUpdate();
        }
    }

    public ResultSet getAllCycles() throws Exception {

        String sql = """
                    SELECT cycle_id, cycle_name, start_date, end_date, is_active
                    FROM performance_cycles
                    ORDER BY start_date DESC
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    /**
     * Prints all currently active performance cycles to the console.
     * This helper is used by PerformanceService to show valid options before
     * asking the employee to select a cycle for their self-review.
     */
    public void printActiveCycles() {
        // Query only active cycles (is_active = 1), ordered by start date
        String sql = "SELECT cycle_id, cycle_name, start_date, end_date FROM performance_cycles WHERE is_active = 1 ORDER BY start_date DESC";

        // Try-with-resources handles closing connection, statement, and result set
        // automatically
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- ACTIVE PERFORMANCE CYCLES ---");
            boolean found = false;
            // Loop through results
            while (rs.next()) {
                found = true;
                // Display ID, Name, and Date Range to help user identify the correct cycle
                System.out.println(
                        rs.getInt("cycle_id") + " | " +
                                rs.getString("cycle_name") + " (" +
                                rs.getDate("start_date") + " to " +
                                rs.getDate("end_date") + ")");
            }
            // Inform user if no cycles are open
            if (!found) {
                System.out.println("No active performance cycles found.");
            }
        } catch (Exception e) {
            System.out.println("Error listing performance cycles: " + e.getMessage());
        }
    }
}
