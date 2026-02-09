package com.revworkforce.util;

import com.revworkforce.exception.ValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = { "test@example.com", "user.name@domain.co", "user+label@domain.com" })
    void testIsValidEmail_Valid(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "plainaddress", "@missingusername.com", "username@.com.my", "username@domain",
            "username@domain..com" })
    void testIsValidEmail_Invalid(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1234567890", "9876543210" })
    void testIsValidPhone_Valid(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "123", "12345678901", "abcdefghij", "123-456-7890" })
    void testIsValidPhone_Invalid(String phone) {
        assertFalse(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    void testValidateNotEmpty_Invalid(String input) {
        assertThrows(ValidationException.class, () -> ValidationUtil.validateNotEmpty(input, "Field"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Valid Input", " A ", "123" })
    void testValidateNotEmpty_Valid(String input) {
        assertDoesNotThrow(() -> ValidationUtil.validateNotEmpty(input, "Field"));
    }

    @ParameterizedTest
    @CsvSource({
            "-1, Amount",
            "-0.01, Balance",
            "-100, Salary"
    })
    void testValidatePositive_Invalid(double number, String fieldName) {
        assertThrows(ValidationException.class, () -> ValidationUtil.validatePositive(number, fieldName));
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, 0.01, 100, 9999.99 })
    void testValidatePositive_Valid(double number) {
        assertDoesNotThrow(() -> ValidationUtil.validatePositive(number, "Field"));
    }
}
