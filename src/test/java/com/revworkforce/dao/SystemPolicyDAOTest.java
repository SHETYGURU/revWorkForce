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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SystemPolicyDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private SystemPolicyDAO policyDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        policyDAO = new SystemPolicyDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testUpdatePolicy() throws Exception {
        policyDAO.updatePolicy("TIMEOUT", "30");

        // The query uses parameteres 1, 2, 3, 4 (some repeated)
        verify(mockPreparedStatement).setString(1, "TIMEOUT");
        verify(mockPreparedStatement).setString(2, "30");
        verify(mockPreparedStatement).setString(3, "TIMEOUT");
        verify(mockPreparedStatement).setString(4, "30");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeletePolicy() throws Exception {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        policyDAO.deletePolicy("TIMEOUT");

        verify(mockPreparedStatement).setString(1, "TIMEOUT");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeletePolicyNotFound() throws Exception {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        assertThrows(Exception.class, () -> policyDAO.deletePolicy("UNKNOWN"));
    }

    @Test
    void testGetAllPolicies() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        policyDAO.getAllPolicies();
        verify(mockPreparedStatement).executeQuery();
    }
}
