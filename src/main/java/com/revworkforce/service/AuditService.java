/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.service;

import com.revworkforce.dao.AuditLogDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class for logging system audits.
 * Records interactions such as profile updates, leave processing, and security
 * events.
 * 
 * @author Gururaj Shetty
 */
public class AuditService {

    private static final Logger logger = LogManager.getLogger(AuditService.class);

    private static AuditLogDAO dao = new AuditLogDAO();

    /**
     * Logs an action in the audit_logs table.
     *
     * @param employeeId  ID of the employee performing the action.
     * @param action      Action type (e.g., UPDATE, INSERT, DELETE).
     * @param table       Table affected.
     * @param recordId    ID of the affected record.
     * @param description Brief description of the change.
     */
    public static void log(String employeeId, String action, String table, String recordId, String description) {
        // Delegate to the 6-arg method with null (it will be ignored anyway)
        log(employeeId, action, table, null, recordId, description);
    }

    /**
     * Logs an action with optional column-level detail.
     * 
     * @param employeeId  ID of the employee performing the action.
     * @param action      Action type (e.g., UPDATE, INSERT).
     * @param table       Table affected.
     * @param columnName  Specific column changed (optional, currently unused by
     *                    DAO).
     * @param recordId    ID of the affected record.
     * @param description Brief description of the change.
     */
    public static void log(String employeeId, String action, String table, String columnName, String recordId,
            String description) {
        try {
            // IGNORE columnName as requested primarily
            dao.log(employeeId, action, table, recordId, description);
        } catch (Exception e) {
            // We do not wan't to throw exception here to avoid interrupting the main flow
            // just because logging failed.
            logger.error("Audit Log Failed: " + e.getMessage(), e);
        }
    }
}
