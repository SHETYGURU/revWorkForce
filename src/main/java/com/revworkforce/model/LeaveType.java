package com.revworkforce.model;

/**
 * Represents a type of leave (CL, PL, SL).
 * Maps to the 'leave_types' table.
 */
public class LeaveType {

    private int leaveTypeId;
    private String leaveTypeName;
    private String description;

    public int getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(int leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public String getLeaveTypeName() { return leaveTypeName; }
    public void setLeaveTypeName(String leaveTypeName) { this.leaveTypeName = leaveTypeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
