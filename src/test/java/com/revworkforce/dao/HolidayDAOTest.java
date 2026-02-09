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

class HolidayDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBConnection> mockedDBConnection;
    private HolidayDAO holidayDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockedDBConnection = Mockito.mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        holidayDAO = new HolidayDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testAddHoliday() throws Exception {
        Date date = Date.valueOf("2024-01-01");
        holidayDAO.addHoliday("New Year", date, 2024);

        verify(mockPreparedStatement).setString(1, "New Year");
        verify(mockPreparedStatement).setDate(2, date);
        verify(mockPreparedStatement).setInt(3, 2024);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetHolidays() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        holidayDAO.getHolidays(2024);

        verify(mockPreparedStatement).setInt(1, 2024);
        verify(mockPreparedStatement).executeQuery();
    }
}
