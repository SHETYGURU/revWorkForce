package com.revworkforce.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Timestamp;

class ModelTest {

    @Test
    void testAttendance() {
        Attendance att = new Attendance();
        att.setAttendanceId(1);
        att.setEmployeeId("EMP001");
        att.setAttendanceDate(Date.valueOf("2023-01-01"));
        att.setCheckInTime(Timestamp.valueOf("2023-01-01 09:00:00"));
        att.setCheckOutTime(Timestamp.valueOf("2023-01-01 17:00:00"));
        att.setStatus("Present");

        assertEquals(1, att.getAttendanceId());
        assertEquals("EMP001", att.getEmployeeId());
        assertEquals(Date.valueOf("2023-01-01"), att.getAttendanceDate());
        assertEquals(Timestamp.valueOf("2023-01-01 09:00:00"), att.getCheckInTime());
        assertEquals(Timestamp.valueOf("2023-01-01 17:00:00"), att.getCheckOutTime());
        assertEquals("Present", att.getStatus());
    }

    @Test
    void testDepartment() {
        Department dept = new Department();
        dept.setDepartmentId(101);
        dept.setDepartmentName("Engineering");

        assertEquals(101, dept.getDepartmentId());
        assertEquals("Engineering", dept.getDepartmentName());
    }

    @Test
    void testDesignation() {
        Designation desig = new Designation();
        desig.setDesignationId(201);
        desig.setDesignationName("Developer");

        assertEquals(201, desig.getDesignationId());
        assertEquals("Developer", desig.getDesignationName());
    }

    @Test
    void testEmployee() {
        Employee emp = new Employee();
        emp.setEmployeeId("EMP001");
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setEmail("john@example.com");
        emp.setPhone("1234567890");
        emp.setAddress("123 Main St");
        emp.setEmergencyContact("Jane");

        Date dob = Date.valueOf("1990-01-01");
        emp.setDateOfBirth(dob);

        emp.setDepartmentId(1);
        emp.setDesignationId(2);
        emp.setManagerId("MGR001");

        Date joinDate = Date.valueOf("2020-01-01");
        emp.setJoiningDate(joinDate);

        emp.setSalary(50000.0);
        emp.setPasswordHash("hash123");

        // Corrected methods based on Employee.java
        emp.setActive(true);
        emp.setFailedLoginAttempts(0);
        emp.setAccountLocked(false);

        Timestamp lastLogin = new Timestamp(System.currentTimeMillis());
        emp.setLastLogin(lastLogin);

        assertEquals("EMP001", emp.getEmployeeId());
        assertEquals("John", emp.getFirstName());
        assertEquals("Doe", emp.getLastName());
        assertEquals("john@example.com", emp.getEmail());
        assertEquals("1234567890", emp.getPhone());
        assertEquals("123 Main St", emp.getAddress());
        assertEquals("Jane", emp.getEmergencyContact());
        assertEquals(dob, emp.getDateOfBirth());
        assertEquals(1, emp.getDepartmentId());
        assertEquals(2, emp.getDesignationId());
        assertEquals("MGR001", emp.getManagerId());
        assertEquals(joinDate, emp.getJoiningDate());
        assertEquals(50000.0, emp.getSalary());
        assertEquals("hash123", emp.getPasswordHash());

        // Corrected assertions
        assertTrue(emp.isActive());
        assertEquals(0, emp.getFailedLoginAttempts());
        assertFalse(emp.isAccountLocked());
        assertEquals(lastLogin, emp.getLastLogin());
    }

    @Test
    void testLeaveBalance() {
        LeaveBalance lb = new LeaveBalance();
        lb.setEmployeeId("EMP001");
        lb.setLeaveTypeId(1);

        // Corrected methods based on LeaveBalance.java
        lb.setUsedLeaves(5);
        lb.setTotalAllocated(12);

        assertEquals("EMP001", lb.getEmployeeId());
        assertEquals(1, lb.getLeaveTypeId());
        assertEquals(5, lb.getUsedLeaves());
        assertEquals(12, lb.getTotalAllocated());
    }

    @Test
    void testLeaveType() {
        LeaveType lt = new LeaveType();
        lt.setLeaveTypeId(1);
        lt.setLeaveTypeName("Casual");

        assertEquals(1, lt.getLeaveTypeId());
        assertEquals("Casual", lt.getLeaveTypeName());
    }

    @Test
    void testPerformanceReview() {
        PerformanceReview pr = new PerformanceReview();
        pr.setReviewId(1);
        pr.setEmployeeId("EMP001");

        // Corrected methods based on PerformanceReview.java
        pr.setReviewedBy("MGR001"); // was setReviewerId
        pr.setCycleId(1);
        pr.setManagerRating(4.5); // was setRating
        pr.setManagerFeedback("Good job"); // was setComments

        // Note: isGoalsMet/setGoalsMet does not appear in the PerformanceReview.java
        // file I read.
        // It has keyDeliverables, majorAccomplishments, areasOfImprovement,
        // selfAssessmentRating, status...
        // I will remove isGoalsMet test as it's not in the model.

        pr.setKeyDeliverables("Delivered X");

        Date date = Date.valueOf("2023-06-01");
        Timestamp ts = new Timestamp(date.getTime());
        pr.setReviewedDate(ts);

        assertEquals(1, pr.getReviewId());
        assertEquals("EMP001", pr.getEmployeeId());
        assertEquals("MGR001", pr.getReviewedBy());
        assertEquals(1, pr.getCycleId());
        assertEquals(4.5, pr.getManagerRating());
        assertEquals("Good job", pr.getManagerFeedback());
        assertEquals("Delivered X", pr.getKeyDeliverables());
        assertEquals(ts, pr.getReviewedDate());
    }

    @Test
    void testRole() {
        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("Admin");

        assertEquals(1, role.getRoleId());
        assertEquals("Admin", role.getRoleName());
    }

}
