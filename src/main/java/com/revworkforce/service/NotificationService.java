/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.NotificationDAO;

import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class for handling system notifications.
 * Manages notification creation, retrieval, and daily generation tasks.
 * 
 * @author Gururaj Shetty
 */
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private static NotificationDAO dao = new NotificationDAO();
    private static EmployeeDAO employeeDAO = new EmployeeDAO();

    public static int getUnreadCount(String empId) {
        try {
            return dao.getUnreadCount(empId);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void viewNotifications(String empId) {
        try {
            dao.printAndMarkRead(empId);
        } catch (Exception e) {
            logger.error("Unable to fetch notifications", e);
        }
    }

    public static void notifyLeaveUpdate(String empId, String status) {
        String message = "Your leave request has been " + status + ".";
        dao.createNotification(empId, "LEAVE_" + status, message);
    }

    public static void notifyPerformanceFeedback(String empId) {
        String message = "You have received new performance feedback from your manager.";
        dao.createNotification(empId, "PERFORMANCE", message);
    }

    /**
     * Generates recurring daily notifications.
     * triggers automated alerts for Birthdays and Work Anniversaries.
     * Should be scheduled to run once per day.
     */
    public static void generateDailyNotifications() {
        try {
            logger.info("Generating daily notifications...");
            // Birthdays
            try (ResultSet rs = employeeDAO.getBirthdaysToday()) {
                while (rs.next()) {
                    String empId = rs.getString("employee_id");
                    String name = rs.getString("first_name");
                    dao.createNotification(empId, "BIRTHDAY", "Happy Birthday, " + name + "!");
                }
            }

            // Anniversaries
            try (ResultSet rs = employeeDAO.getWorkAnniversariesToday()) {
                while (rs.next()) {
                    String empId = rs.getString("employee_id");
                    dao.createNotification(empId, "ANNIVERSARY", "Happy Work Anniversary!");
                }
            }
            logger.info("Daily notifications generated.");

        } catch (Exception e) {
            logger.error("Error generating daily notifications: " + e.getMessage(), e);
        }
    }
}
