package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.*;

public class SystemPolicyDAO {

    public void updatePolicy(String name, String value) throws Exception {

        String sql = """
            MERGE INTO system_policies sp
            USING dual
            ON (sp.policy_name = ?)
            WHEN MATCHED THEN
                UPDATE SET sp.policy_value = ?, sp.updated_at = CURRENT_TIMESTAMP
            WHEN NOT MATCHED THEN
                INSERT (policy_name, policy_value)
                VALUES (?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, value);
            ps.setString(3, name);
            ps.setString(4, value);
            ps.executeUpdate();
        }
    }

    public void deletePolicy(String name) throws Exception {
        String sql = "DELETE FROM system_policies WHERE policy_name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new Exception("Policy not found: " + name);
            }
        }
    }

    public void printPolicies() {
        String sql = """
            SELECT policy_name, policy_value
            FROM system_policies
            ORDER BY policy_name
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- SYSTEM POLICIES ---");
            System.out.printf("%-30s | %-50s%n", "Policy Name", "Value");
            System.out.println("-".repeat(85));
            
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-30s | %-50s%n", 
                    rs.getString("policy_name"), 
                    rs.getString("policy_value")
                );
            }
            if (!found) {
                System.out.println("(No policies configured)");
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error listing policies: " + e.getMessage());
        }
    }

    public ResultSet getAllPolicies() throws Exception {

        String sql = """
            SELECT policy_name, policy_value
            FROM system_policies
            ORDER BY policy_name
        """;

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }
}
