package com.revworkforce.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordUtilTest {

    @Test
    public void testHashPassword() {
        String password = "mySecretPassword";
        String hash = PasswordUtil.hashPassword(password);

        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals(password, hash);
    }

    @Test
    public void testVerifyPassword_Match() {
        String password = "password123";
        String hash = PasswordUtil.hashPassword(password);

        Assertions.assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @Test
    public void testVerifyPassword_NoMatch() {
        String password = "password123";
        String hash = PasswordUtil.hashPassword(password);

        Assertions.assertFalse(PasswordUtil.verifyPassword("wrongPassword", hash));
    }
}
