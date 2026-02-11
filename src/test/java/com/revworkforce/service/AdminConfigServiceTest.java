package com.revworkforce.service;

import com.revworkforce.dao.*;
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
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class AdminConfigServiceTest {

        @Mock
        private DepartmentDAO mockDeptDao;
        @Mock
        private DesignationDAO mockDesigDao;
        @Mock
        private PerformanceCycleDAO mockCycleDao;
        @Mock
        private SystemPolicyDAO mockPolicyDao;
        @Mock
        private HolidayDAO mockHolidayDao;
        @Mock
        private ResultSet mockResultSet;

        private MockedStatic<InputUtil> mockedInputUtil;
        private MockedStatic<AuditService> mockedAuditService;

        @BeforeEach
        void setUp() throws Exception {
                MockitoAnnotations.openMocks(this);

                setStaticField(AdminConfigService.class, "departmentDAO", mockDeptDao);
                setStaticField(AdminConfigService.class, "designationDAO", mockDesigDao);
                setStaticField(AdminConfigService.class, "cycleDAO", mockCycleDao);
                setStaticField(AdminConfigService.class, "policyDAO", mockPolicyDao);
                setStaticField(AdminConfigService.class, "holidayDAO", mockHolidayDao);

                mockedInputUtil = Mockito.mockStatic(InputUtil.class);
                mockedAuditService = Mockito.mockStatic(AuditService.class);
        }

        @AfterEach
        void tearDown() throws Exception {
                if (mockedInputUtil != null)
                        mockedInputUtil.close();
                if (mockedAuditService != null)
                        mockedAuditService.close();

                // Reset
                setStaticField(AdminConfigService.class, "departmentDAO", new DepartmentDAO());
                setStaticField(AdminConfigService.class, "designationDAO", new DesignationDAO());
                setStaticField(AdminConfigService.class, "cycleDAO", new PerformanceCycleDAO());
                setStaticField(AdminConfigService.class, "policyDAO", new SystemPolicyDAO());
                setStaticField(AdminConfigService.class, "holidayDAO", new HolidayDAO());
        }

        @Test
        void testManageDepartments_Add() throws Exception {
                // Mock menu choices: 1 (Add), then 5 (Back)
                // Note: InputUtil calls for "Select Option" are used in loop
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(1)
                                .thenReturn(5);

                mockedInputUtil.when(() -> InputUtil.readString(contains("New Department Name")))
                                .thenReturn("HR");

                AdminConfigService.manageDepartments();

                verify(mockDeptDao).addDepartment("HR");
        }

        @Test
        void testManageDepartments_View() throws Exception {
                // Mock menu choices: 2 (View), then 5 (Back)
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(2)
                                .thenReturn(5);

                // Mock result set for view
                java.sql.Statement mockStmt = mock(java.sql.Statement.class);
                java.sql.Connection mockCon = mock(java.sql.Connection.class);

                when(mockDeptDao.getAllDepartments()).thenReturn(mockResultSet);
                when(mockResultSet.getStatement()).thenReturn(mockStmt);
                when(mockStmt.getConnection()).thenReturn(mockCon);
                when(mockResultSet.next()).thenReturn(true, false);
                when(mockResultSet.getString("department_id")).thenReturn("1");
                when(mockResultSet.getString("department_name")).thenReturn("HR");

                AdminConfigService.manageDepartments();

                verify(mockDeptDao).getAllDepartments();
        }

        @Test
        void testConfigurePerformanceCycles() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Performance Cycle Year"))).thenReturn(2024);
                mockedInputUtil.when(() -> InputUtil.readString(contains("Start Date"))).thenReturn("2024-01-01");
                mockedInputUtil.when(() -> InputUtil.readString(contains("End Date"))).thenReturn("2024-12-31");

                AdminConfigService.configurePerformanceCycles();

                verify(mockCycleDao).createCycle(eq(2024), any(Date.class), any(Date.class));
        }

        @Test
        void testConfigureHolidays() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readString(contains("Holiday Name"))).thenReturn("Xmas");
                // Date loops until valid date not in past
                // We provide a future date to pass the check
                mockedInputUtil.when(() -> InputUtil.readString(contains("Date"))).thenReturn("2099-12-25");

                AdminConfigService.configureHolidays();

                verify(mockHolidayDao).addHoliday(eq("Xmas"), any(Date.class), eq(2099));
        }

        @Test
        void testManageDesignations() throws Exception {
                // 1. Add Designation
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(1) // Add
                                .thenReturn(5); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Designation Name")))
                                .thenReturn("Lead");

                AdminConfigService.manageDesignations();

                verify(mockDesigDao).addDesignation("Lead");
        }

        @Test
        void testManageDepartments_Update() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(3) // Update
                                .thenReturn(5); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Department ID to Update")))
                                .thenReturn("1");
                mockedInputUtil.when(() -> InputUtil.readString(contains("New Department Name")))
                                .thenReturn("Finance");

                AdminConfigService.manageDepartments();

                verify(mockDeptDao).updateDepartment("1", "Finance");
        }

        @Test
        void testManageDepartments_Delete() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(4) // Delete
                                .thenReturn(5); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Department ID to Delete")))
                                .thenReturn("1");

                AdminConfigService.manageDepartments();

                verify(mockDeptDao).deleteDepartment("1");
        }

        @Test
        void testManageDesignations_Update() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(3) // Update
                                .thenReturn(5); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Designation ID to Update")))
                                .thenReturn("1");
                mockedInputUtil.when(() -> InputUtil.readString(contains("New Designation Name")))
                                .thenReturn("Senior Lead");

                AdminConfigService.manageDesignations();

                verify(mockDesigDao).updateDesignation("1", "Senior Lead");
        }

        @Test
        void testManageDesignations_Delete() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(4) // Delete
                                .thenReturn(5); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Designation ID to Delete")))
                                .thenReturn("1");

                AdminConfigService.manageDesignations();

                verify(mockDesigDao).deleteDesignation("1");
        }

        @Test
        void testManageSystemPolicies() throws Exception {
                // 1. Add Policy
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(1) // Add
                                .thenReturn(4); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Policy Name")))
                                .thenReturn("Remote Work");
                mockedInputUtil.when(() -> InputUtil.readString(contains("Policy Value")))
                                .thenReturn("WFH Allowed");

                AdminConfigService.manageSystemPolicies();

                verify(mockPolicyDao).updatePolicy("Remote Work", "WFH Allowed");
        }

        @Test
        void testManageSystemPolicies_Delete() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option")))
                                .thenReturn(3) // Delete
                                .thenReturn(4); // Back

                mockedInputUtil.when(() -> InputUtil.readString(contains("Policy Name to Delete")))
                                .thenReturn("Remote Work");

                AdminConfigService.manageSystemPolicies();

                verify(mockPolicyDao).deletePolicy("Remote Work");
        }

        @Test
        void testManageDepartments_Failure() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option"))).thenReturn(1).thenReturn(5);
                mockedInputUtil.when(() -> InputUtil.readString(contains("New Department Name"))).thenReturn("HR");

                doThrow(new RuntimeException("DB Error")).when(mockDeptDao).addDepartment(anyString());

                AdminConfigService.manageDepartments();

                verify(mockDeptDao).addDepartment("HR");
        }

        @Test
        void testManageDesignations_Failure() throws Exception {
                mockedInputUtil.when(() -> InputUtil.readInt(contains("Select Option"))).thenReturn(1).thenReturn(5);
                mockedInputUtil.when(() -> InputUtil.readString(contains("Designation Name"))).thenReturn("Lead");

                doThrow(new RuntimeException("DB Error")).when(mockDesigDao).addDesignation(anyString());

                AdminConfigService.manageDesignations();

                verify(mockDesigDao).addDesignation("Lead");
        }

        private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(null, value);
        }
}
