package com.slms.util;

import com.slms.exception.InvalidInputException;

/** Static helper methods for validating user-supplied input across the app. */
public final class ValidationUtil {

    private ValidationUtil() { }

    public static void requireNonEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    public static void requirePositive(int value, String fieldName) throws InvalidInputException {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be a positive number.");
        }
    }

    public static void requireValidIsbn(String isbn) throws InvalidInputException {
        String cleaned = isbn == null ? "" : isbn.replace("-", "").trim();
        if (!cleaned.matches("\\d{10}|\\d{13}")) {
            throw new InvalidInputException("ISBN must be 10 or 13 digits.");
        }
    }

    public static int parseIntOrThrow(String value, String fieldName) throws InvalidInputException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a whole number.");
        }
    }
}
