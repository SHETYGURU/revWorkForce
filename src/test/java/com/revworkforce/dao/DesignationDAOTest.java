package com.revworkforce.dao;

import com.revworkforce.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DesignationDAOTest {

    private DesignationDAO designationDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        designationDAO = new DesignationDAO();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testAddDesignation() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            designationDAO.addDesignation("Developer");

            verify(mockPreparedStatement).setString(1, "Developer");
            verify(mockPreparedStatement).executeUpdate();
        }
    }

    @Test
    void testIsDesignationMatchRole_Manager() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("designation_name")).thenReturn("Project Manager");

            boolean match = designationDAO.isDesignationMatchRole("1", true);
            assertTrue(match);
        }
    }

    @Test
    void testIsDesignationMatchRole_NotManager() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("designation_name")).thenReturn("Software Engineer");

            boolean match = designationDAO.isDesignationMatchRole("1", false);
            assertTrue(match);

            boolean mismatch = designationDAO.isDesignationMatchRole("1", true);
            assertFalse(mismatch);
        }
    }
}
