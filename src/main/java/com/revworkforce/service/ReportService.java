package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;

import java.sql.ResultSet;

public class ReportService {

    private static final PerformanceDAO dao = new PerformanceDAO();

    public static void teamPerformanceSummary(String managerId) {

        try {
            ResultSet rs = dao.getTeamPerformanceSummary(managerId);

            System.out.println("\n--- TEAM PERFORMANCE SUMMARY ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") +
                                " | Avg Rating: " + rs.getDouble("avg_rating")
                );
            }
        } catch (Exception e) {
            System.err.println("Unable to generate performance report");
        }
    }
}
