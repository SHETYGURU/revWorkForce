package com.revworkforce.service;

import com.revworkforce.dao.AttendanceDAO;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.PerformanceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ManagerServiceTest {

    private EmployeeDAO mockEmpDao;
    private LeaveDAO mockLeaveDao;
    private AttendanceDAO mockAttendanceDao;
    private PerformanceDAO mockPerformanceDao;

    @BeforeEach
    void setUp() throws Exception {
        mockEmpDao = Mockito.mock(EmployeeDAO.class);
        mockLeaveDao = Mockito.mock(LeaveDAO.class);
        mockAttendanceDao = Mockito.mock(AttendanceDAO.class);
        mockPerformanceDao = Mockito.mock(PerformanceDAO.class);

        setPrivateStaticField(ManagerService.class, "employeeDAO", mockEmpDao);
        setPrivateStaticField(ManagerService.class, "leaveDAO", mockLeaveDao);
        setPrivateStaticField(ManagerService.class, "attendanceDAO", mockAttendanceDao);
        setPrivateStaticField(ManagerService.class, "performanceDAO", mockPerformanceDao);

        // Mock dependencies of helper services used by ManagerService
        com.revworkforce.dao.NotificationDAO mockNotifDao = Mockito.mock(com.revworkforce.dao.NotificationDAO.class);
        setPrivateStaticField(NotificationService.class, "dao", mockNotifDao);

        com.revworkforce.dao.AuditLogDAO mockAuditDao = Mockito.mock(com.revworkforce.dao.AuditLogDAO.class);
        setPrivateStaticField(AuditService.class, "dao", mockAuditDao);
    }

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    void testViewTeam_Success() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockEmpDao.getReportees("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false); // One record then end
        when(mockRs.getString("employee_id")).thenReturn("EMP001");
        when(mockRs.getString("first_name")).thenReturn("Alice");

        ManagerService.viewTeam("MGR001");

        verify(mockEmpDao).getReportees("MGR001");
    }

    @Test
    void testIsTeamMember_True() throws Exception {
        when(mockEmpDao.isReportee("MGR001", "EMP001")).thenReturn(true);
        boolean result = ManagerService.isTeamMember("MGR001", "EMP001");
        assertTrue(result);
        verify(mockEmpDao).isReportee("MGR001", "EMP001");
    }

    @Test
    void testIsTeamMember_False() throws Exception {
        when(mockEmpDao.isReportee("MGR001", "EMP002")).thenReturn(false);
        boolean result = ManagerService.isTeamMember("MGR001", "EMP002");
        assertFalse(result);
        verify(mockEmpDao).isReportee("MGR001", "EMP002");
    }

    @Test
    void testProcessLeave_Approve() throws Exception {
        // Mock notification service static call?
        // NotificationService.notifyLeaveUpdate is static. Accessing it might trigger
        // side effects or errors if dependencies missing.
        // Ideally we should mock NotificationService too if possible, but it's not a
        // field in ManagerService.
        // It resides in ManagerService method as
        // `com.revworkforce.service.NotificationService.notifyLeaveUpdate`.
        // We might need to mockStatic NotificationService if we want to be safe, or
        // assume it's safe if it just does DB calls we can't easily mock w/o more
        // injection.
        // However, looking at ManagerService.java, it calls
        // `leaveDAO.getEmployeeIdForLeave`.

        when(mockLeaveDao.getEmployeeIdForLeave(100)).thenReturn("EMP001");

        // AuditService.log() is also static. static mocks are tricky if multiple per
        // test.
        // Let's rely on standard execution. If those services crash because of DB, we
        // might need to mock them.
        // But AuditService likely uses AuditLogDAO. If AuditLogDAO is not injected, it
        // might be hard.
        // Let's try running it. If it fails, I'll add MockedStatic for AuditService and
        // NotificationService.

        // For now, I'll just skip detailed static mocking of helpers to keep it simple,
        // assuming they are robust enough or I'll catch the error.
        // ACTUALLY, to be safe, I should probably try to mock AuditService and
        // NotificationService if I want pure unit test.

        // Let's just mock what we can easily control first.

        ManagerService.processLeave("MGR001", 100, "APPROVED", "Good to go");

        verify(mockLeaveDao).updateLeaveStatus(100, "MGR001", "APPROVED", "Good to go");
    }

    @Test
    void testViewTeamLeaveRequests() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockLeaveDao.getTeamLeaveRequests("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getInt("leave_application_id")).thenReturn(101);
        when(mockRs.getString("employee_id")).thenReturn("EMP002");
        when(mockRs.getString("first_name")).thenReturn("Bob");
        when(mockRs.getDate("start_date")).thenReturn(java.sql.Date.valueOf("2024-01-01"));
        when(mockRs.getDate("end_date")).thenReturn(java.sql.Date.valueOf("2024-01-02"));
        when(mockRs.getString("status")).thenReturn("PENDING");

        ManagerService.viewTeamLeaveRequests("MGR001");

        verify(mockLeaveDao).getTeamLeaveRequests("MGR001");
    }

    @Test
    void testSubmitPerformanceReview() throws Exception {
        // Mock team member check
        when(mockPerformanceDao.getEmployeeIdForReview(50)).thenReturn("EMP003");
        when(mockEmpDao.isReportee("MGR001", "EMP003")).thenReturn(true);

        ManagerService.submitPerformanceReview("MGR001", 50, "Great Job", 5);

        verify(mockPerformanceDao).submitManagerFeedback(50, "Great Job", 5);
        // We implicitly verified NotificationService didn't crash because we mocked its
        // DAO
    }

    @Test
    void testRevokeApprovedLeave() throws Exception {
        when(mockLeaveDao.getEmployeeIdForLeave(200)).thenReturn("EMP004");

        ManagerService.revokeApprovedLeave("MGR001", 200, "Project Critical");

        verify(mockLeaveDao).updateLeaveStatus(200, "MGR001", "REVOKED", "Project Critical");
    }

    @Test
    void testViewTeam_Failure() throws Exception {
        when(mockEmpDao.getReportees("MGR001")).thenThrow(new RuntimeException("DB Error"));

        ManagerService.viewTeam("MGR001");

        verify(mockEmpDao).getReportees("MGR001");
    }

    @Test
    void testProcessLeave_Failure() throws Exception {
        when(mockLeaveDao.getEmployeeIdForLeave(100)).thenReturn("EMP001");
        doThrow(new RuntimeException("DB Error")).when(mockLeaveDao).updateLeaveStatus(anyInt(), anyString(),
                anyString(), anyString());

        ManagerService.processLeave("MGR001", 100, "APPROVED", "Good");

        verify(mockLeaveDao).updateLeaveStatus(100, "MGR001", "APPROVED", "Good");
    }

    @Test
    void testViewTeamBasic() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockEmpDao.getReportees("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getString("employee_id")).thenReturn("EMP001");
        when(mockRs.getString("first_name")).thenReturn("Bob");

        ManagerService.viewTeamBasic("MGR001");

        verify(mockEmpDao).getReportees("MGR001");
    }

    @Test
    void testViewTeamAttendance() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockAttendanceDao.getTeamAttendanceSummary("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewTeamAttendance("MGR001");

        verify(mockAttendanceDao).getTeamAttendanceSummary("MGR001");
    }

    @Test
    void testViewTeamPerformance() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockPerformanceDao.getTeamReviews("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewTeamPerformance("MGR001");

        verify(mockPerformanceDao).getTeamReviews("MGR001");
    }

    @Test
    void testViewTeamGoals() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockPerformanceDao.getTeamGoals("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewTeamGoals("MGR001");

        verify(mockPerformanceDao).getTeamGoals("MGR001");
    }

    @Test
    void testViewGoalCompletionSummary() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockPerformanceDao.getGoalCompletionSummary("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewGoalCompletionSummary("MGR001");

        verify(mockPerformanceDao).getGoalCompletionSummary("MGR001");
    }

    @Test
    void testViewTeamLeaveCalendar() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockLeaveDao.getTeamLeaveCalendar("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewTeamLeaveCalendar("MGR001");

        verify(mockLeaveDao).getTeamLeaveCalendar("MGR001");
    }

    @Test
    void testViewTeamLeaveBalances() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockLeaveDao.getTeamLeaveBalances("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewTeamLeaveBalances("MGR001");

        verify(mockLeaveDao).getTeamLeaveBalances("MGR001");
    }

    @Test
    void testViewEmployeeLeaveCalendar() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockLeaveDao.getApprovedLeavesForEmployee("EMP001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

        ManagerService.viewEmployeeLeaveCalendar("EMP001");

        verify(mockLeaveDao).getApprovedLeavesForEmployee("EMP001");
    }

    @Test
    void testIsPendingLeave_NotPending() throws Exception {
        ResultSet mockRs = mock(ResultSet.class);
        when(mockLeaveDao.getTeamLeaveRequests("MGR001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getInt("leave_application_id")).thenReturn(999);

        boolean result = ManagerService.isPendingLeave("MGR001", 100);

        assertFalse(result);
    }

    @Test
    void testIsTeamMember_Error() throws Exception {
        when(mockEmpDao.isReportee("MGR001", "EMP999")).thenThrow(new RuntimeException("DB Error"));

        boolean result = ManagerService.isTeamMember("MGR001", "EMP999");

        assertFalse(result);
    }
}
