package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;

import java.sql.*;

public class DepartmentDAO {

    public void addDepartment(String name) throws Exception {

        String sql = "INSERT INTO departments (department_name) VALUES (?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public ResultSet getAllDepartments() throws Exception {

        String sql = "SELECT department_id, department_name FROM departments ORDER BY department_name";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        return ps.executeQuery();
    }
    public void updateDepartment(String id, String name) throws Exception {
        String sql = "UPDATE departments SET department_name = ? WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public void deleteDepartment(String id) throws Exception {
        String sql = "DELETE FROM departments WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    public boolean isDepartmentIdExists(String id) throws Exception {
        String sql = "SELECT 1 FROM departments WHERE department_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void printDepartments() {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_id, department_name FROM departments ORDER BY department_id")) {
             
            System.out.println("\nAvailable Departments:");
            System.out.println("ID   | Name");
            System.out.println("---- | --------------------");
            while (rs.next()) {
                System.out.printf("%-4d | %s%n", rs.getInt("department_id"), rs.getString("department_name"));
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error listing departments: " + e.getMessage());
        }
    }
}
