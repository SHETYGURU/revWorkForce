package com.revworkforce.service;

import com.revworkforce.dao.*;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

/**
 * A comprehensive test to cover "View" methods in Services.
 * These methods primarily print to console using DAOs.
 * We mock DAOs and invoke these methods to ensuring they run without error.
 */
class ServiceViewCoverageTest {

    @Mock
    EmployeeDAO employeeDAO;
    @Mock
    LeaveDAO leaveDAO;
    @Mock
    AttendanceDAO attendanceDAO;
    @Mock
    PerformanceDAO performanceDAO;
    @Mock
    NotificationDAO notificationDAO;
    @Mock
    AnnouncementDAO announcementDAO;
    @Mock
    AuditLogDAO auditLogDAO;
    @Mock
    SystemPolicyDAO systemPolicyDAO;

    // Helper classes
    @Mock
    ResultSet mockResultSet;

    MockedStatic<InputUtil> mockedInputUtil;
    MockedStatic<AuditService> mockedAuditService; // To avoid static logs causing issues

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Common Mock Behavior
        // Create Mock List for refactored methods
        java.util.List<java.util.Map<String, Object>> mockList = new java.util.ArrayList<>();
        java.util.Map<String, Object> row = new java.util.HashMap<>();
        row.put("employee_id", "1");
        row.put("leave_application_id", 1);
        row.put("first_name", "Test");
        mockList.add(row);

        // EmployeeDAO partial updates
        when(employeeDAO.getReportees(anyString())).thenReturn(mockList);
        when(employeeDAO.getProfile(anyString())).thenReturn(mockResultSet);
        when(employeeDAO.searchEmployees(anyString())).thenReturn(mockList);
        when(employeeDAO.getUpcomingBirthdays()).thenReturn(mockResultSet);
        when(employeeDAO.getWorkAnniversaries()).thenReturn(mockResultSet);
        when(employeeDAO.getBirthdaysToday()).thenReturn(mockResultSet);
        when(employeeDAO.getWorkAnniversariesToday()).thenReturn(mockResultSet);

        // LeaveDAO updates
        when(leaveDAO.getLeaveBalances(anyString())).thenReturn(mockList);
        when(leaveDAO.getMyLeaves(anyString())).thenReturn(mockList);
        when(leaveDAO.getTeamLeaveRequests(anyString())).thenReturn(mockList);
        when(leaveDAO.getTeamLeaveCalendar(anyString())).thenReturn(mockList);
        when(leaveDAO.getTeamLeaveBalances(anyString())).thenReturn(mockList);

        when(attendanceDAO.getAttendanceHistory(anyString())).thenReturn(mockResultSet);
        when(attendanceDAO.getTeamAttendanceSummary(anyString())).thenReturn(mockResultSet);

        when(performanceDAO.getMyFeedback(anyString())).thenReturn(mockResultSet); // My Reviews
        when(performanceDAO.getTeamReviews(anyString())).thenReturn(mockResultSet);
        when(performanceDAO.getTeamGoals(anyString())).thenReturn(mockResultSet);
        when(performanceDAO.getGoalCompletionSummary(anyString())).thenReturn(mockResultSet);
        when(performanceDAO.getTeamPerformanceSummary(anyString())).thenReturn(mockResultSet);

        when(announcementDAO.getAllAnnouncements()).thenReturn(mockResultSet);
        // NotificationDAO does not return ResultSet for viewing, it prints directly via
        // printAndMarkRead

        when(mockResultSet.next()).thenReturn(true, false); // Return one row then stop
        when(mockResultSet.getString(anyString())).thenReturn("Test Data");
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getDate(anyString())).thenReturn(java.sql.Date.valueOf("2024-01-01"));

        // Mock Statics
        mockedInputUtil = Mockito.mockStatic(InputUtil.class);
        mockedAuditService = Mockito.mockStatic(AuditService.class);

        // Inject Mocks into Services
        injectMock(ManagerService.class, "employeeDAO", employeeDAO);
        injectMock(ManagerService.class, "leaveDAO", leaveDAO);
        injectMock(ManagerService.class, "attendanceDAO", attendanceDAO);
        injectMock(ManagerService.class, "performanceDAO", performanceDAO);

        injectMock(EmployeeService.class, "employeeDAO", employeeDAO);
        injectMock(EmployeeService.class, "announcementDAO", announcementDAO);

        injectMock(AdminService.class, "employeeDAO", employeeDAO);
        injectMock(AdminService.class, "leaveDAO", leaveDAO);
        injectMock(AdminService.class, "auditLogDAO", auditLogDAO);
        injectMock(AdminService.class, "systemPolicyDAO", systemPolicyDAO);

        // NotificationService has static DAOs
        injectMock(NotificationService.class, "dao", notificationDAO);
        injectMock(NotificationService.class, "employeeDAO", employeeDAO);
    }

    @AfterEach
    void tearDown() {
        if (mockedInputUtil != null)
            mockedInputUtil.close();
        if (mockedAuditService != null)
            mockedAuditService.close();
    }

    private void injectMock(Class<?> targetClass, String fieldName, Object mock) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, mock); // Static fields
        } catch (Exception e) {
            // Ignore if field not found
        }
    }

    @Test
    void testManagerServiceViews() {
        assertDoesNotThrow(() -> ManagerService.viewTeam("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamBasic("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamLeaveRequests("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamLeaveCalendar("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamLeaveBalances("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamAttendance("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamPerformance("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewTeamGoals("MGR1"));
        assertDoesNotThrow(() -> ManagerService.viewGoalCompletionSummary("MGR1"));

        // Special case: viewTeamMemberProfile calls EmployeeService
        assertDoesNotThrow(() -> ManagerService.viewTeamMemberProfile("EMP1"));
    }

    @Test
    void testEmployeeServiceViews() {
        assertDoesNotThrow(() -> EmployeeService.viewProfile("EMP1"));
        assertDoesNotThrow(() -> EmployeeService.viewManagerDetails("EMP1"));
        assertDoesNotThrow(() -> EmployeeService.viewUpcomingBirthdays());
        assertDoesNotThrow(() -> EmployeeService.viewWorkAnniversaries());
        assertDoesNotThrow(() -> EmployeeService.viewAnnouncements());
        // viewNotifications is not in EmployeeService

        // Mock input for directory search
        mockedInputUtil.when(() -> InputUtil.readString(anyString())).thenReturn("keyword");
        assertDoesNotThrow(() -> EmployeeService.employeeDirectory());
    }

    @Test
    void testNotificationServiceViews() {
        assertDoesNotThrow(() -> NotificationService.viewNotifications("EMP1"));
        assertDoesNotThrow(() -> NotificationService.getUnreadCount("EMP1"));
        assertDoesNotThrow(() -> NotificationService.generateDailyNotifications());
    }
}
