package com.revworkforce.service;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.NotificationDAO;

import java.sql.ResultSet;

public class NotificationService {

    private static final NotificationDAO dao = new NotificationDAO();
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();

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
            System.err.println("Unable to fetch notifications");
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

    public static void generateDailyNotifications() {
        try {
            System.out.println("Generating daily notifications...");
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
            System.out.println("Daily notifications generated.");

        } catch (Exception e) {
            System.err.println("Error generating daily notifications: " + e.getMessage());
        }
    }
}
