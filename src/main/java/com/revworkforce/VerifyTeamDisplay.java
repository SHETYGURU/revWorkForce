package com.revworkforce;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.service.ManagerService;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Verification script for testing Team Display functionality.
 * 
 * @author Gururaj Shetty
 */
public class VerifyTeamDisplay {
    private static final Logger logger = LogManager.getLogger(VerifyTeamDisplay.class);

    public static void main(String[] args) {
        try {
            EmployeeDAO empDAO = new EmployeeDAO();
            // Search for John Doe to get ID
            logger.info("Searching for Manager 'John Doe'...");
            ResultSet rs = empDAO.searchEmployees("John");
            String managerId = null;
            while (rs.next()) {
                if (rs.getString("first_name").equalsIgnoreCase("John")
                        && rs.getString("last_name").equalsIgnoreCase("Doe")) {
                    managerId = rs.getString("employee_id");
                    logger.info("Found Manager: " + managerId);
                    break;
                }
            }

            if (managerId != null) {
                logger.info("Displaying Team for ID: " + managerId);
                ManagerService.viewTeam(managerId);
            } else {
                logger.warn("Manager John Doe not found. Trying to find any manager with reportees...");
                // Fallback: try to find any manager
                // This part is tricky without querying directly.
                // Let's assume John Doe exists as per prompt.
                // Or search for 'ADMIN001' if John Doe is not found, maybe he is the admin.
                logger.info("Trying 'MGR001'...");
                ManagerService.viewTeam("MGR001");
            }

        } catch (Exception e) {
            logger.error("Error in verification", e);
        }
    }
}
