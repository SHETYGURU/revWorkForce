package com.revworkforce.model;

/**
 * Represents the leave balance for an employee for a specific year.
 * Maps to the 'leave_balances' table.
 */
public class LeaveBalance {

    private int leaveBalanceId;
    private String employeeId;
    private int leaveTypeId;
    private int year;
    private int totalAllocated;
    private int usedLeaves;
    private int availableLeaves;

    public int getLeaveBalanceId() { return leaveBalanceId; }
    public void setLeaveBalanceId(int leaveBalanceId) { this.leaveBalanceId = leaveBalanceId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(int leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getTotalAllocated() { return totalAllocated; }
    public void setTotalAllocated(int totalAllocated) { this.totalAllocated = totalAllocated; }

    public int getUsedLeaves() { return usedLeaves; }
    public void setUsedLeaves(int usedLeaves) { this.usedLeaves = usedLeaves; }

    public int getAvailableLeaves() { return availableLeaves; }
    public void setAvailableLeaves(int availableLeaves) { this.availableLeaves = availableLeaves; }
}
