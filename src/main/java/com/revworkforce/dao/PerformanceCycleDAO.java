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
}
