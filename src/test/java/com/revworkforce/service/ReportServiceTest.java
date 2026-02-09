package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private PerformanceDAO mockDao;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setStaticField(ReportService.class, "dao", mockDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        setStaticField(ReportService.class, "dao", null);
        setStaticField(ReportService.class, "dao", new PerformanceDAO());
    }

    @Test
    void testTeamPerformanceSummary() throws Exception {
        when(mockDao.getTeamPerformanceSummary("MGR1")).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("employee_id")).thenReturn("EMP1");
        when(mockResultSet.getDouble("avg_rating")).thenReturn(4.5);

        ReportService.teamPerformanceSummary("MGR1");

        verify(mockDao).getTeamPerformanceSummary("MGR1");
        verify(mockResultSet, atLeastOnce()).getString("employee_id");
    }

    @Test
    void testTeamPerformanceSummary_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).getTeamPerformanceSummary("MGR1");

        ReportService.teamPerformanceSummary("MGR1");

        verify(mockDao).getTeamPerformanceSummary("MGR1");
    }

    @Test
    void testTeamPerformanceSummary_NoResults() throws Exception {
        when(mockDao.getTeamPerformanceSummary("MGR1")).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No records

        ReportService.teamPerformanceSummary("MGR1");

        verify(mockDao).getTeamPerformanceSummary("MGR1");
        verify(mockResultSet, never()).getString(anyString());
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
