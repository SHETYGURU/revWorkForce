package com.revworkforce.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a performance review cycle (e.g., "2024 Appraisal").
 * Maps to the 'performance_cycles' table.
 */
public class PerformanceCycle {

    private int cycleId;
    private int year;
    private Date startDate;
    private Date endDate;
    private String status; // ACTIVE, CLOSED

    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
