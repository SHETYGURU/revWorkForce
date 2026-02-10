package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.*;

/**
 * DAO for Attendance Tracking.
 * Handles check-in, check-out, and attendance history retrieval.
 * 
 * @author Gururaj Shetty
 */
public class AttendanceDAO {

    public boolean hasCheckedIn(String empId, Date date) throws Exception {
        String sql = "SELECT 1 FROM attendance WHERE employee_id = ? AND attendance_date = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.setDate(2, date);
            return ps.executeQuery().next();
        }
    }

    public void checkIn(String empId) throws Exception {
        String sql = """
                    INSERT INTO attendance (employee_id, attendance_date, check_in_time, status)
                    VALUES (?, CURRENT_DATE, CURRENT_TIMESTAMP, 'PRESENT')
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public boolean hasCheckedOut(String empId, Date date) throws Exception {
        String sql = "SELECT 1 FROM attendance WHERE employee_id = ? AND attendance_date = ? AND check_out_time IS NOT NULL";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.setDate(2, date);
            return ps.executeQuery().next();
        }
    }

    public void checkOut(String empId) throws Exception {
        String sql = """
                    UPDATE attendance
                    SET check_out_time = CURRENT_TIMESTAMP
                    WHERE employee_id = ? AND attendance_date = CURRENT_DATE
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.executeUpdate();
        }
    }

    public ResultSet getAttendanceHistory(String empId) throws Exception {
        String sql = "SELECT * FROM attendance WHERE employee_id = ? ORDER BY attendance_date DESC";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public ResultSet getTeamAttendanceSummary(String managerId) throws Exception {
        String sql = """
                    SELECT a.employee_id,
                           SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_days,
                           SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_days
                    FROM attendance a
                    JOIN employees e ON a.employee_id = e.employee_id
                    WHERE e.manager_id = ?
                    GROUP BY a.employee_id
                """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }
}
