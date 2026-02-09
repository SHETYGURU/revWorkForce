/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.dao;

import com.revworkforce.exception.AppException;
import com.revworkforce.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Data Access Object for Notification management.
 * Handles fetching unread counts and retrieving/marking notifications.
 */
/**
 * Data Access Object for Notification management.
 * Handles fetching unread counts, retrieving notifications, and marking them as
 * read.
 * 
 * @author Gururaj Shetty
 */
public class NotificationDAO {

    private static final Logger logger = LogManager.getLogger(NotificationDAO.class);

    // SQL Queries
    private static final String SQL_GET_UNREAD_COUNT = """
                SELECT COUNT(*)
                FROM notifications
                WHERE employee_id = ?
                  AND is_read = 0
            """;

    private static final String SQL_GET_NOTIFICATIONS = """
                SELECT notification_id, message, created_at
                FROM notifications
                WHERE employee_id = ?
                  AND is_read = 0
                ORDER BY created_at DESC
            """;

    private static final String SQL_MARK_READ = """
                UPDATE notifications
                SET is_read = 1
                WHERE notification_id = ?
            """;

    private static final String SQL_INSERT_NOTIFICATION = """
                INSERT INTO notifications (employee_id, notification_type, message)
                VALUES (?, ?, ?)
            """;

    /**
     * Creates a new notification record for an employee.
     * 
     * @param empId   The target Employee ID.
     * @param type    The notification category (e.g., LEAVE_APPROVED).
     * @param message The content to display.
     */
    public void createNotification(String empId, String type, String message) {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT_NOTIFICATION)) {
            ps.setString(1, empId);
            ps.setString(2, type);
            ps.setString(3, message);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("Error creating notification: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the count of unread notifications for a specific employee.
     *
     * @param empId The employee ID.
     * @return The number of unread notifications.
     */
    public int getUnreadCount(String empId) {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(SQL_GET_UNREAD_COUNT)) {

            ps.setString(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            // Log error internally but return 0 to avoid breaking UI
            logger.error("Error fetching notification count: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Prints unread notifications to the console and marks them as read.
     *
     * @param empId The employee ID.
     */
    public void printAndMarkRead(String empId) {

        System.out.println("\n--- NOTIFICATIONS ---");

        try (Connection con = DBConnection.getConnection();
                PreparedStatement psSelect = con.prepareStatement(SQL_GET_NOTIFICATIONS);
                PreparedStatement psUpdate = con.prepareStatement(SQL_MARK_READ)) {

            psSelect.setString(1, empId);

            try (ResultSet rs = psSelect.executeQuery()) {

                boolean hasNotifications = false;

                while (rs.next()) {
                    hasNotifications = true;
                    int notifId = rs.getInt("notification_id");
                    String message = rs.getString("message");
                    // Assuming created_at is a timestamp/date
                    String date = rs.getString("created_at");

                    System.out.println("[NEW] " + date + ": " + message);

                    // Mark as read immediately
                    psUpdate.setInt(1, notifId);
                    psUpdate.addBatch();
                }

                if (hasNotifications) {
                    psUpdate.executeBatch();
                } else {
                    System.out.println("No new notifications.");
                }
            }

        } catch (Exception e) {
            throw new AppException("Failed to retrieve notifications", e);
        }
    }
}
