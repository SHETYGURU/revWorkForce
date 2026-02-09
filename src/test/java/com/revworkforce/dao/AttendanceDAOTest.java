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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AttendanceDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private AttendanceDAO attendanceDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        attendanceDAO = new AttendanceDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testHasCheckedIn() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        boolean result = attendanceDAO.hasCheckedIn("EMP001", Date.valueOf("2024-01-01"));

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "EMP001");
        verify(mockPreparedStatement).setDate(eq(2), any(Date.class));
    }

    @Test
    void testCheckIn() throws Exception {
        attendanceDAO.checkIn("EMP001");
        verify(mockPreparedStatement).setString(1, "EMP001");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testHasCheckedOut() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        boolean result = attendanceDAO.hasCheckedOut("EMP001", Date.valueOf("2024-01-01"));

        assertFalse(result);
        verify(mockPreparedStatement).setString(1, "EMP001");
    }

    @Test
    void testCheckOut() throws Exception {
        attendanceDAO.checkOut("EMP001");
        verify(mockPreparedStatement).setString(1, "EMP001");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetAttendanceHistory() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        attendanceDAO.getAttendanceHistory("EMP001");
        verify(mockPreparedStatement).setString(1, "EMP001");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetTeamAttendanceSummary() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        attendanceDAO.getTeamAttendanceSummary("MGR001");
        verify(mockPreparedStatement).setString(1, "MGR001");
        verify(mockPreparedStatement).executeQuery();
    }
}
