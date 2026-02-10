package com.revworkforce.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a Leave Application.
 * Maps to the 'leave_applications' table.
 * 
 * @author Gururaj Shetty
 */
public class LeaveApplication {

    private int leaveApplicationId;
    private String employeeId;
    private int leaveTypeId;
    private Date startDate;
    private Date endDate;
    private int totalDays;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED
    private String managerComments;
    private Timestamp appliedDate;
    private Timestamp reviewedDate;
    private String reviewedBy;

    // Getters and Setters
    public int getLeaveApplicationId() {
        return leaveApplicationId;
    }

    public void setLeaveApplicationId(int leaveApplicationId) {
        this.leaveApplicationId = leaveApplicationId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerComments() {
        return managerComments;
    }

    public void setManagerComments(String managerComments) {
        this.managerComments = managerComments;
    }

    public Timestamp getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Timestamp appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Timestamp getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(Timestamp reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
