package com.revworkforce.service;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.model.Employee;
import com.revworkforce.util.InputUtil;
import com.revworkforce.util.PasswordUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private EmployeeDAO mockDao;
    private MockedStatic<InputUtil> mockInputUtil;

    @BeforeEach
    void setUp() throws Exception {
        mockDao = mock(EmployeeDAO.class);
        setPrivateStaticField(AuthService.class, "dao", mockDao);
        mockInputUtil = mockStatic(InputUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockInputUtil.close();
    }

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    void testLogin_Success() throws Exception {
        Map<String, Object> authDetails = new HashMap<>();
        authDetails.put("account_locked", 0);
        authDetails.put("failed_login_attempts", 0);
        String realHash = PasswordUtil.hashPassword("password");
        authDetails.put("password_hash", realHash);

        when(mockDao.getAuthDetails("EMP001")).thenReturn(authDetails);
        when(mockDao.getEmployeeById("EMP001")).thenReturn(new Employee());

        boolean result = AuthService.login("EMP001", "password");

        assertTrue(result);
        verify(mockDao).recordSuccessfulLogin("EMP001");
    }

    @Test
    void testLogin_Failure_WrongPassword() throws Exception {
        Map<String, Object> authDetails = new HashMap<>();
        authDetails.put("account_locked", 0);
        authDetails.put("failed_login_attempts", 0);
        String realHash = PasswordUtil.hashPassword("password");
        authDetails.put("password_hash", realHash);

        when(mockDao.getAuthDetails("EMP001")).thenReturn(authDetails);

        boolean result = AuthService.login("EMP001", "wrongpass");

        assertFalse(result);
        verify(mockDao).recordFailedLogin("EMP001");
    }

    @Test
    void testChangePassword_Failure_DBError() throws Exception {
        Map<String, Object> authDetails = new HashMap<>();
        String oldHash = PasswordUtil.hashPassword("oldPass");
        authDetails.put("password_hash", oldHash);

        when(mockDao.getAuthDetails("EMP001")).thenReturn(authDetails);

        // dao.updatePassword now throws Exception
        when(mockDao.updatePassword(eq("EMP001"), anyString())).thenThrow(new RuntimeException("DB Error"));

        boolean result = AuthService.changePassword("EMP001", "oldPass", "newPass");

        assertFalse(result);
    }

    @Test
    void testChangePassword_Success() throws Exception {
        Map<String, Object> authDetails = new HashMap<>();
        String oldHash = PasswordUtil.hashPassword("oldPass");
        authDetails.put("password_hash", oldHash);

        when(mockDao.getAuthDetails("EMP001")).thenReturn(authDetails);

        boolean result = AuthService.changePassword("EMP001", "oldPass", "newPass");

        assertTrue(result);
        verify(mockDao).updatePassword(eq("EMP001"), anyString());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        when(mockDao.getAuthDetails("EMP001")).thenReturn(null);

        boolean result = AuthService.login("EMP001", "password");

        assertFalse(result);
    }

    @Test
    void testLogin_AccountLocked() throws Exception {
        Map<String, Object> authDetails = new HashMap<>();
        authDetails.put("account_locked", 1);
        when(mockDao.getAuthDetails("EMP001")).thenReturn(authDetails);

        boolean result = AuthService.login("EMP001", "password");

        assertFalse(result);
    }

    @Test
    void testForgotPasswordFlow_Success() throws Exception {
        mockInputUtil.when(() -> InputUtil.readString(contains("Employee ID"))).thenReturn("EMP001");

        Map<String, Object> secDetails = new HashMap<>();
        secDetails.put("question_text", "Pet Name?");
        secDetails.put("answer_hash", PasswordUtil.hashPassword("Fluffy"));

        when(mockDao.getSecurityDetails("EMP001")).thenReturn(secDetails);

        mockInputUtil.when(() -> InputUtil.readString(contains("Answer"))).thenReturn("Fluffy");
        mockInputUtil.when(() -> InputUtil.readString(contains("New Password"))).thenReturn("newPass");
        mockInputUtil.when(() -> InputUtil.readString(contains("Confirm New Password"))).thenReturn("newPass");

        AuthService.forgotPasswordFlow();

        verify(mockDao).updatePassword(eq("EMP001"), anyString());
    }

    @Test
    void testChangePassword_UserNotFound() throws Exception {
        when(mockDao.getAuthDetails("EMP001")).thenReturn(null);

        boolean result = AuthService.changePassword("EMP001", "oldPass", "newPass");

        assertFalse(result);
        verify(mockDao, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void testLogin_FailedAttempts_Lockout() throws Exception {
        Map<String, Object> authDetails = new HashMap<>();
        authDetails.put("account_locked", 0);
        authDetails.put("failed_login_attempts", 2); // On 3rd attempt
        String realHash = PasswordUtil.hashPassword("password");
        authDetails.put("password_hash", realHash);

        when(mockDao.getAuthDetails("EMP001")).thenReturn(authDetails);

        boolean result = AuthService.login("EMP001", "wrongpass");

        assertFalse(result);
        verify(mockDao).recordFailedLogin("EMP001");
        verify(mockDao).lockAccount("EMP001");
    }
}
