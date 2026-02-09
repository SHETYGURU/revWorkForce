package com.revworkforce.service;

import com.revworkforce.dao.AuditLogDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.Mockito.verify;

class AuditServiceTest {

    private AuditLogDAO mockDao;

    @BeforeEach
    void setUp() throws Exception {
        mockDao = Mockito.mock(AuditLogDAO.class);
        setPrivateStaticField(AuditService.class, "dao", mockDao);
    }

    private void setPrivateStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    void testLog_ShortMethod() throws Exception {
        AuditService.log("EMP1", "UPDATE", "USERS", "101", "Changed name");
        verify(mockDao).log("EMP1", "UPDATE", "USERS", "101", "Changed name");
    }

    @Test
    void testLog_LongMethod() throws Exception {
        AuditService.log("EMP1", "INSERT", "USERS", "ColName", "102", "New User");
        verify(mockDao).log("EMP1", "INSERT", "USERS", "102", "New User");
    }

    @Test
    void testLog_Failure() throws Exception {
        Mockito.doThrow(new RuntimeException("DB Error")).when(mockDao).log(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        AuditService.log("EMP1", "UPDATE", "USERS", "101", "Changed name");

        verify(mockDao).log("EMP1", "UPDATE", "USERS", "101", "Changed name");
    }
}
