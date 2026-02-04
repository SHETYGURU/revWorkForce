package com.revworkforce.service;

import com.revworkforce.dao.EmployeeDAO;
import com.revworkforce.util.PasswordUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private EmployeeDAO mockDao;

    @BeforeEach
    public void setUp() throws Exception {
        mockDao = Mockito.mock(EmployeeDAO.class);
        setPrivateStaticField(AuthService.class, "dao", mockDao);
    }

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        // Remove final modifier if necessary (mostly not needed for simple unit tests
        // but good practice if fails)
        // Note: In modern Java, modifying static final fields via reflection can be
        // tricky,
        // but often works for testing if SecurityManager allows.
        field.set(null, value);
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Mock ResultSet behavior
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockDao.getAuthDetails("E001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("account_locked")).thenReturn(0);
        // "password" hashes to... well we need to mock verifyPassword or use real hash
        // It's better to mock PasswordUtil too if possible, but it's static.
        // For simplicity, let's assume we use the real PasswordUtil and just put a
        // valid hash in the mock RS.
        String correctHash = PasswordUtil.hashPassword("password");
        when(mockRs.getString("password_hash")).thenReturn(correctHash);
        when(mockRs.getInt("failed_login_attempts")).thenReturn(0);

        when(mockDao.getEmployeeById("E001")).thenReturn(new com.revworkforce.model.Employee());

        boolean result = AuthService.login("E001", "password");
        Assertions.assertTrue(result);

        verify(mockDao).recordSuccessfulLogin("E001");
    }

    @Test
    public void testLogin_Failure_WrongPassword() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockDao.getAuthDetails("E001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("account_locked")).thenReturn(0);
        String correctHash = PasswordUtil.hashPassword("password");
        when(mockRs.getString("password_hash")).thenReturn(correctHash);

        boolean result = AuthService.login("E001", "wrongpass");
        Assertions.assertFalse(result);

        verify(mockDao).recordFailedLogin("E001");
    }

    @Test
    public void testLogin_AccountLocked() throws Exception {
        ResultSet mockRs = Mockito.mock(ResultSet.class);
        when(mockDao.getAuthDetails("E001")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("account_locked")).thenReturn(1);

        boolean result = AuthService.login("E001", "password");
        Assertions.assertFalse(result);
    }
}
