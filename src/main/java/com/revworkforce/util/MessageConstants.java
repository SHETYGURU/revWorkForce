package com.revworkforce.util;

/**
 * centralized constants for messages used throughout the application.
 * Helps in avoiding duplicate string literals and maintaining consistency.
 */
public final class MessageConstants {

    private MessageConstants() {
        // Private constructor to prevent instantiation
    }

    // Error Prefixes
    public static final String UNABLE_TO_FETCH_PREFIX = "Unable to fetch ";
    public static final String ERROR_FETCHING_PREFIX = "Error fetching ";
    public static final String ERROR_LISTING_PREFIX = "Error listing ";
    public static final String FAILED_TO_FETCH_PREFIX = "Failed to fetch ";

    // Input Validation
    public static final String INVALID_NUMBER_INPUT = "Invalid input! Please enter a valid number.";
    public static final String INVALID_DATE_FORMAT = "Invalid date format. Please use YYYY-MM-DD.";
}
