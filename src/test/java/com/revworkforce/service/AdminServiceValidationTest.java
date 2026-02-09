package com.revworkforce.service;

import com.revworkforce.dao.DepartmentDAO;
import com.revworkforce.dao.DesignationDAO;
import com.revworkforce.dao.EmployeeDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for AdminService validation methods.
 * These tests cover all branches in extracted validation lambdas.
 */
class AdminServiceValidationTest {

    private EmployeeDAO mockEmpDao;
    private DepartmentDAO mockDeptDao;
    private DesignationDAO mockDesigDao;

    @BeforeEach
    void setUp() throws Exception {
        mockEmpDao = Mockito.mock(EmployeeDAO.class);
        mockDeptDao = Mockito.mock(DepartmentDAO.class);
        mockDesigDao = Mockito.mock(DesignationDAO.class);

        setPrivateStaticField(AdminService.class, "employeeDAO", mockEmpDao);
        setPrivateStaticField(AdminService.class, "departmentDAO", mockDeptDao);
        setPrivateStaticField(AdminService.class, "designationDAO", mockDesigDao);
    }

    // ==================== validateEmail Tests ====================

    @Test
    void testValidateEmail_ValidAndUnique() throws Exception {
        when(mockEmpDao.isEmailExists("valid@email.com")).thenReturn(false);

        String result = AdminService.validateEmail("valid@email.com");

        assertNull(result, "Valid unique email should return null");
        verify(mockEmpDao).isEmailExists("valid@email.com");
    }

    @Test
    void testValidateEmail_InvalidFormat() {
        String result = AdminService.validateEmail("invalid-email");

        assertNotNull(result);
        assertTrue(result.contains("Invalid email format"));
    }

    @Test
    void testValidateEmail_AlreadyExists() throws Exception {
        when(mockEmpDao.isEmailExists("existing@email.com")).thenReturn(true);

        String result = AdminService.validateEmail("existing@email.com");

        assertNotNull(result);
        assertTrue(result.contains("already exists"));
        verify(mockEmpDao).isEmailExists("existing@email.com");
    }

    @Test
    void testValidateEmail_DatabaseError() throws Exception {
        when(mockEmpDao.isEmailExists(anyString())).thenThrow(new RuntimeException("DB Connection Failed"));

        String result = AdminService.validateEmail("test@email.com");

        assertNotNull(result);
        assertTrue(result.contains("Error checking email"));
        assertTrue(result.contains("DB Connection Failed"));
    }

    // ==================== validatePhone Tests ====================

    @Test
    void testValidatePhone_Valid() throws Exception {
        when(mockEmpDao.isPhoneExists("1234567890")).thenReturn(false);

        String result = AdminService.validatePhone("1234567890");

        assertNull(result);
        verify(mockEmpDao).isPhoneExists("1234567890");
    }

    @Test
    void testValidatePhone_InvalidFormat() {
        String result = AdminService.validatePhone("123"); // Not 10 digits

        assertNotNull(result);
        assertTrue(result.contains("10 digits"));
    }

    @Test
    void testValidatePhone_AlreadyExists() throws Exception {
        when(mockEmpDao.isPhoneExists("1234567890")).thenReturn(true);

        String result = AdminService.validatePhone("1234567890");

        assertNotNull(result);
        assertTrue(result.contains("already exists"));
    }

    @Test
    void testValidatePhone_DatabaseError() throws Exception {
        when(mockEmpDao.isPhoneExists(anyString())).thenThrow(new RuntimeException("DB Error"));

        String result = AdminService.validatePhone("1234567890");

        assertNotNull(result);
        assertTrue(result.contains("Error checking phone"));
    }

    // ==================== validateDateFormat Tests ====================

    @Test
    void testValidateDateFormat_Valid() {
        String result = AdminService.validateDateFormat("2024-01-15");

        assertNull(result);
    }

