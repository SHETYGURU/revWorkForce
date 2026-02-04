package com.revworkforce.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelTest {

    @Test
    public void testEmployeeModel() {
        Employee emp = new Employee();
        emp.setEmployeeId("E001");
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setEmail("john.doe@example.com");

        Assertions.assertEquals("E001", emp.getEmployeeId());
        Assertions.assertEquals("John", emp.getFirstName());
        Assertions.assertEquals("Doe", emp.getLastName());
        Assertions.assertEquals("john.doe@example.com", emp.getEmail());
    }

    @Test
    public void testAuditLogModel() {
        AuditLog log = new AuditLog();
        log.setLogId(1);
        log.setAction("LOGIN");
        log.setEmployeeId("E001");

        Assertions.assertEquals(1, log.getLogId());
        Assertions.assertEquals("LOGIN", log.getAction());
        Assertions.assertEquals("E001", log.getEmployeeId());
    }
}
