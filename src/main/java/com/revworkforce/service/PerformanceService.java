package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;
import com.revworkforce.util.InputUtil;

import java.sql.ResultSet;

public class PerformanceService {

    private static final PerformanceDAO dao = new PerformanceDAO();

    public static void reviewTeam(String managerId) {

        try {
            ResultSet rs = dao.getTeamReviews(managerId);
            System.out.println("\n--- TEAM PERFORMANCE ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("review_id") +
                                " | " + rs.getString("employee_id") +
                                " | Status: " + rs.getString("status")
                );
            }

            int reviewId = InputUtil.readInt("Review ID to process: ");
            String feedback = InputUtil.readString("Manager Feedback: ");
            int rating = InputUtil.readInt("Rating (1–5): ");

            dao.submitManagerFeedback(reviewId, feedback, rating);

            AuditService.log(
                    managerId,
                    "REVIEW",
                    "PERFORMANCE_REVIEWS",
                    String.valueOf(reviewId),
                    "Performance reviewed"
            );

            System.out.println("Performance reviewed successfully");

        } catch (Exception e) {
            System.err.println("Performance review failed");
        }
    }
    public static void submitSelfReview(String empId) {

        try {
            int cycleId = InputUtil.readInt("Performance Cycle ID: ");
            String del = InputUtil.readString("Key Deliverables: ");
            String acc = InputUtil.readString("Major Accomplishments: ");
            String imp = InputUtil.readString("Areas of Improvement: ");
            double rating = Double.parseDouble(
                    InputUtil.readString("Self Rating (1–5): ")
            );

            dao.submitSelfReview(empId, cycleId, del, acc, imp, rating);

            AuditService.log(
                    empId, "SUBMIT",
                    "PERFORMANCE_REVIEWS",
                    "NEW",
                    "Self review submitted"
            );

            System.out.println("Performance review submitted");

        } catch (Exception e) {
            System.err.println("Submission failed");
        }
    }
    public static void manageGoals(String empId) {

        try {
            ResultSet rs = dao.getMyGoals(empId);
            System.out.println("\n--- MY GOALS ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("goal_id") + " | " +
                                rs.getString("goal_description") +
                                " | Progress: " +
                                rs.getInt("progress_percentage") + "%"
                );
            }

            int goalId = InputUtil.readInt("Goal ID to update: ");
            int progress = InputUtil.readInt("New progress %: ");

            dao.updateGoalProgress(goalId, progress);

            AuditService.log(
                    empId, "UPDATE",
                    "GOALS",
                    String.valueOf(goalId),
                    "Goal progress updated"
            );

            System.out.println("Goal updated");

        } catch (Exception e) {
            System.err.println("Goal operation failed");
        }
    }
    public static void viewManagerFeedback(String empId) {

        try {
            ResultSet rs = dao.getMyFeedback(empId);

            System.out.println("\n--- MANAGER FEEDBACK ---");
            while (rs.next()) {
                System.out.println(
                        "Rating: " + rs.getInt("manager_rating") +
                                " | Feedback: " + rs.getString("manager_feedback")
                );
            }
        } catch (Exception e) {
            System.err.println("No feedback available");
        }
    }

}