    @Test
    void testValidatedateFormat_InvalidFormat_NoHyphens() {
        String result = AdminService.validateDateFormat("20240115");

        assertNotNull(result);
        assertTrue(result.contains("Invalid date format"));
    }

    @Test
    void testValidateDateFormat_InvalidFormat_WrongPattern() {
        String result = AdminService.validateDateFormat("01-15-2024");

        assertNotNull(result);
        assertTrue(result.contains("YYYY-MM-DD"));
    }

    @Test
    void testValidateDateFormat_InvalidFormat_Letters() {
        String result = AdminService.validateDateFormat("abcd-ef-gh");

        assertNotNull(result);
    }

    // ==================== validateDepartment Tests ====================

    @Test
    void testValidateDepartment_Valid() throws Exception {
        when(mockDeptDao.isDepartmentIdExists("D001")).thenReturn(true);

        String result = AdminService.validateDepartment("D001");

        assertNull(result);
        verify(mockDeptDao).isDepartmentIdExists("D001");
    }

    @Test
    void testValidateDepartment_NotFound() throws Exception {
        when(mockDeptDao.isDepartmentIdExists("D999")).thenReturn(false);

        String result = AdminService.validateDepartment("D999");

        assertNotNull(result);
        assertTrue(result.contains("Invalid Department ID"));
    }

    @Test
    void testValidateDepartment_DatabaseError() throws Exception {
        when(mockDeptDao.isDepartmentIdExists(anyString())).thenThrow(new RuntimeException("DB Error"));

        String result = AdminService.validateDepartment("D001");

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }

    // ==================== validateDesignation Tests ====================

    @Test
    void testValidateDesignation_ValidManager() throws Exception {
        when(mockDesigDao.isDesignationIdExists("DES001")).thenReturn(true);
        when(mockDesigDao.isDesignationMatchRole("DES001", true)).thenReturn(true);

        String result = AdminService.validateDesignation("DES001", true);

        assertNull(result);
        verify(mockDesigDao).isDesignationIdExists("DES001");
        verify(mockDesigDao).isDesignationMatchRole("DES001", true);
    }

    @Test
    void testValidateDesignation_ValidEmployee() throws Exception {
        when(mockDesigDao.isDesignationIdExists("DES002")).thenReturn(true);
        when(mockDesigDao.isDesignationMatchRole("DES002", false)).thenReturn(true);

        String result = AdminService.validateDesignation("DES002", false);

        assertNull(result);
    }

    @Test
    void testValidateDesignation_NotFound() throws Exception {
        when(mockDesigDao.isDesignationIdExists("DES999")).thenReturn(false);

        String result = AdminService.validateDesignation("DES999", true);

        assertNotNull(result);
        assertTrue(result.contains("Invalid Designation ID"));
    }

    @Test
    void testValidateDesignation_RoleMismatch() throws Exception {
        when(mockDesigDao.isDesignationIdExists("DES001")).thenReturn(true);
        when(mockDesigDao.isDesignationMatchRole("DES001", true)).thenReturn(false);

        String result = AdminService.validateDesignation("DES001", true);

        assertNotNull(result);
        assertTrue(result.contains("Invalid Designation for role"));
    }

    @Test
    void testValidateDesignation_DatabaseError() throws Exception {
        when(mockDesigDao.isDesignationIdExists(anyString())).thenThrow(new RuntimeException("DB Error"));

        String result = AdminService.validateDesignation("DES001", true);

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }

    @Test
    void testValidateDesignation_RoleCheckError() throws Exception {
        when(mockDesigDao.isDesignationIdExists("DES001")).thenReturn(true);
        when(mockDesigDao.isDesignationMatchRole("DES001", true)).thenThrow(new RuntimeException("Role check failed"));

        String result = AdminService.validateDesignation("DES001", true);

        assertNotNull(result);
        assertTrue(result.contains("Error"));
    }

    // ==================== Helper Methods ====================

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
