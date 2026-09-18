package com.slms.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Minimal one-way password hashing (SHA-256) so plaintext passwords are never
 * written to disk. Satisfies the project's "security" non-functional
 * requirement at a level appropriate for an academic console application.
 */
public final class PasswordUtil {

    private PasswordUtil() { }

    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("Hashing algorithm unavailable", e);
        }
    }

    public static boolean matches(String plainText, String hash) {
        return hash.equals(hash(plainText));
    }
}
