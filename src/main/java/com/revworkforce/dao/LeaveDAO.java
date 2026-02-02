package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.*;

public class LeaveDAO {

    public ResultSet getLeaveBalances(String empId) throws Exception {
        String sql = """
            SELECT lt.leave_type_name, lb.total_allocated,
                   lb.used_leaves, lb.available_leaves
            FROM leave_balances lb
            JOIN leave_types lt ON lb.leave_type_id = lt.leave_type_id
            WHERE lb.employee_id = ?
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public ResultSet getTeamLeaveRequests(String managerId) throws Exception {

        String sql = """
        SELECT la.leave_application_id,
               la.employee_id,
               la.start_date,
               la.end_date,
               la.status
        FROM leave_applications la
        JOIN employees e ON la.employee_id = e.employee_id
        WHERE e.manager_id = ?
          AND la.status = 'PENDING'
        ORDER BY la.applied_date
    """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }

    public void updateLeaveStatus(
            int leaveId,
            String managerId,
            String status,
            String comments
    ) throws Exception {

        String sql = """
        UPDATE leave_applications
        SET status = ?, manager_comments = ?, reviewed_by = ?, reviewed_date = SYSDATE
        WHERE leave_application_id = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, comments);
            ps.setString(3, managerId);
            ps.setInt(4, leaveId);
            ps.executeUpdate();
        }
    }

    public void applyLeave(
            String empId,
            int leaveTypeId,
            Date start,
            Date end,
            String reason
    ) throws Exception {

        String sql = """
            INSERT INTO leave_applications
            (employee_id, leave_type_id, start_date, end_date,
             status, reason)
            VALUES (?, ?, ?, ?, 'PENDING', ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empId);
            ps.setInt(2, leaveTypeId);
            ps.setDate(3, start);
            ps.setDate(4, end);
            ps.setString(5, reason);
            ps.executeUpdate();
        }
    }

    public ResultSet getMyLeaves(String empId) throws Exception {
        String sql = """
            SELECT leave_application_id, start_date, end_date, status
            FROM leave_applications
            WHERE employee_id = ?
            ORDER BY applied_date DESC
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public ResultSet getLeaveStatistics() throws Exception {
        String sql = "SELECT status, COUNT(*) as count FROM leave_applications GROUP BY status";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getDepartmentLeaveReport() throws Exception {
        String sql = """
            SELECT d.department_name, e.first_name || ' ' || e.last_name as emp_name,
                   lt.leave_type_name, SUM(la.end_date - la.start_date + 1) as days_taken
            FROM leave_applications la
            JOIN employees e ON la.employee_id = e.employee_id
            JOIN departments d ON e.department_id = d.department_id
            JOIN leave_types lt ON la.leave_type_id = lt.leave_type_id
            WHERE la.status = 'APPROVED'
            GROUP BY d.department_name, e.first_name, e.last_name, lt.leave_type_name
            ORDER BY d.department_name, emp_name
        """;
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getEmployeeLeaveReport(String empId) throws Exception {
        String sql = """
            SELECT lt.leave_type_name, la.start_date, la.end_date, la.status, la.reason
            FROM leave_applications la
            JOIN leave_types lt ON la.leave_type_id = lt.leave_type_id
            WHERE la.employee_id = ?
            ORDER BY la.start_date DESC
        """;
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public void cancelLeave(int leaveId, String empId) throws Exception {
        String sql = """
            UPDATE leave_applications
            SET status = 'CANCELLED'
            WHERE leave_application_id = ?
              AND employee_id = ?
              AND status = 'PENDING'
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, leaveId);
            ps.setString(2, empId);
            ps.executeUpdate();
        }
    }

    public ResultSet getTeamLeaveCalendar(String managerId) throws Exception {

        String sql = """
        SELECT la.employee_id,
               la.start_date,
               la.end_date,
               lt.leave_type_name
        FROM leave_applications la
        JOIN employees e ON la.employee_id = e.employee_id
        JOIN leave_types lt ON la.leave_type_id = lt.leave_type_id
        WHERE e.manager_id = ?
          AND la.status = 'APPROVED'
        ORDER BY la.start_date
    """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }
    public ResultSet getTeamLeaveBalances(String managerId) throws Exception {

        String sql = """
        SELECT lb.employee_id,
               lt.leave_type_name,
               lb.total_allocated,
               lb.used_leaves,
               lb.available_leaves
        FROM leave_balances lb
        JOIN leave_types lt ON lb.leave_type_id = lt.leave_type_id
        JOIN employees e ON lb.employee_id = e.employee_id
        WHERE e.manager_id = ?
        ORDER BY lb.employee_id, lt.leave_type_name
    """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }
    public void assignLeaveQuota(
            String empId,
            int leaveTypeId,
            int year,
            int total
    ) throws Exception {

        String updateSql = """
            UPDATE leave_balances
            SET total_allocated = ?,
                available_leaves = ? - used_leaves
            WHERE employee_id = ? AND leave_type_id = ? AND year = ?
        """;

        String insertSql = """
            INSERT INTO leave_balances
            (employee_id, leave_type_id, year,
             total_allocated, used_leaves, available_leaves)
            VALUES (?, ?, ?, ?, 0, ?)
        """;

        try (Connection con = DBConnection.getConnection()) {
            boolean updated = false;
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setInt(1, total);
                ps.setInt(2, total);
                ps.setString(3, empId);
                ps.setInt(4, leaveTypeId);
                ps.setInt(5, year);
                int rows = ps.executeUpdate();
                updated = (rows > 0);
            }

            if (!updated) {
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setString(1, empId);
                    ps.setInt(2, leaveTypeId);
                    ps.setInt(3, year);
                    ps.setInt(4, total);
                    ps.setInt(5, total);
                    ps.executeUpdate();
                }
            }
        }
    }
    public void adjustLeaveBalance(
            int balanceId,
            int total,
            int used
    ) throws Exception {

        int available = total - used;

        String sql = """
        UPDATE leave_balances
        SET total_allocated = ?,
            used_leaves = ?,
            available_leaves = ?
        WHERE leave_balance_id = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, total);
            ps.setInt(2, used);
            ps.setInt(3, available);
            ps.setInt(4, balanceId);
            ps.executeUpdate();
        }
    }
    public void revokeApprovedLeave(int leaveId) throws Exception {

        String sql = """
        UPDATE leave_applications
        SET status = 'REVOKED'
        WHERE leave_application_id = ?
          AND status = 'APPROVED'
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, leaveId);
            ps.executeUpdate();
        }
    }

    public void addHoliday(String name, Date date) throws Exception {

        String sql = """
        INSERT INTO holidays (holiday_name, holiday_date)
        VALUES (?, ?)
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDate(2, date);
            ps.executeUpdate();
        }
    }

}
