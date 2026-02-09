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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PerformanceCycleDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private PerformanceCycleDAO cycleDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        cycleDAO = new PerformanceCycleDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testCreateCycle() throws Exception {
        Date start = Date.valueOf("2024-01-01");
        Date end = Date.valueOf("2024-12-31");
        cycleDAO.createCycle("2024 Review", start, end);

        verify(mockPreparedStatement).setString(1, "2024 Review");
        verify(mockPreparedStatement).setDate(2, start);
        verify(mockPreparedStatement).setDate(3, end);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testCloseCycle() throws Exception {
        cycleDAO.closeCycle(1);

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetAllCycles() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        cycleDAO.getAllCycles();
        verify(mockPreparedStatement).executeQuery();
    }
}
