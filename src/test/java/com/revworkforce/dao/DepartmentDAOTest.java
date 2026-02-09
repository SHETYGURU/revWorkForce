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

class DepartmentDAOTest {

    private DepartmentDAO departmentDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        departmentDAO = new DepartmentDAO();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testAddDepartment() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            departmentDAO.addDepartment("HR");

            verify(mockPreparedStatement).setString(1, "HR");
            verify(mockPreparedStatement).executeUpdate();
        }
    }

    @Test
    void testGetAllDepartments() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            ResultSet rs = departmentDAO.getAllDepartments();
            assertNotNull(rs);
            verify(mockPreparedStatement).executeQuery();
        }
    }

    @Test
    void testUpdateDepartment() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            departmentDAO.updateDepartment("1", "Human Resources");

            verify(mockPreparedStatement).setString(1, "Human Resources");
            verify(mockPreparedStatement).setString(2, "1");
            verify(mockPreparedStatement).executeUpdate();
        }
    }

    @Test
    void testDeleteDepartment() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            departmentDAO.deleteDepartment("1");

            verify(mockPreparedStatement).setString(1, "1");
            verify(mockPreparedStatement).executeUpdate();
        }
    }

    @Test
    void testIsDepartmentIdExists() throws Exception {
        try (MockedStatic<DBConnection> mockedDB = Mockito.mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            boolean exists = departmentDAO.isDepartmentIdExists("1");

            assertTrue(exists);
            verify(mockPreparedStatement).setString(1, "1");
        }
    }
}
