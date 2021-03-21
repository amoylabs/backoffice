package com.bn.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordUtilsTest {
    @Test
    void testGetSaltValue() {
        int length = 30;
        String actual = PasswordUtils.getSalt(length);
        assertNotNull(actual);
        assertEquals(length, actual.length());
    }

    @Test
    void testPasswordGeneration() {
        String pwd = "foo_bar";
        String salt = PasswordUtils.getSalt(30);
        String hashedPwd = assertDoesNotThrow(() -> PasswordUtils.generateSecurePassword(pwd, salt));
        assertNotNull(hashedPwd);
        assertTrue(PasswordUtils.verifyPassword(pwd, hashedPwd, salt));
    }
}
