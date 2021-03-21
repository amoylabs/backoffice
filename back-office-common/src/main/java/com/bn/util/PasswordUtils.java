package com.bn.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    public static final int DEFAULT_SALT_LENGTH = 20;

    public static String getSalt() {
        return getSalt(DEFAULT_SALT_LENGTH);
    }

    public static String getSalt(int length) {
        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            salt.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(salt);
    }

    private static byte[] hash(String password, String salt) {
        char[] pwdChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        PBEKeySpec spec = new PBEKeySpec(pwdChars, saltBytes, ITERATIONS, KEY_LENGTH);
        Arrays.fill(pwdChars, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError(String.format("Error occurred while hashing a password: %s", e.getMessage()), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSecurePassword(String password, String salt) {
        byte[] securePassword = hash(password, salt);
        return Base64.getEncoder().encodeToString(securePassword);
    }

    public static boolean verifyPassword(String providedPassword, String securedPassword, String salt) {
        String newSecurePassword = generateSecurePassword(providedPassword, salt);
        return newSecurePassword.equalsIgnoreCase(securedPassword);
    }
}
