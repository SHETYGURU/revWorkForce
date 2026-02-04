package com.revworkforce.dao;

import com.revworkforce.model.Employee;
import com.revworkforce.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeeDAOTest {

    private MockedStatic<DBConnection> mockDBConnection;
    private Connection mockConnection;
    private PreparedStatement mockPs;
    private ResultSet mockRs;
    private EmployeeDAO employeeDAO;

    @BeforeEach
    public void setUp() throws Exception {
        mockConnection = Mockito.mock(Connection.class);
        mockPs = Mockito.mock(PreparedStatement.class);
        mockRs = Mockito.mock(ResultSet.class);
        employeeDAO = new EmployeeDAO();

        mockDBConnection = Mockito.mockStatic(DBConnection.class);
        mockDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
    }

    @AfterEach
    public void tearDown() {
        mockDBConnection.close();
    }

    @Test
    public void testGetEmployeeById_Found() throws Exception {
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("employee_id")).thenReturn("E001");
        when(mockRs.getString("first_name")).thenReturn("John");
        when(mockRs.getString("last_name")).thenReturn("Doe");
        when(mockRs.getString("email")).thenReturn("john@example.com");

        Employee emp = employeeDAO.getEmployeeById("E001");

        Assertions.assertNotNull(emp);
        Assertions.assertEquals("E001", emp.getEmployeeId());
        Assertions.assertEquals("John", emp.getFirstName());

        verify(mockPs).setString(1, "E001");
    }

    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        when(mockRs.next()).thenReturn(false);

        Employee emp = employeeDAO.getEmployeeById("E999");

        Assertions.assertNull(emp);
    }
}
