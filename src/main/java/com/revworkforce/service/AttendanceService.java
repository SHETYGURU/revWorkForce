/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.AttendanceDAO;
import com.revworkforce.util.DateUtil;
import com.revworkforce.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.ResultSet;

/**
 * Service to handle Employee Attendance.
 * Manages daily check-ins, check-outs, and historical attendance records.
 * 
 * @author Gururaj Shetty
 */
public class AttendanceService {

    private static final Logger logger = LogManager.getLogger(AttendanceService.class);

    private static AttendanceDAO dao = new AttendanceDAO();

    /**
     * Records an employee's check-in time for the day.
     * Prevents multiple check-ins on the same day.
     * 
     * @param empId The Employee ID.
     */
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
            logger.error("Check-in failed: " + e.getMessage(), e);
        }
    }

    /**
     * Records an employee's check-out time for the day.
     * Ensures an employee checks out only once per day.
     * 
     * @param empId The Employee ID.
     */
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
            logger.error("Check-out failed: " + e.getMessage(), e);
        }
    }

    /**
     * Displays the attendance history for an employee.
     * Shows dates, check-in/out times, and daily status.
     * 
     * @param empId The Employee ID.
     */
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
                        rs.getString("status")));
            }
        } catch (Exception e) {
            logger.error("Failed to fetch attendance.", e);
        }
    }
}
