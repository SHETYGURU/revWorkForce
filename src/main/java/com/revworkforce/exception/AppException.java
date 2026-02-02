package com.revworkforce.exception;

/**
 * Custom runtime exception for the RevWorkForce application.
 * Used to wrap checked exceptions (like SQLException) or business rule violations.
 */
public class AppException extends RuntimeException {

    /**
     * Constructs a new AppException with the specified detail message.
     *
     * @param message The detail message.
     */
    public AppException(String message) {
        super(message);
    }

    /**
     * Constructs a new AppException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause (which is saved for later retrieval by the getCause() method).
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
