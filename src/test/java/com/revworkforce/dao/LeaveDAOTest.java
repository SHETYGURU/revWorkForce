package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LeaveDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private LeaveDAO leaveDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        leaveDAO = new LeaveDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testApplyLeave() throws Exception {
        Date start = Date.valueOf("2024-01-01");
        Date end = Date.valueOf("2024-01-05");
        leaveDAO.applyLeave("EMP1", 1, start, end, "Medical");

        verify(mockPreparedStatement).setString(1, "EMP1");
        verify(mockPreparedStatement).setInt(2, 1);
        verify(mockPreparedStatement).setDate(3, start);
        verify(mockPreparedStatement).setDate(4, end);
        verify(mockPreparedStatement).setString(5, "Medical");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateLeaveStatus() throws Exception {
        leaveDAO.updateLeaveStatus(100, "MGR1", "APPROVED", "Okay");

        verify(mockPreparedStatement).setString(1, "APPROVED");
        verify(mockPreparedStatement).setString(2, "Okay");
        verify(mockPreparedStatement).setString(3, "MGR1");
        verify(mockPreparedStatement).setInt(4, 100);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetLeaveBalances() throws Exception {
        leaveDAO.getLeaveBalances("EMP1");
        verify(mockPreparedStatement).setString(1, "EMP1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testAssignLeaveQuota_Update() throws Exception {
        // Mock update returning 1 row (success)
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        leaveDAO.assignLeaveQuota("EMP1", 1, 2024, 12);

        // Should execute update but NOT insert
        verify(mockPreparedStatement).setInt(1, 12);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setInt(5, 2024);
    }

    @Test
    void testAssignLeaveQuota_Insert() throws Exception {
        // First call (UPDATE) returns 0
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        leaveDAO.assignLeaveQuota("EMP1", 1, 2024, 12);

        // Verify update was attempted then insert
        verify(mockPreparedStatement, times(2)).executeUpdate();
    }

    @Test
    void testAdjustLeaveBalance() throws Exception {
        leaveDAO.adjustLeaveBalance(1, 10, 5);
        verify(mockPreparedStatement).setInt(1, 10); // total
        verify(mockPreparedStatement).setInt(2, 5); // used
        verify(mockPreparedStatement).setInt(3, 5); // available (10-5)
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetEmployeeIdForLeave() throws Exception {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("employee_id")).thenReturn("EMP99");

        String empId = leaveDAO.getEmployeeIdForLeave(500);

        assertEquals("EMP99", empId);
    }

    @Test
    void testGetEmployeeIdForLeave_NotFound() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        String empId = leaveDAO.getEmployeeIdForLeave(500);

        assertNull(empId);
    }

    @Test
    void testGetTeamLeaveRequests() throws Exception {
        leaveDAO.getTeamLeaveRequests("MGR1");
        verify(mockPreparedStatement).setString(1, "MGR1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetMyLeaves() throws Exception {
        leaveDAO.getMyLeaves("EMP1");
        verify(mockPreparedStatement).setString(1, "EMP1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetLeaveStatistics() throws Exception {
        leaveDAO.getLeaveStatistics();
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetDepartmentLeaveReport() throws Exception {
        leaveDAO.getDepartmentLeaveReport();
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetEmployeeLeaveReport() throws Exception {
        leaveDAO.getEmployeeLeaveReport("EMP1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testCancelLeave() throws Exception {
        leaveDAO.cancelLeave(100, "EMP1");
        verify(mockPreparedStatement).setInt(1, 100);
        verify(mockPreparedStatement).setString(2, "EMP1");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetTeamLeaveCalendar() throws Exception {
        leaveDAO.getTeamLeaveCalendar("MGR1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetApprovedLeavesForEmployee() throws Exception {
        leaveDAO.getApprovedLeavesForEmployee("EMP1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetTeamLeaveBalances() throws Exception {
        leaveDAO.getTeamLeaveBalances("MGR1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testRevokeApprovedLeave() throws Exception {
        leaveDAO.revokeApprovedLeave(100);
        verify(mockPreparedStatement).setInt(1, 100);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddHoliday() throws Exception {
        Date date = Date.valueOf("2024-12-25");
        leaveDAO.addHoliday("Xmas", date);
        verify(mockPreparedStatement).setString(1, "Xmas");
        verify(mockPreparedStatement).setDate(2, date);
        verify(mockPreparedStatement).executeUpdate();
    }
}
