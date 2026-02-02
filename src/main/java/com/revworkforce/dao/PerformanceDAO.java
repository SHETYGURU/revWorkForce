package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PerformanceDAO {

    public ResultSet getTeamGoals(String managerId) throws Exception {
        String sql = """
            SELECT g.goal_id,
                   g.employee_id,
                   g.goal_description,
                   g.deadline,
                   g.priority,
                   g.progress_percentage
            FROM goals g
            JOIN employees e ON g.employee_id = e.employee_id
            WHERE e.manager_id = ?
            ORDER BY g.deadline
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }

    public ResultSet getGoalCompletionSummary(String managerId) throws Exception {
        String sql = """
            SELECT e.employee_id,
                   COUNT(*) AS total_goals,
                   SUM(CASE WHEN g.progress_percentage = 100 THEN 1 ELSE 0 END) AS completed_goals
            FROM goals g
            JOIN employees e ON g.employee_id = e.employee_id
            WHERE e.manager_id = ?
            GROUP BY e.employee_id
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }

    public void submitSelfReview(
            String empId,
            int cycleId,
            String deliverables,
            String accomplishments,
            String improvements,
            double rating
    ) throws Exception {

        String sql = """
            INSERT INTO performance_reviews
            (employee_id, cycle_id, key_deliverables,
             major_accomplishments, areas_of_improvement,
             self_assessment_rating, status)
            VALUES (?, ?, ?, ?, ?, ?, 'SUBMITTED')
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empId);
            ps.setInt(2, cycleId);
            ps.setString(3, deliverables);
            ps.setString(4, accomplishments);
            ps.setString(5, improvements);
            ps.setDouble(6, rating);
            ps.executeUpdate();
        }
    }

    public void createGoal(
            String empId,
            String desc,
            Date deadline,
            String priority,
            String metrics
    ) throws Exception {

        String sql = """
            INSERT INTO goals
            (employee_id, goal_description, deadline,
             priority, success_metrics, progress_percentage)
            VALUES (?, ?, ?, ?, ?, 0)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empId);
            ps.setString(2, desc);
            ps.setDate(3, deadline);
            ps.setString(4, priority);
            ps.setString(5, metrics);
            ps.executeUpdate();
        }
    }

    public ResultSet getMyGoals(String empId) throws Exception {
        String sql = """
            SELECT goal_id, goal_description,
                   deadline, priority, progress_percentage
            FROM goals
            WHERE employee_id = ?
            ORDER BY deadline
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public void updateGoalProgress(int goalId, int progress) throws Exception {
        String sql = """
            UPDATE goals
            SET progress_percentage = ?
            WHERE goal_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, progress);
            ps.setInt(2, goalId);
            ps.executeUpdate();
        }
    }

    public ResultSet getMyFeedback(String empId) throws Exception {
        String sql = """
            SELECT manager_feedback, manager_rating
            FROM performance_reviews
            WHERE employee_id = ?
              AND manager_feedback IS NOT NULL
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, empId);
        return ps.executeQuery();
    }

    public ResultSet getTeamReviews(String managerId) throws Exception {
        String sql = """
            SELECT pr.review_id,
                   pr.employee_id,
                   pr.status,
                   pr.self_assessment_rating
            FROM performance_reviews pr
            JOIN employees e ON pr.employee_id = e.employee_id
            WHERE e.manager_id = ?
            ORDER BY pr.submission_date DESC
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }

    public void submitManagerFeedback(int reviewId, String feedback, int rating) throws Exception {
        String sql = """
            UPDATE performance_reviews
            SET manager_feedback = ?,
                manager_rating = ?,
                status = 'REVIEWED',
                review_date = CURRENT_TIMESTAMP
            WHERE review_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, feedback);
            ps.setInt(2, rating);
            ps.setInt(3, reviewId);
            ps.executeUpdate();
        }
    }

    public ResultSet getTeamPerformanceSummary(String managerId) throws Exception {
        String sql = """
            SELECT e.employee_id,
                   AVG(pr.manager_rating) as avg_rating
            FROM employees e
            JOIN performance_reviews pr ON e.employee_id = pr.employee_id
            WHERE e.manager_id = ?
            GROUP BY e.employee_id
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, managerId);
        return ps.executeQuery();
    }
}
