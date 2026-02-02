package com.revworkforce.config;

/**
 * System-wide constants.
 */
public class AppConstants {

    // Date Patterns
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Application Roles (Matching DB)
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_EMPLOYEE = "Employee";

    // Leave Types
    public static final String LEAVE_CASUAL = "CL";
    public static final String LEAVE_SICK = "SL";
    public static final String LEAVE_PAID = "PL";
    
    // Statuses
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private AppConstants() {
        // Prevent instantiation
    }
}
