package com.revworkforce.service;

import com.revworkforce.dao.AttendanceDAO;
import com.revworkforce.util.DateUtil;
import com.revworkforce.util.InputUtil;

import java.sql.Date;
import java.sql.ResultSet;

/**
 * Service to handle Employee Attendance.
 */
public class AttendanceService {

    private static final AttendanceDAO dao = new AttendanceDAO();

    public static void checkIn(String empId) {
        try {
            if (dao.hasCheckedIn(empId, DateUtil.getCurrentDate())) {
                System.out.println("You have already checked in today.");
            } else {
                dao.checkIn(empId);
                AuditService.log(empId, "CREATE", "ATTENDANCE", "TODAY", "Employee checked in");
                System.out.println("Check-in successful at " + DateUtil.getCurrentTimestamp());
            }
        } catch (Exception e) {
            System.err.println("Check-in failed: " + e.getMessage());
        }
    }

    public static void checkOut(String empId) {
        try {
             if (dao.hasCheckedOut(empId, DateUtil.getCurrentDate())) {
                System.out.println("You have already checked out today.");
            } else {
                dao.checkOut(empId);
                AuditService.log(empId, "UPDATE", "ATTENDANCE", "TODAY", "Employee checked out");
                System.out.println("Check-out successful at " + DateUtil.getCurrentTimestamp());
            }
        } catch (Exception e) {
            System.err.println("Check-out failed: " + e.getMessage());
        }
    }

    public static void viewMyAttendance(String empId) {
        try {
            ResultSet rs = dao.getAttendanceHistory(empId);
            System.out.println("\n--- ATTENDANCE HISTORY ---");
            System.out.println(String.format("%-15s %-25s %-25s %-10s", "Date", "Check-In", "Check-Out", "Status"));
            
            while (rs.next()) {
                System.out.println(String.format("%-15s %-25s %-25s %-10s",
                        rs.getDate("attendance_date"),
                        DateUtil.formatTimestamp(rs.getTimestamp("check_in_time")),
                        DateUtil.formatTimestamp(rs.getTimestamp("check_out_time")),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch attendance.");
        }
    }
}
