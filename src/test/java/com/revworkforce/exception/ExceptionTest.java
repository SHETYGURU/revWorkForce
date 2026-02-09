package com.revworkforce.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testAppException() {
        AppException ex = new AppException("Error occurred");
        assertEquals("Error occurred", ex.getMessage());

        Exception cause = new RuntimeException("Cause");
        AppException ex2 = new AppException("Error with cause", cause);
        assertEquals("Error with cause", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testValidationException() {
        ValidationException ex = new ValidationException("Invalid input");
        assertEquals("Invalid input", ex.getMessage());
    }
}
