package com.revworkforce.model;

import java.sql.Date;

/**
 * Represents a performance goal set for an employee.
 * Maps to the 'goals' table.
 */
public class Goal {

    private int goalId;
    private String employeeId;
    private int cycleId;
    private String goalDescription;
    private Date deadline;
    private String priority; // High, Medium, Low
    private String successMetrics;
    private int progressPercentage;
    private String status; // PENDING, COMPLETED
    private String managerComments;

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }

    public String getGoalDescription() { return goalDescription; }
    public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSuccessMetrics() { return successMetrics; }
    public void setSuccessMetrics(String successMetrics) { this.successMetrics = successMetrics; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getManagerComments() { return managerComments; }
    public void setManagerComments(String managerComments) { this.managerComments = managerComments; }
}
