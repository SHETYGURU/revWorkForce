/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * DAO for Designation management.
 * 
 * @author Gururaj Shetty
 */
public class DesignationDAO {

    private static final Logger logger = LogManager.getLogger(DesignationDAO.class);

    public void addDesignation(String name) throws Exception {

        String sql = "INSERT INTO designations (designation_name) VALUES (?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public ResultSet getAllDesignations() throws Exception {

        String sql = "SELECT designation_id, designation_name FROM designations ORDER BY designation_name";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }

    public void updateDesignation(String id, String name) throws Exception {
        String sql = "UPDATE designations SET designation_name = ? WHERE designation_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public void deleteDesignation(String id) throws Exception {
        String sql = "DELETE FROM designations WHERE designation_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    public boolean isDesignationIdExists(String id) throws Exception {
        String sql = "SELECT 1 FROM designations WHERE designation_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void printDesignations() {
        printDesignations(false); // Default (legacy support, though likely unused now)
    }

    /**
     * Prints the list of available designations to the console.
     * Can filter by role type (Manager vs non-Manager).
     * 
     * @param showManagersOnly true to show only Manager titles, false for others.
     */
    public void printDesignations(boolean showManagersOnly) {
        try (Connection con = DBConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT designation_id, designation_name FROM designations ORDER BY designation_id")) {

            System.out.println(
                    "\nAvailable Designations (" + (showManagersOnly ? "Manager Role" : "Employee Role") + "):");
            System.out.println("ID   | Name");
            System.out.println("---- | --------------------");

            boolean found = false;
            while (rs.next()) {
                String name = rs.getString("designation_name");
                boolean isManagerTitle = name.toLowerCase().contains("manager");

                if (showManagersOnly == isManagerTitle) {
                    System.out.printf("%-4d | %s%n", rs.getInt("designation_id"), name);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("(No designations found for this role category)");
            }
            System.out.println();
        } catch (Exception e) {
            logger.error("Error listing designations: " + e.getMessage(), e);
        }
    }

    public boolean isDesignationMatchRole(String id, boolean mustBeManager) throws Exception {
        String sql = "SELECT designation_name FROM designations WHERE designation_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("designation_name");
                    boolean isManagerTitle = name.toLowerCase().contains("manager");
                    return isManagerTitle == mustBeManager;
                }
                return false; // ID doesn't exist
            }
        }
    }
}
