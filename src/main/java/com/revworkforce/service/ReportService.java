/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

/**
 * Service class for generating reports.
 * 
 * @author Gururaj Shetty
 */
public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);

    private static PerformanceDAO dao = new PerformanceDAO();

    /**
     * Generates a performance summary for a specific team.
     * calculates average ratings and outlines key metrics.
     * 
     * @param managerId The Manager's Employee ID.
     */
    public static void teamPerformanceSummary(String managerId) {

        try {
            ResultSet rs = dao.getTeamPerformanceSummary(managerId);

            System.out.println("\n--- TEAM PERFORMANCE SUMMARY ---");
            while (rs.next()) {
                System.out.println(
                        rs.getString("employee_id") +
                                " | Avg Rating: " + rs.getDouble("avg_rating"));
            }
        } catch (Exception e) {
            logger.error("Unable to generate performance report", e);
        }
    }
}
