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
            int year,
            Date startDate,
            Date endDate) throws Exception {

        String sql = """
                    INSERT INTO performance_cycles
                    (year, start_date, end_date, status)
                    VALUES (?, ?, ?, 'ACTIVE')
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.executeUpdate();
        }
    }

    public void closeCycle(int cycleId) throws Exception {

        String sql = """
                    UPDATE performance_cycles
                    SET status = 'CLOSED'
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
                    SELECT cycle_id, year, start_date, end_date, status
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
    public java.util.List<Integer> getActiveCycleIds() throws Exception {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        String sql = "SELECT cycle_id FROM performance_cycles WHERE status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("cycle_id"));
            }
        }
        return ids;
    }

    /**
     * Prints all currently active performance cycles to the console.
     * This helper is used by PerformanceService to show valid options before
     * asking the employee to select a cycle for their self-review.
     */
    public void printActiveCycles() {
        // Query only active cycles (status = 'ACTIVE'), ordered by start date
        String sql = "SELECT cycle_id, year, start_date, end_date FROM performance_cycles WHERE status = 'ACTIVE' ORDER BY start_date DESC";

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
                // Display ID, Year, and Date Range to help user identify the correct cycle
                System.out.println(
                        rs.getInt("cycle_id") + " | " +
                                "Year: " + rs.getInt("year") + " (" +
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
