/*
 * Developed by Gururaj Shetty
 */
package com.revworkforce.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for Password Hashing and Verification.
 * Wraps JBCrypt for secure password handling.
 * 
 * @author Gururaj Shetty
 */
public class PasswordUtil {

    private PasswordUtil() {
        // Private constructor to prevent instantiation
    }

    private static final int WORK_FACTOR = 12;

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
