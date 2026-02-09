package com.revworkforce.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;

class UtilTest {

    @Test
    void testValidationUtil_Email() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.uk"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
    }

    @Test
    void testValidationUtil_Phone() {
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertFalse(ValidationUtil.isValidPhone("123"));
        assertFalse(ValidationUtil.isValidPhone("abcdefghij"));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    void testPasswordUtil() {
        String password = "securePassword";
        String hash = PasswordUtil.hashPassword(password);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$")); // BCrypt prefix
        assertTrue(PasswordUtil.verifyPassword(password, hash));
        assertFalse(PasswordUtil.verifyPassword("wrongPassword", hash));
    }

    @Test
    void testDateUtil() {
        Date date = DateUtil.getCurrentDate();
        assertNotNull(date);
        assertEquals(LocalDate.now().toString(), date.toString());

        String formatted = DateUtil.formatDate(date);
        assertNotNull(formatted); // Assuming format is YYYY-MM-DD or similar
    }
}
