package com.revworkforce.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents an employee's daily attendance record.
 * Maps to the 'attendance' table.
 */
public class Attendance {

    private int attendanceId;
    private String employeeId;
    private Date attendanceDate;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private String status; // PRESENT, ABSENT, HALF_DAY

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public Timestamp getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Timestamp checkInTime) { this.checkInTime = checkInTime; }

    public Timestamp getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(Timestamp checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
