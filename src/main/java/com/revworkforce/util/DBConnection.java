package com.revworkforce.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing Database connections using HikariCP.
 * Loads configuration from db.properties and manages a connection pool.
 * 
 * @author Gururaj Shetty
 */
public class DBConnection {

    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    private static HikariDataSource dataSource;

    private DBConnection() {
        // Private constructor
    }

    static {
        try {
            Properties props = new Properties();
            // Use absolute path for safety in static context
            try (InputStream input = DBConnection.class.getResourceAsStream("/db.properties")) {
                if (input == null) {
                    // Fallback: Try context class loader
                    try (InputStream ctxInput = Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("db.properties")) {
                        if (ctxInput == null) {
                            throw new RuntimeException(
                                    "db.properties not found in classpath (checked both /db.properties and via ContextClassLoader)");
                        }
                        props.load(ctxInput);
                    }
                } else {
                    props.load(input);
                }
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver"));

            // Pool Configuration for Performance
            config.setMaximumPoolSize(10); // Max connections in pool
            config.setMinimumIdle(2); // Min idle connections
            config.setIdleTimeout(30000); // 30 seconds
            config.setConnectionTimeout(30000); // 30 seconds wait time
            config.setPoolName("RevWorkForcePool");

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP Connection Pool initialized successfully.");

        } catch (Exception e) {
            logger.fatal("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database Initialization Failed", e);
        }
    }

    /**
     * Gets a connection from the HikariCP pool.
     *
     * @return A valid Connection object.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool. call strictly on application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("HikariCP Connection Pool closed.");
        }
    }
}
