package com.revworkforce.service;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.NotificationDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationDAO mockNotifDao;
    @Mock
    private EmployeeDAO mockEmpDao;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setStaticField(NotificationService.class, "dao", mockNotifDao);
        setStaticField(NotificationService.class, "employeeDAO", mockEmpDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        setStaticField(NotificationService.class, "dao", null);
        setStaticField(NotificationService.class, "employeeDAO", null);
        // ideally reset to new instances but for tests ok
    }

    @Test
    void testGetUnreadCount() throws Exception {
        when(mockNotifDao.getUnreadCount("EMP1")).thenReturn(5);
        int count = NotificationService.getUnreadCount("EMP1");
        assertEquals(5, count);
    }

    @Test
    void testNotifyLeaveUpdate() throws Exception {
        NotificationService.notifyLeaveUpdate("EMP1", "APPROVED");
        verify(mockNotifDao).createNotification(eq("EMP1"), eq("LEAVE_APPROVED"), contains("APPROVED"));
    }

    @Test
    void testGenerateDailyNotifications() throws Exception {
        when(mockEmpDao.getBirthdaysToday()).thenReturn(mockResultSet);
        when(mockEmpDao.getWorkAnniversariesToday()).thenReturn(mockResultSet);

        // Mock one birthday
        when(mockResultSet.next()).thenReturn(true, false); // for first loop
        when(mockResultSet.getString("employee_id")).thenReturn("EMP1");
        when(mockResultSet.getString("first_name")).thenReturn("John");

        // Note: the same mockResultSet is used for both calls in this simplified setup.
        // It might cause issues if the second call expects fresh iterator.
        // Better:
        ResultSet birthdayRS = mock(ResultSet.class);
        ResultSet anniversaryRS = mock(ResultSet.class);
        when(mockEmpDao.getBirthdaysToday()).thenReturn(birthdayRS);
        when(mockEmpDao.getWorkAnniversariesToday()).thenReturn(anniversaryRS);

        when(birthdayRS.next()).thenReturn(true, false);
        when(birthdayRS.getString("employee_id")).thenReturn("EMP1");
        when(birthdayRS.getString("first_name")).thenReturn("John");

        when(anniversaryRS.next()).thenReturn(true, false);
        when(anniversaryRS.getString("employee_id")).thenReturn("EMP2");

        NotificationService.generateDailyNotifications();

        verify(mockNotifDao).createNotification(eq("EMP1"), eq("BIRTHDAY"), contains("Happy Birthday"));
        verify(mockNotifDao).createNotification(eq("EMP2"), eq("ANNIVERSARY"), contains("Anniversary"));
    }

    @Test
    void testViewNotifications_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockNotifDao).printAndMarkRead("EMP1");

        NotificationService.viewNotifications("EMP1");

        verify(mockNotifDao).printAndMarkRead("EMP1");
    }

    @Test
    void testViewNotifications() throws Exception {
        // NotificationService.viewNotifications calls dao.printAndMarkRead
        NotificationService.viewNotifications("EMP1");

        verify(mockNotifDao).printAndMarkRead("EMP1");
    }

    @Test
    void testGenerateDailyNotifications_Failure() throws Exception {
        when(mockEmpDao.getBirthdaysToday()).thenThrow(new RuntimeException("DB Error"));

        NotificationService.generateDailyNotifications();

        verify(mockEmpDao).getBirthdaysToday();
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
