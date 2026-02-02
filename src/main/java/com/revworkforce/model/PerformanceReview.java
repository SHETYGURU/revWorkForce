package com.revworkforce.model;

import java.sql.Timestamp;

/**
 * Represents a performance review document.
 * Maps to the 'performance_reviews' table.
 */
public class PerformanceReview {

    private int reviewId;
    private String employeeId;
    private int cycleId;
    private String keyDeliverables;
    private String majorAccomplishments;
    private String areasOfImprovement;
    private Double selfAssessmentRating;
    private String managerFeedback;
    private Double managerRating;
    private String status; // DRAFT, SUBMITTED, REVIEWED
    private Timestamp submittedDate;
    private Timestamp reviewedDate;
    private String reviewedBy;

    // Getters / Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }

    public String getKeyDeliverables() { return keyDeliverables; }
    public void setKeyDeliverables(String keyDeliverables) { this.keyDeliverables = keyDeliverables; }

    public String getMajorAccomplishments() { return majorAccomplishments; }
    public void setMajorAccomplishments(String majorAccomplishments) { this.majorAccomplishments = majorAccomplishments; }

    public String getAreasOfImprovement() { return areasOfImprovement; }
    public void setAreasOfImprovement(String areasOfImprovement) { this.areasOfImprovement = areasOfImprovement; }

    public Double getSelfAssessmentRating() { return selfAssessmentRating; }
    public void setSelfAssessmentRating(Double selfAssessmentRating) { this.selfAssessmentRating = selfAssessmentRating; }

    public String getManagerFeedback() { return managerFeedback; }
    public void setManagerFeedback(String managerFeedback) { this.managerFeedback = managerFeedback; }

    public Double getManagerRating() { return managerRating; }
    public void setManagerRating(Double managerRating) { this.managerRating = managerRating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(Timestamp submittedDate) { this.submittedDate = submittedDate; }

    public Timestamp getReviewedDate() { return reviewedDate; }
    public void setReviewedDate(Timestamp reviewedDate) { this.reviewedDate = reviewedDate; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
}
