package com.revworkforce.service;

import com.revworkforce.dao.NotificationDAO;

public class NotificationService {

    private static final NotificationDAO dao = new NotificationDAO();

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
}
