package com.revworkforce.service;

import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.util.InputUtil;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class LeaveServiceTest {

    @Mock
    private LeaveDAO mockDao;

    private MockedStatic<InputUtil> mockedInputUtil;
    private MockedStatic<AuditService> mockedAuditService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inject mockDAO: Reflection is used to set the private static 'dao' field in
        // LeaveService
        // This allows us to intercept DAO calls without needing dependency injection
        // framework
        setStaticField(LeaveService.class, "dao", mockDao);

        // Mock statics: InputUtil and AuditService are static utilities
        // We mock them to control user input and verify audit logging without side
        // effects
        mockedInputUtil = Mockito.mockStatic(InputUtil.class);
        mockedAuditService = Mockito.mockStatic(AuditService.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Close static mocks to avoid memory leaks and interference with other tests
        if (mockedInputUtil != null)
            mockedInputUtil.close();
        if (mockedAuditService != null)
            mockedAuditService.close();
        // Reset the static DAO field to a fresh instance or null to clean up state
        setStaticField(LeaveService.class, "dao", null);
        setStaticField(LeaveService.class, "dao", new LeaveDAO());
    }

    /**
     * Test Case: Successfully applying for leave.
     * Logic:
     * 1. Mock user inputs for Leave Type, Dates, and Reason.
     * 2. Call the service method.
     * 3. Verify that the DAO's applyLeave method is called with correctly parsed
     * parameters.
     * 4. Verify that an audit log entry is created.
     */
    @Test
    void testApplyLeave_Success() throws Exception {
        // Mock UI Inputs:
        // 1. Leave Type ID (e.g., 1 for Sick Leave) - The list is printed before this
        // read
        mockedInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1);
        // 2. Dates and Reason
        mockedInputUtil.when(() -> InputUtil.readString(contains("Start"))).thenReturn("2024-01-01");
        mockedInputUtil.when(() -> InputUtil.readString(contains("End"))).thenReturn("2024-01-05");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Reason"))).thenReturn("Sick");

        // Execute Service Method
        LeaveService.applyLeave("EMP1");

        // Verification:
        // Ensure DAO received the correct ID, Dates, and Reason
        verify(mockDao).applyLeave(eq("EMP1"), eq(1), any(Date.class), any(Date.class), eq("Sick"));
        // Ensure action was logged
        mockedAuditService
                .verify(() -> AuditService.log(eq("EMP1"), anyString(), anyString(), anyString(), anyString()));
    }

    @Test
    void testApplyLeave_InvalidDates() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1);
        // Start date AFTER End date
        mockedInputUtil.when(() -> InputUtil.readString(contains("Start"))).thenReturn("2024-01-05");
        mockedInputUtil.when(() -> InputUtil.readString(contains("End"))).thenReturn("2024-01-01");

        LeaveService.applyLeave("EMP1");

        // Should NOT call DAO
        verify(mockDao, never()).applyLeave(anyString(), anyInt(), any(), any(), anyString());
    }

    @Test
    void testViewLeaveBalance() throws Exception {
        LeaveService.viewLeaveBalance("EMP1");
        verify(mockDao).getLeaveBalances("EMP1");
    }

    @Test
    void testViewLeaveHistory() throws Exception {
        LeaveService.viewMyLeaves("EMP1");
        verify(mockDao).getMyLeaves("EMP1");
    }

    @Test
    void testViewLeaveBalance_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).getLeaveBalances("EMP1");
        LeaveService.viewLeaveBalance("EMP1");
        verify(mockDao).getLeaveBalances("EMP1");
    }

    @Test
    void testViewMyLeaves_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockDao).getMyLeaves("EMP1");
        LeaveService.viewMyLeaves("EMP1");
        verify(mockDao).getMyLeaves("EMP1");
    }

    @Test
    void testCancelLeave() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(100);

        LeaveService.cancelLeave("EMP1");

        verify(mockDao).cancelLeave(100, "EMP1");
        mockedAuditService
                .verify(() -> AuditService.log(eq("EMP1"), eq("CANCEL"), anyString(), anyString(), anyString()));
    }

    @Test
    void testCancelLeave_Failure() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(100);
        doThrow(new RuntimeException("DB Error")).when(mockDao).cancelLeave(100, "EMP1");

        LeaveService.cancelLeave("EMP1");

        verify(mockDao).cancelLeave(100, "EMP1");
    }

    @Test
    void testApplyLeave_InvalidDateFormat() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Start"))).thenReturn("invalid-date");

        LeaveService.applyLeave("EMP1");

        // Should NOT call DAO due to date parse error
        verify(mockDao, never()).applyLeave(anyString(), anyInt(), any(), any(), anyString());
    }

    @Test
    void testApplyLeave_Failure() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Start"))).thenReturn("2024-01-01");
        mockedInputUtil.when(() -> InputUtil.readString(contains("End"))).thenReturn("2024-01-05");
        mockedInputUtil.when(() -> InputUtil.readString(contains("Reason"))).thenReturn("Sick");

        doThrow(new RuntimeException("DB Error")).when(mockDao).applyLeave(anyString(), anyInt(), any(Date.class),
                any(Date.class), anyString());

        LeaveService.applyLeave("EMP1");

        verify(mockDao).applyLeave(anyString(), anyInt(), any(Date.class), any(Date.class), anyString());
    }

    @Test
    void testViewHolidays() {
        // Simply call to ensure no errors
        LeaveService.viewHolidays();
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
