package com.revworkforce.util;

import com.revworkforce.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationUtilTest {

    @Test
    public void testValidateNotEmpty_Valid() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validateNotEmpty("test", "Field"));
    }

    @Test
    public void testValidateNotEmpty_Empty() {
        Exception exception = Assertions.assertThrows(ValidationException.class,
                () -> ValidationUtil.validateNotEmpty("", "Field"));
        Assertions.assertEquals("Field cannot be empty.", exception.getMessage());
    }

    @Test
    public void testValidateNotEmpty_Null() {
        Assertions.assertThrows(ValidationException.class, () -> ValidationUtil.validateNotEmpty(null, "Field"));
    }

    @Test
    public void testValidateEmail_Valid() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validateEmail("test@example.com"));
    }

    @Test
    public void testValidateEmail_Invalid() {
        Assertions.assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail("invalid-email"));
    }

    @Test
    public void testValidatePhone_Valid() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validatePhone("1234567890"));
    }

    @Test
    public void testValidatePhone_Invalid() {
        Assertions.assertThrows(ValidationException.class, () -> ValidationUtil.validatePhone("123"));
    }

    @Test
    public void testValidatePositive_Valid() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validatePositive(100, "Salary"));
    }

    @Test
    public void testValidatePositive_Negative() {
        Assertions.assertThrows(ValidationException.class, () -> ValidationUtil.validatePositive(-50, "Salary"));
    }

    @Test
    public void testIsValidEmail_True() {
        Assertions.assertTrue(ValidationUtil.isValidEmail("test@example.com"));
    }

    @Test
    public void testIsValidEmail_False() {
        Assertions.assertFalse(ValidationUtil.isValidEmail("invalid"));
        Assertions.assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testIsValidPhone_True() {
        Assertions.assertTrue(ValidationUtil.isValidPhone("1234567890"));
    }

    @Test
    public void testIsValidPhone_False() {
        Assertions.assertFalse(ValidationUtil.isValidPhone("123"));
        Assertions.assertFalse(ValidationUtil.isValidPhone(null));
    }
}
