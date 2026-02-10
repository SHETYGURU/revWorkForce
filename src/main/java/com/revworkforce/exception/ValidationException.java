package com.revworkforce.exception;

/**
 * Exception thrown during input validation failures.
 * Exception thrown when input or business validation fails.
 * 
 * @author Gururaj Shetty
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
