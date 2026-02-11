package com.revworkforce.service;

import com.revworkforce.model.Employee;
import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.dao.DepartmentDAO;
import com.revworkforce.dao.DesignationDAO;
import com.revworkforce.util.InputUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AdminServiceTest {

        private EmployeeDAO mockEmpDao;
        private DepartmentDAO mockDeptDao;
        private DesignationDAO mockDesigDao;
        private MockedStatic<InputUtil> mockInputUtil;

        @BeforeEach
        void setUp() throws Exception {
                // Initialize Mockito annotations
                mockEmpDao = Mockito.mock(EmployeeDAO.class);
                mockDeptDao = Mockito.mock(DepartmentDAO.class);
                mockDesigDao = Mockito.mock(DesignationDAO.class);

                // Inject Mocks into AdminService:
                // AdminService uses multiple DAOs (Employee, Department, Designation), all must
                // be injected
                setPrivateStaticField(AdminService.class, "employeeDAO", mockEmpDao);
                setPrivateStaticField(AdminService.class, "departmentDAO", mockDeptDao);
                setPrivateStaticField(AdminService.class, "designationDAO", mockDesigDao);

                // Inject into EmployeeService as well since AdminService delegates some checks
                setPrivateStaticField(EmployeeService.class, "employeeDAO", mockEmpDao);

                // Mock Static InputUtil: This is critical for testing console-based UI services
                // allowing us to script the user's responses.
                mockInputUtil = Mockito.mockStatic(InputUtil.class);
        }

        @AfterEach
        void tearDown() {
                // Always close static mocks
                if (mockInputUtil != null) {
                        mockInputUtil.close();
                }
        }

        /**
         * Test Case: Adming adding a new employee.
         * Complexity: High (Interaction with multiple DAOs and many inputs)
         * Steps:
         * 1. Mock inputs for Manager Role check, Personal Info, Professional Info.
         * 2. Mock validations (e.g., Dept ID exists).
         * 3. Execute method.
         * 4. Capture the 'Employee' object passed to the DAO to verify all fields are
         * set correctly.
         */
        @Test
        void testAddEmployee_Success() throws Exception {
                // Mock Inputs for AdminService.addEmployee() flow sequentially:

                // 1. Role Check: "N" -> Not a Manager
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Manager?"), any(),
                                anyString()))
                                .thenReturn("N");

                // Mock ID Generation
                when(mockEmpDao.getNextId("EMP")).thenReturn("EMP001");

                // 2-6. Personal Details (First, Last, Email, Phone, Address)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("First Name"), any(),
                                anyString()))
                                .thenReturn("John");

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Last Name"), any(),
                                anyString()))
                                .thenReturn("Doe");

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Email"), any()))
                                .thenReturn("john@example.com");

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Phone"), any()))
                                .thenReturn("1234567890");

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Address"), any(),
                                anyString()))
                                .thenReturn("123 Main St");

                // 7-8. Emergency Contact & DOB
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Emergency"), any(),
                                anyString()))
                                .thenReturn("Jane Doe");

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("DOB"), any()))
                                .thenReturn("1990-01-01");

                // 9. Joining Date Selection: Option 1 (Today)
                mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1);

                // 10. Department ID Selection
                // The service prints department list before this (not verified here, but
                // implied)
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Department ID"), any()))
                                .thenReturn("101");
                // Mock validation: 101 must exist
                when(mockDeptDao.isDepartmentIdExists("101")).thenReturn(true);

                // 11. Designation ID Selection
                // The service prints designation list before this
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Designation ID"), any()))
                                .thenReturn("201");
                // Mock validation: 201 must exist and match 'Employee' role (false)
                when(mockDesigDao.isDesignationIdExists("201")).thenReturn(true);
                when(mockDesigDao.isDesignationMatchRole("201", false)).thenReturn(true);

                // 12. Reporting Manager Selection
                // The service prints employee list before this
                mockInputUtil.when(() -> InputUtil.readString(contains("Manager ID"))).thenReturn(""); // Skip

                // 13. Salary
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Salary"), any(),
                                anyString()))
                                .thenReturn("50000");

                // Execute Service Method
                AdminService.addEmployee();

                // Verification:
                // Use ArgumentCaptor to inspect the Employee object constructed by the Service
                ArgumentCaptor<Employee> empCaptor = ArgumentCaptor.forClass(Employee.class);
                verify(mockEmpDao).insertEmployee(empCaptor.capture());

                Employee capturedEmp = empCaptor.getValue();

                // Assertions to ensure data integrity
                assertEquals("EMP001", capturedEmp.getEmployeeId());
                assertEquals("John", capturedEmp.getFirstName());
                assertEquals("Doe", capturedEmp.getLastName());
                assertEquals("john@example.com", capturedEmp.getEmail());
                assertEquals("1234567890", capturedEmp.getPhone());
                assertEquals("123 Main St", capturedEmp.getAddress());
                assertEquals("Jane Doe", capturedEmp.getEmergencyContact());
                assertEquals("1990-01-01", capturedEmp.getDateOfBirth().toString());
                assertEquals(101, capturedEmp.getDepartmentId());
                assertEquals(201, capturedEmp.getDesignationId());
                assertNull(capturedEmp.getManagerId());
                assertEquals(50000.0, capturedEmp.getSalary());
                assertNotNull(capturedEmp.getJoiningDate());
                assertNotNull(capturedEmp.getPasswordHash());
        }

        @Test
        void testUpdateEmployee_ContactInfo() throws Exception {
                // Choice 1: Update Contact Info
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");

                // Mock validation for Employee ID existence
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1); // Select Option 1

                // Phone
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("New Phone"), any()))
                                .thenReturn("9876543210");
                when(mockEmpDao.isPhoneExists("9876543210")).thenReturn(false);

                // Address
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("New Address"), any(),
                                anyString()))
                                .thenReturn("456 Elm St");

                // Emergency Contact
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("New Emergency Contact"),
                                any(), anyString()))
                                .thenReturn("Mom");

                AdminService.updateEmployee();

                verify(mockEmpDao).updateProfile("EMP001", "9876543210", "456 Elm St", "Mom");
        }

        @Test
        void testUnlockEmployeeAccount() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");

                // Mock validation for Employee ID existence
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readString(contains("New Password"))).thenReturn("newpass");

                when(mockEmpDao.updatePassword(eq("EMP001"), anyString())).thenReturn(true);

                AdminService.resetUserPassword();

                verify(mockEmpDao).updatePassword(eq("EMP001"), anyString());
        }

        @Test
        void testAssignManager() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");
                mockInputUtil.when(() -> InputUtil.readString(contains("New Manager ID"))).thenReturn("MGR001");

                // Mock validation for Employee ID existence
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);
                // Mock validation for Manager ID existence
                when(mockEmpDao.isEmployeeExists("MGR001")).thenReturn(true);

                AdminService.assignManager();

                verify(mockEmpDao).assignManager("EMP001", "MGR001");
        }

        @Test
        void testToggleEmployeeStatus() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");

                // Mock validation for Employee ID existence
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);

                AdminService.toggleEmployeeStatus();

                verify(mockEmpDao).toggleStatus("EMP001");
        }

        @Test
        void testViewAllEmployees() throws Exception {
                AdminService.viewAllEmployees();
                verify(mockEmpDao).printAllEmployees();
        }

        @Test
        void testSearchEmployees() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("search"))).thenReturn("John");
                java.util.List<java.util.Map<String, Object>> mockList = new java.util.ArrayList<>();
                when(mockEmpDao.searchEmployees("John")).thenReturn(mockList);

                AdminService.searchEmployees();

                verify(mockEmpDao).searchEmployees("John");
        }

        @Test
        void testAddEmployee_Failure() throws Exception {
                // Test exception handling covering the catch block
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Manager?"), any(),
                                anyString()))
                                .thenThrow(new RuntimeException("Input Error"));

                AdminService.addEmployee();

                // Verify that insertEmployee was NEVER called due to exception
                verify(mockEmpDao, never()).insertEmployee(any(Employee.class));
        }

        @Test
        void testUpdateEmployee_Failure() throws Exception {
                // Test database error during update
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");

                // Mock validation to allow flow to proceed
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(1); // Option 1

                // Mock valid input flow
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("New Phone"), any()))
                                .thenReturn("9876543210");
                when(mockEmpDao.isPhoneExists("9876543210")).thenReturn(false);

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("New Address"), any(),
                                anyString()))
                                .thenReturn("Addr");
                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("New Emergency"), any(),
                                anyString()))
                                .thenReturn("Emg");

                // Force DAO exception
                doThrow(new RuntimeException("DB Error")).when(mockEmpDao).updateProfile(anyString(), anyString(),
                                anyString(), anyString());

                AdminService.updateEmployee();

                verify(mockEmpDao).updateProfile(eq("EMP001"), anyString(), anyString(), anyString());
        }

        @Test
        void testResetUserPassword_Failure() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");

                // Mock validation to allow flow to proceed
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readString(contains("New Password"))).thenReturn("newpass");

                when(mockEmpDao.updatePassword(anyString(), anyString())).thenReturn(false); // ID not found

                AdminService.resetUserPassword();

                verify(mockEmpDao).updatePassword(eq("EMP001"), anyString());
        }

        @Test
        void testAssignManager_Failure() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");
                mockInputUtil.when(() -> InputUtil.readString(contains("New Manager ID"))).thenReturn("MGR001");

                // Mock validation for IDs to allow flow to proceed
                when(mockEmpDao.isEmployeeExists("EMP001")).thenReturn(true);
                when(mockEmpDao.isEmployeeExists("MGR001")).thenReturn(true);

                doThrow(new RuntimeException("DB Error")).when(mockEmpDao).assignManager(anyString(), anyString());

                AdminService.assignManager();

                verify(mockEmpDao).assignManager("EMP001", "MGR001");
        }

        @Test
        void testUpdateEmployee_ProfessionalInfo() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP002");

                // Mock validation
                when(mockEmpDao.isEmployeeExists("EMP002")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(2);

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Department ID"), any()))
                                .thenReturn("5");
                when(mockDeptDao.isDepartmentIdExists("5")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readString(contains("Designation ID"))).thenReturn("10");
                when(mockDesigDao.isDesignationIdExists("10")).thenReturn(true);
                when(mockDesigDao.isDesignationMatchRole("10", false)).thenReturn(true); // Assuming role N

                mockInputUtil.when(() -> InputUtil.readString(contains("Manager ID"))).thenReturn("MGR001");
                // Manager exists validation handled by generic isEmployeeExists("MGR001") if
                // checked, but logic checks if !empty
                when(mockEmpDao.isEmployeeExists("MGR001")).thenReturn(true);

                mockInputUtil.when(() -> InputUtil.readValidatedString(contains("Salary"), any(),
                                anyString()))
                                .thenReturn("75000");

                AdminService.updateEmployee();

                verify(mockEmpDao).updateProfessionalDetails("EMP002", "5", "10", 75000.0, "MGR001");
        }

        @Test
        void testUpdateEmployee_InvalidChoice() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP003");
                mockInputUtil.when(() -> InputUtil.readInt(anyString())).thenReturn(99);

                AdminService.updateEmployee();

                verify(mockEmpDao, never()).updateProfile(anyString(), anyString(), anyString(), anyString());
                verify(mockEmpDao, never()).updateProfessionalDetails(anyString(), anyString(), anyString(),
                                anyDouble(),
                                anyString());
        }

        @Test
        void testResetUserPassword_EmptyPassword() throws Exception {
                mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");
                mockInputUtil.when(() -> InputUtil.readString(contains("New Password"))).thenReturn("");

                AdminService.resetUserPassword();

                verify(mockEmpDao, never()).updatePassword(anyString(), anyString());
        }

        private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(null, value);
        }
}
