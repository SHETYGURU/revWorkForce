package com.revworkforce.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Utility class for managing Database connections.
 * Uses a static block to load the Oracle JDBC driver once.
 */
public class DBConnection {

    // Database Configuration Constants
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "revworkforce_db";
    private static final String PASSWORD = "revworkforce";

    /*
     * Static initialization block to load the driver class.
     * This executes only once when the class is loaded.
     */
    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            // Convert checked exception to runtime to halt application start if driver is missing
            throw new RuntimeException("CRITICAL: Oracle JDBC Driver not found!", e);
        }
    }

    /**
     * Establishes and returns a new connection to the database.
     *
     * @return A valid Connection object.
     * @throws Exception If a database access error occurs.
     */
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
