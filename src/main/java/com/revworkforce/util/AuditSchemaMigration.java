package com.revworkforce.util;

import java.sql.Connection;
import java.sql.Statement;

public class AuditSchemaMigration {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
            
            String sql = "ALTER TABLE audit_logs ADD column_name VARCHAR2(100)";
            stmt.executeUpdate(sql);
            System.out.println("Migration successful: Added column_name to audit_logs.");
            
        } catch (Exception e) {
            if (e.getMessage().contains("ORA-01430")) {
                System.out.println("Migration skipped: Column already exists.");
            } else {
                e.printStackTrace();
            }
        }
    }
}
