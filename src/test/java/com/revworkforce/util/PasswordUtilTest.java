package com.revworkforce.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordUtilTest {

    @ParameterizedTest
    @ValueSource(strings = { "mySecretPassword", "123456", "!@#$$%^" })
    void testHashPassword(String password) {
        String hash = PasswordUtil.hashPassword(password);
        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals(password, hash);
    }

    @ParameterizedTest
    @ValueSource(strings = { "password123", "admin", "securePass" })
    void testVerifyPassword_Match(String password) {
        String hash = PasswordUtil.hashPassword(password);
        Assertions.assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @ParameterizedTest
    @ValueSource(strings = { "wrong", "incorrect", "mismatch" })
    void testVerifyPassword_NoMatch(String wrongPassword) {
        String password = "correctPassword";
        String hash = PasswordUtil.hashPassword(password);
        Assertions.assertFalse(PasswordUtil.verifyPassword(wrongPassword, hash));
    }
}
