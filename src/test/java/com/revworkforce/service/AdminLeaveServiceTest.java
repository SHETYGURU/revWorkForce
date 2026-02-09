package com.revworkforce.service;

import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.LeavePolicyDAO;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AdminLeaveServiceTest {

    @Mock
    private LeavePolicyDAO mockPolicyDao;
    @Mock
    private LeaveDAO mockLeaveDao;

    private MockedStatic<InputUtil> mockedInputUtil;
    private MockedStatic<AuditService> mockedAuditService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        setStaticField(AdminLeaveService.class, "policyDAO", mockPolicyDao);
        setStaticField(AdminLeaveService.class, "leaveDAO", mockLeaveDao);

        mockedInputUtil = Mockito.mockStatic(InputUtil.class);
        mockedAuditService = Mockito.mockStatic(AuditService.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mockedInputUtil != null)
            mockedInputUtil.close();
        if (mockedAuditService != null)
            mockedAuditService.close();

        setStaticField(AdminLeaveService.class, "policyDAO", new LeavePolicyDAO());
        setStaticField(AdminLeaveService.class, "leaveDAO", new LeaveDAO());
    }

    @Test
    void testConfigureLeaveTypes_Failure() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readString(contains("Name"))).thenReturn("Sick");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Max"))).thenReturn(10);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Carry"))).thenReturn("Y");

        doThrow(new RuntimeException("DB Error")).when(mockPolicyDao).createLeaveType(anyString(), anyInt(),
                anyBoolean());

        AdminLeaveService.configureLeaveTypes();

        verify(mockPolicyDao).createLeaveType("Sick", 10, true);
    }

    @Test
    void testConfigureLeaveTypes() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readString(contains("Name"))).thenReturn("Sick");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Max"))).thenReturn(10);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Carry"))).thenReturn("Y");

        AdminLeaveService.configureLeaveTypes();

        verify(mockPolicyDao).createLeaveType("Sick", 10, true);
    }

    @Test
    void testAssignLeaveQuotas() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP1");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Leave Type ID"))).thenReturn(1);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Year"))).thenReturn(2024);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Quota"))).thenReturn(12);

        AdminLeaveService.assignLeaveQuotas();

        verify(mockLeaveDao).assignLeaveQuota("EMP1", 1, 2024, 12);
    }

    @Test
    void testRevokeLeave() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Leave Application ID"))).thenReturn(100);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Reason"))).thenReturn("Admin Action");

        AdminLeaveService.revokeLeave();

        verify(mockLeaveDao).updateLeaveStatus(eq(100), anyString(), eq("REVOKED"), eq("Admin Action"));
    }

    @Test
    void testAdjustLeaveBalance() throws Exception {
        // AdminLeaveService.adjustLeaveBalance() calls assignLeaveQuotas() internally
        mockedInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP1");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Leave Type ID"))).thenReturn(1);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Year"))).thenReturn(2024);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Quota"))).thenReturn(20);

        AdminLeaveService.adjustLeaveBalance();

        verify(mockLeaveDao).assignLeaveQuota("EMP1", 1, 2024, 20);
    }

    @Test
    void testLeaveReportsMenu() throws Exception {
        // 1. Employee Leave History (Option 3)
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                .thenReturn(3) // Employee Report
                .thenReturn(4); // Back

        mockedInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP1");

        AdminLeaveService.leaveReportsMenu();

        verify(mockLeaveDao).getEmployeeLeaveReport("EMP1");
    }

    @Test
    void testConfigureLeaveTypes_CarryForwardNo() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readString(contains("Name"))).thenReturn("Annual");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Max"))).thenReturn(15);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Carry"))).thenReturn("N");

        AdminLeaveService.configureLeaveTypes();

        verify(mockPolicyDao).createLeaveType("Annual", 15, false);
    }

    @Test
    void testAssignLeaveQuotas_Failure() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP1");
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Leave Type ID"))).thenReturn(1);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Year"))).thenReturn(2024);
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Quota"))).thenReturn(12);

        doThrow(new RuntimeException("DB Error")).when(mockLeaveDao).assignLeaveQuota(anyString(), anyInt(), anyInt(),
                anyInt());

        AdminLeaveService.assignLeaveQuotas();

        verify(mockLeaveDao).assignLeaveQuota("EMP1", 1, 2024, 12);
    }

    @Test
    void testRevokeLeave_Failure() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Leave Application ID"))).thenReturn(100);
        mockedInputUtil.when(() -> InputUtil.readString(contains("Reason"))).thenReturn("Admin Action");

        doThrow(new RuntimeException("DB Error")).when(mockLeaveDao).updateLeaveStatus(anyInt(), anyString(),
                anyString(), anyString());

        AdminLeaveService.revokeLeave();

        verify(mockLeaveDao).updateLeaveStatus(eq(100), anyString(), eq("REVOKED"), eq("Admin Action"));
    }

    @Test
    void testViewLeaveStatistics() throws Exception {
        AdminLeaveService.viewLeaveStatistics();
        verify(mockLeaveDao).getLeaveStatistics();
    }

    @Test
    void testViewLeaveStatistics_Failure() throws Exception {
        doThrow(new RuntimeException("DB Error")).when(mockLeaveDao).getLeaveStatistics();
        AdminLeaveService.viewLeaveStatistics();
        verify(mockLeaveDao).getLeaveStatistics();
    }

    @Test
    void testLeaveReportsMenu_Option1() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                .thenReturn(1) // View Statistics
                .thenReturn(4); // Back

        AdminLeaveService.leaveReportsMenu();

        verify(mockLeaveDao).getLeaveStatistics();
    }

    @Test
    void testLeaveReportsMenu_Option2() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                .thenReturn(2) // Department Report
                .thenReturn(4); // Back

        AdminLeaveService.leaveReportsMenu();

        verify(mockLeaveDao).getDepartmentLeaveReport();
    }

    @Test
    void testLeaveReportsMenu_InvalidOption() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                .thenReturn(999) // Invalid
                .thenReturn(4); // Back

        AdminLeaveService.leaveReportsMenu();
    }

    @Test
    void testLeaveReportsMenu_ErrorHandling() throws Exception {
        mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                .thenReturn(1) // Statistics
                .thenReturn(4); // Back

        doThrow(new RuntimeException("DB Error")).when(mockLeaveDao).getLeaveStatistics();

        AdminLeaveService.leaveReportsMenu();

        verify(mockLeaveDao).getLeaveStatistics();
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
