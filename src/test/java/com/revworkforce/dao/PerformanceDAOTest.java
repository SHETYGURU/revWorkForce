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

class PerformanceDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private PerformanceDAO performanceDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        performanceDAO = new PerformanceDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testCreateGoal() throws Exception {
        Date deadline = Date.valueOf("2024-12-31");
        performanceDAO.createGoal("EMP1", "Finish Project", deadline, "High", "All tests pass");

        verify(mockPreparedStatement).setString(1, "EMP1");
        verify(mockPreparedStatement).setString(2, "Finish Project");
        verify(mockPreparedStatement).setDate(3, deadline);
        verify(mockPreparedStatement).setString(4, "High");
        verify(mockPreparedStatement).setString(5, "All tests pass");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testSubmitSelfReview() throws Exception {
        performanceDAO.submitSelfReview("EMP1", 2024, "Delivered X", "Accomplished Y", "Improve Z", 4.5);

        verify(mockPreparedStatement).setString(1, "EMP1");
        verify(mockPreparedStatement).setInt(2, 2024);
        verify(mockPreparedStatement).setDouble(6, 4.5);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetTeamGoals() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        performanceDAO.getTeamGoals("MGR1");
        verify(mockPreparedStatement).setString(1, "MGR1");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void testSubmitManagerFeedback() throws Exception {
        performanceDAO.submitManagerFeedback(10, "Great work", 5);

        verify(mockPreparedStatement).setString(1, "Great work");
        verify(mockPreparedStatement).setInt(2, 5);
        verify(mockPreparedStatement).setInt(3, 10);
        verify(mockPreparedStatement).executeUpdate();
    }
}
