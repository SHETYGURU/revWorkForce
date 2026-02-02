package com.revworkforce.context;

import com.revworkforce.model.Employee;

public class SessionContext {

    private static Employee employee;
    private static long lastAccess;

    private static final long TIMEOUT_MS = 10 * 60 * 1000; // 10 minutes

    public static void set(Employee emp) {
        employee = emp;
        touch();
    }

    public static Employee get() {
        if (isExpired()) {
            clear();
            System.out.println("Session expired. Please login again.");
            return null;
        }
        touch();
        return employee;
    }

    public static void clear() {
        employee = null;
    }

    private static void touch() {
        lastAccess = System.currentTimeMillis();
    }

    private static boolean isExpired() {
        return employee != null &&
                System.currentTimeMillis() - lastAccess > TIMEOUT_MS;
    }
}
