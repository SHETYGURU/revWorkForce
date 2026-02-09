package com.revworkforce.dao;

import com.revworkforce.model.Employee;
import com.revworkforce.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmployeeDAOTest {

    private EmployeeDAO employeeDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDB;

    @BeforeEach
    void setUp() {
        employeeDAO = new EmployeeDAO();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedDB = Mockito.mockStatic(DBConnection.class);
        mockedDB.when(DBConnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (mockedDB != null) {
            mockedDB.close();
        }
    }

    private void setupMockQuery() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    private void setupMockUpdate() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    }

    @Test
    void testGetEmployeeById() throws Exception {
        setupMockQuery();
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("employee_id")).thenReturn("EMP001");
        when(mockResultSet.getString("first_name")).thenReturn("John");

        Employee emp = employeeDAO.getEmployeeById("EMP001");

        assertNotNull(emp);
        assertEquals("EMP001", emp.getEmployeeId());
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        setupMockQuery();
        when(mockResultSet.next()).thenReturn(false);

        Employee emp = employeeDAO.getEmployeeById("EMP001");

        assertNull(emp);
    }

    @Test
    void testInsertEmployee() throws Exception {
        setupMockUpdate();

        Employee emp = new Employee();
        emp.setEmployeeId("EMP001");
        emp.setFirstName("John");
        // ... (other fields unnecessary for verification call)
        // Testing that setDate handles nulls correctly or populated
        emp.setDateOfBirth(Date.valueOf("1990-01-01"));
        emp.setJoiningDate(Date.valueOf("2020-01-01"));
        emp.setSalary(50000.0);

        employeeDAO.insertEmployee(emp);

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testInsertEmployee_WithNulls() throws Exception {
        setupMockUpdate();
        Employee emp = new Employee();
        emp.setEmployeeId("EMP001");
        // Leave wrapper types null (departmentId, designationId)

        employeeDAO.insertEmployee(emp);

        // Verify setNull was called
        verify(mockPreparedStatement, atLeastOnce()).setNull(anyInt(), anyInt());
    }

    @Test
    void testIsEmailExists() throws Exception {
        setupMockQuery();
        when(mockResultSet.next()).thenReturn(true);
        assertTrue(employeeDAO.isEmailExists("test@example.com"));
    }

    @Test
    void testIsPhoneExists() throws Exception {
        setupMockQuery();
        when(mockResultSet.next()).thenReturn(true);
        assertTrue(employeeDAO.isPhoneExists("1234567890"));
    }

    @Test
    void testGetNextId() throws Exception {
        setupMockQuery();

        // Case 1: No previous ID
        when(mockResultSet.next()).thenReturn(false);
        assertEquals("EMP001", employeeDAO.getNextId("EMP"));

        // Case 2: Max ID exists
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("EMP005");
        assertEquals("EMP006", employeeDAO.getNextId("EMP"));

        // Case 3: Malformed ID
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("EMPXXX");
        // Fallback or retry? Logic says "EMP001" if fallback exception handled
        assertEquals("EMP001", employeeDAO.getNextId("EMP"));
    }

    @Test
    void testGetProfile() throws Exception {
        setupMockQuery();
        employeeDAO.getProfile("EMP001");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testGetReportees() throws Exception {
        setupMockQuery();
        employeeDAO.getReportees("MGR001");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testIsReportee() throws Exception {
        setupMockQuery();
        when(mockResultSet.next()).thenReturn(true);
        assertTrue(employeeDAO.isReportee("MGR001", "EMP001"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        setupMockUpdate();
        employeeDAO.updateProfile("EMP001", "123", "Addr", "Mom");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateProfessionalDetails() throws Exception {
        setupMockUpdate();
        employeeDAO.updateProfessionalDetails("EMP001", "1", "2", 5000.0, "MGR");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testBirthdayAnniversaryQueries() throws Exception {
        setupMockQuery();
        employeeDAO.getUpcomingBirthdays();
        verify(mockPreparedStatement, times(1)).executeQuery();

        employeeDAO.getBirthdaysToday();
        verify(mockPreparedStatement, times(2)).executeQuery(); // called again

        employeeDAO.getWorkAnniversaries();
        verify(mockPreparedStatement, times(3)).executeQuery();

        employeeDAO.getWorkAnniversariesToday();
        verify(mockPreparedStatement, times(4)).executeQuery();
    }

    @Test
    void testSearchEmployees() throws Exception {
        setupMockQuery();
        employeeDAO.searchEmployees("keyword");
        // Verify multiple setStrings for the keyword params
        verify(mockPreparedStatement, atLeast(6)).setString(anyInt(), contains("%keyword%"));
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testAuthOperations() throws Exception {
        setupMockQuery();
        employeeDAO.getAuthDetails("EMP001");
        verify(mockPreparedStatement).executeQuery();

        setupMockUpdate();
        employeeDAO.recordSuccessfulLogin("EMP001");
        verify(mockPreparedStatement).executeUpdate();

        employeeDAO.recordFailedLogin("EMP001");
        verify(mockPreparedStatement, times(2)).executeUpdate();

        employeeDAO.lockAccount("EMP001");
        verify(mockPreparedStatement, times(3)).executeUpdate();

        employeeDAO.updatePassword("EMP001", "newHash");
        verify(mockPreparedStatement, times(4)).executeUpdate();
    }

    @Test
    void testGetSecurityDetails() throws Exception {
        setupMockQuery();
        employeeDAO.getSecurityDetails("EMP001");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testToggleStatus() throws Exception {
        setupMockUpdate();
        employeeDAO.toggleStatus("EMP001");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAssignManager() throws Exception {
        setupMockUpdate();
        employeeDAO.assignManager("EMP001", "MGRNew");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testPrintAllEmployees() throws Exception {
        setupMockQuery();
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString(anyString())).thenReturn("Data");
        when(mockResultSet.getInt("is_active")).thenReturn(1);

        // This prints to stdout, just ensure no crash
        assertDoesNotThrow(() -> employeeDAO.printAllEmployees());

        setupMockQuery();
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Error"));
        assertDoesNotThrow(() -> employeeDAO.printAllEmployees()); // Should catch and log
    }
}
