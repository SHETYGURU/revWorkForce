/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.util;

import com.revworkforce.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Utility class for Input Validations.
 * Provides static methods to validate emails, phone numbers, and required
 * fields.
 * 
 * @author Gururaj Shetty
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    /**
     * Validates that a string input is not null or empty.
     * 
     * @param input     The input string.
     * @param fieldName The name of the field for error reporting.
     * @throws ValidationException if input is invalid.
     */
    public static void validateNotEmpty(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format.");
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Invalid phone number. Must be 10 digits.");
        }
    }

    public static void validatePositive(double number, String fieldName) {
        if (number < 0) {
            throw new ValidationException(fieldName + " must be positive.");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}
