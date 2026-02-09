package com.revworkforce.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Timestamp;

class AdditionalModelTest {

    @Test
    void testAnnouncement() {
        Announcement ann = new Announcement();
        ann.setAnnouncementId(1);
        ann.setTitle("Meeting");
        ann.setContent("All hands");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ann.setPostedDate(now);

        assertEquals(1, ann.getAnnouncementId());
        assertEquals("Meeting", ann.getTitle());
        assertEquals("All hands", ann.getContent());
        assertEquals(now, ann.getPostedDate());
    }

    @Test
    void testAuditLog() {
        AuditLog log = new AuditLog();
        log.setLogId(10);
        log.setEmployeeId("EMP1");
        log.setAction("UPDATE");
        log.setTableName("users");
        log.setRecordId("55");
        log.setNewValue("active");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        log.setCreatedAt(now);

        assertEquals(10, log.getLogId());
        assertEquals("EMP1", log.getEmployeeId());
        assertEquals("UPDATE", log.getAction());
        assertEquals("users", log.getTableName());
        assertEquals("55", log.getRecordId());
        assertEquals("active", log.getNewValue());
        assertEquals(now, log.getCreatedAt());
    }

    @Test
    void testGoal() {
        Goal goal = new Goal();
        goal.setGoalId(1);
        goal.setEmployeeId("EMP001");
        goal.setCycleId(2024);
        goal.setGoalDescription("Learn Java"); // Fixed
        goal.setStatus("Pending");
        goal.setManagerComments("Good");

        assertEquals(1, goal.getGoalId());
        assertEquals("EMP001", goal.getEmployeeId());
        assertEquals(2024, goal.getCycleId());
        assertEquals("Learn Java", goal.getGoalDescription()); // Fixed
        assertEquals("Pending", goal.getStatus());
        assertEquals("Good", goal.getManagerComments());
    }

    @Test
    void testHoliday() {
        Holiday h = new Holiday();
        h.setHolidayId(5);
        h.setHolidayName("New Year");
        Date date = Date.valueOf("2024-01-01");
        h.setHolidayDate(date);
        h.setYear(2024);

        assertEquals(5, h.getHolidayId());
        assertEquals("New Year", h.getHolidayName());
        assertEquals(date, h.getHolidayDate());
        assertEquals(2024, h.getYear());
    }

    @Test
    void testLeaveApplication() {
        LeaveApplication la = new LeaveApplication();
        la.setLeaveApplicationId(100);
        la.setEmployeeId("EMP99");
        la.setLeaveTypeId(2);
        Date start = Date.valueOf("2024-05-01");
        Date end = Date.valueOf("2024-05-05");
        la.setStartDate(start);
        la.setEndDate(end);
        la.setReason("Sick");
        la.setStatus("APPROVED");
        Timestamp applied = new Timestamp(System.currentTimeMillis());
        la.setAppliedDate(applied);
        la.setReviewedBy("MGR1");
        la.setReviewedDate(applied);
        la.setManagerComments("Get well soon");

        assertEquals(100, la.getLeaveApplicationId());
        assertEquals("EMP99", la.getEmployeeId());
        assertEquals(2, la.getLeaveTypeId());
        assertEquals(start, la.getStartDate());
        assertEquals(end, la.getEndDate());
        assertEquals("Sick", la.getReason());
        assertEquals("APPROVED", la.getStatus());
        assertEquals(applied, la.getAppliedDate());
        assertEquals("MGR1", la.getReviewedBy());
        assertEquals(applied, la.getReviewedDate());
        assertEquals("Get well soon", la.getManagerComments());
    }

    @Test
    void testNotification() {
        Notification n = new Notification();
        n.setNotificationId(99);
        n.setEmployeeId("EMP1");
        n.setNotificationType("INFO");
        n.setMessage("Welcome");
        n.setRead(true);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        n.setCreatedAt(now);

        assertEquals(99, n.getNotificationId());
        assertEquals("EMP1", n.getEmployeeId());
        assertEquals("INFO", n.getNotificationType());
        assertEquals("Welcome", n.getMessage());
        assertTrue(n.isRead());
        assertEquals(now, n.getCreatedAt());
    }

    @Test
    void testPerformanceCycle() {
        PerformanceCycle pc = new PerformanceCycle();
        pc.setCycleId(2023);
        pc.setYear(2023); // Fixed, was setCycleName
        Date start = Date.valueOf("2023-01-01");
        Date end = Date.valueOf("2023-12-31");
        pc.setStartDate(start);
        pc.setEndDate(end);
        pc.setStatus("ACTIVE"); // Fixed, was setActive

        assertEquals(2023, pc.getCycleId());
        assertEquals(2023, pc.getYear()); // Fixed
        assertEquals(start, pc.getStartDate());
        assertEquals(end, pc.getEndDate());
        assertEquals("ACTIVE", pc.getStatus()); // Fixed
    }

    @Test
    void testSystemPolicy() {
        SystemPolicy sp = new SystemPolicy();
        sp.setPolicyId(1);
        sp.setPolicyName("TIMEOUT");
        sp.setPolicyValue("30");
        // Removed setUpdatedAt/getUpdatedAt as they don't exist

        assertEquals(1, sp.getPolicyId());
        assertEquals("TIMEOUT", sp.getPolicyName());
        assertEquals("30", sp.getPolicyValue());
    }
}
