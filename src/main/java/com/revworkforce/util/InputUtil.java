/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.util;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class to handle Console Input operations effectively.
 * Prevents scanner resource leaks and handles basic type parsing.
 * 
 * @author Gururaj Shetty
 */
public class InputUtil {

    private static final Logger logger = LogManager.getLogger(InputUtil.class);

    private InputUtil() {
        // Private constructor to prevent instantiation
    }

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Sets the scanner instance. Useful for testing.
     * 
     * @param s The scanner to use.
     */
    public static void setScanner(Scanner s) {
        scanner = s;
    }

    /**
     * Reads a string input from the console.
     *
     * @param prompt The message to display to the user.
     * @return The trimed string input.
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Reads an integer input from the console.
     * Handles invalid number formats gracefully by re-prompting.
     *
     * @param prompt The message to display to the user.
     * @return The valid integer input.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                logger.warn(MessageConstants.INVALID_NUMBER_INPUT);
            }
        }
    }

    /**
     * Reads a string input from the console and validates it using a predicate.
     * Retries until valid input is provided.
     *
     * @param prompt       The message to display to the user.
     * @param validator    A predicate that returns true if the input is valid.
     * @param errorMessage The error message to display if validation fails.
     * @return The validated string input.
     */
    public static String readValidatedString(String prompt, java.util.function.Predicate<String> validator,
            String errorMessage) {
        while (true) {
            String input = readString(prompt);
            if (validator.test(input)) {
                return input;
            }
            logger.warn(errorMessage);
        }
    }

    /**
     * Reads a string input from the console and validates it using a function.
     * The function should return null (or optional empty-like behavior) if valid,
     * or an error string if invalid.
     * 
     * @param prompt    The message to display to the user.
     * @param validator A function that returns null if valid, or an error message
     *                  string if invalid.
     * @return The validated string input.
     */
    public static String readValidatedString(String prompt, java.util.function.Function<String, String> validator) {
        while (true) {
            String input = readString(prompt);
            String error = validator.apply(input);
            if (error == null) {
                return input;
            }
            logger.warn(error);
        }
    }

    /**
     * Closes the scanner resource.
     * Should be called only when the application is shutting down.
     */
    public static void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
