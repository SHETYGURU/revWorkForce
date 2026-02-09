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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NotificationDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private NotificationDAO notificationDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        notificationDAO = new NotificationDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testCreateNotification() throws Exception {
        notificationDAO.createNotification("EMP1", "INFO", "Hello");

        verify(mockPreparedStatement).setString(1, "EMP1");
        verify(mockPreparedStatement).setString(2, "INFO");
        verify(mockPreparedStatement).setString(3, "Hello");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetUnreadCount() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        int count = notificationDAO.getUnreadCount("EMP1");

        assertEquals(5, count);
        verify(mockPreparedStatement).setString(1, "EMP1");
    }

    @Test
    void testPrintAndMarkRead() throws Exception {
        // This method uses TWO prepared statements.
        // We need to mock preparing statement twice.
        PreparedStatement psSelect = mock(PreparedStatement.class);
        PreparedStatement psUpdate = mock(PreparedStatement.class);

        // Return different mocks based on SQL or sequence
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(psSelect)
                .thenReturn(psUpdate);

        when(psSelect.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("notification_id")).thenReturn(10);
        when(mockResultSet.getString("message")).thenReturn("Msg");
        when(mockResultSet.getString("created_at")).thenReturn("2024-01-01");

        notificationDAO.printAndMarkRead("EMP1");

        verify(psSelect).setString(1, "EMP1");
        verify(psUpdate).setInt(1, 10);
        verify(psUpdate).addBatch();
        verify(psUpdate).executeBatch();
    }
}
