package com.revworkforce;

import com.revworkforce.menu.MainMenu;
import com.revworkforce.util.DBConnection;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("     RevWorkForce HRMS (Console)     ");
        System.out.println("====================================");

        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Database connection established");

            // AUTO-FIX: Reset Passwords for ALL test users
            String newHash = com.revworkforce.util.PasswordUtil.hashPassword("password");
            try (java.sql.PreparedStatement ps = con.prepareStatement(
                    "UPDATE employees SET password_hash = ? WHERE employee_id IN ('ADMIN001','MGR001','EMP001','EMP002')")) {
                ps.setString(1, newHash);
                int count = ps.executeUpdate();
                System.out.println("Passwords reset for " + count + " test users to 'password'");
            }

        } catch (Exception e) {
            System.err.println("FATAL: Database unavailable");
            e.printStackTrace();
            return;
        }

        MainMenu.start();
    }
}
