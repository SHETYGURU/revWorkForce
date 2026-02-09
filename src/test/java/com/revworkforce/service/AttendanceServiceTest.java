package com.revworkforce.service;

import com.revworkforce.dao.AttendanceDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    @Mock
    private AttendanceDAO mockDao;

    private MockedStatic<AuditService> mockedAuditService;
    // We might need to mock DateUtil if we want consistent dates,
    // but AttendanceService calls DateUtil.getCurrentDate() which is static.

    // However, AttendanceService uses DateUtil.getCurrentDate()
    // Let's assume DateUtil works or we mock it if needed.
    // For this test, let's keep it simple.

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mockDAO into AttendanceService using Reflection
        setStaticField(AttendanceService.class, "dao", mockDao);

        // Mock AuditService to prevent DB calls
        mockedAuditService = Mockito.mockStatic(AuditService.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mockedAuditService != null) {
            mockedAuditService.close();
        }
        // Reset field to null or original if possible, but hard to get original.
        // For unit tests in isolation it is okay.
        setStaticField(AttendanceService.class, "dao", null);
        // Note: setting to null might break other tests if running in parallel JVM,
        // but here it is fine. Ideally we should create a new DAO() instance or save
        // original.
        setStaticField(AttendanceService.class, "dao", new AttendanceDAO());
    }

    @Test
    void testCheckIn_Success() throws Exception {
        when(mockDao.hasCheckedIn(anyString(), any(Date.class))).thenReturn(false);

        AttendanceService.checkIn("EMP1");

        verify(mockDao).checkIn("EMP1");
        mockedAuditService
                .verify(() -> AuditService.log(eq("EMP1"), anyString(), anyString(), anyString(), anyString()));
    }

    @Test
    void testCheckIn_AlreadyCheckedIn() throws Exception {
        when(mockDao.hasCheckedIn(anyString(), any(Date.class))).thenReturn(true);

        AttendanceService.checkIn("EMP1");

        verify(mockDao, never()).checkIn(anyString());
    }

    @Test
    void testCheckOut_Success() throws Exception {
        when(mockDao.hasCheckedOut(anyString(), any(Date.class))).thenReturn(false);

        AttendanceService.checkOut("EMP1");

        verify(mockDao).checkOut("EMP1");
    }

    @Test
    void testCheckIn_Failure() throws Exception {
        when(mockDao.hasCheckedIn(anyString(), any(Date.class))).thenReturn(false);
        doThrow(new RuntimeException("DB Error")).when(mockDao).checkIn("EMP1");

        AttendanceService.checkIn("EMP1");

        verify(mockDao).checkIn("EMP1");
    }

    @Test
    void testViewMyAttendance() throws Exception {
        // Need to mock ResultSet for getAttendanceHistory
        java.sql.ResultSet mockRS = mock(java.sql.ResultSet.class);
        when(mockDao.getAttendanceHistory("EMP1")).thenReturn(mockRS);

        AttendanceService.viewMyAttendance("EMP1");

        verify(mockDao).getAttendanceHistory("EMP1");
    }

    @Test
    void testCheckOut_AlreadyCheckedOut() throws Exception {
        when(mockDao.hasCheckedOut(anyString(), any(Date.class))).thenReturn(true);

        AttendanceService.checkOut("EMP1");

        verify(mockDao, never()).checkOut(anyString());
    }

    @Test
    void testCheckOut_Failure() throws Exception {
        when(mockDao.hasCheckedOut(anyString(), any(Date.class))).thenReturn(false);
        doThrow(new RuntimeException("DB Error")).when(mockDao).checkOut("EMP1");

        AttendanceService.checkOut("EMP1");

        verify(mockDao).checkOut("EMP1");
    }

    @Test
    void testCheckIn_HasCheckedInFailure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).hasCheckedIn(anyString(), any(Date.class));

        AttendanceService.checkIn("EMP1");

        verify(mockDao).hasCheckedIn(anyString(), any(Date.class));
    }

    @Test
    void testViewMyAttendance_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).getAttendanceHistory("EMP1");

        AttendanceService.viewMyAttendance("EMP1");

        verify(mockDao).getAttendanceHistory("EMP1");
    }

    // Helper method for reflection
    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        // Remove final modifier if present (Works in old Java, Java 17+ needs options
        // or Unsafe,
        // but often accessible is enough for simple static final in tests depending on
        // JDK)

        // For Java 17, removing final is hard.
        // But let's try just setAccessible(true) and set(null, value).
        // If it fails, we will know.
        field.set(null, value);
    }
}
