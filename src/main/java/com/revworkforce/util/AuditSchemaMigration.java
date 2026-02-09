package com.revworkforce.util;

import java.sql.Connection;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuditSchemaMigration {

    private static final Logger logger = LogManager.getLogger(AuditSchemaMigration.class);

    private AuditSchemaMigration() {
        // Private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
                Statement stmt = con.createStatement()) {

            String sql = "ALTER TABLE audit_logs ADD column_name VARCHAR2(100)";
            stmt.executeUpdate(sql);
            logger.info("Migration successful: Added column_name to audit_logs.");

        } catch (Exception e) {
            if (e.getMessage().contains("ORA-01430")) {
                logger.warn("Migration skipped: Column already exists.");
            } else {
                logger.error("Migration failed", e);
            }
        }
    }
}
