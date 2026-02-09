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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuditLogDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private AuditLogDAO auditLogDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        auditLogDAO = new AuditLogDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testLog() throws Exception {
        auditLogDAO.log("EMP001", "UPDATE", "USERS", "1", "Active");

        verify(mockPreparedStatement).setString(1, "EMP001");
        verify(mockPreparedStatement).setString(2, "UPDATE");
        verify(mockPreparedStatement).setString(3, "USERS");
        verify(mockPreparedStatement).setString(4, "1");
        verify(mockPreparedStatement).setString(5, "Active");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testPrintAuditLogs() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(new java.sql.Timestamp(System.currentTimeMillis()));

        auditLogDAO.printAuditLogs();

        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, atLeastOnce()).getString("action");
    }
}
