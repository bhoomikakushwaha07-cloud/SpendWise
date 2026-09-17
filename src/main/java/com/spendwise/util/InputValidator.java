package com.spendwise.util;

import com.spendwise.exception.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Centralised place for every validation rule in the app.
 * Every method either returns a clean, parsed value or throws
 * InvalidInputException with a message the user can understand.
 *
 * Keeping validation here (instead of copy-pasting checks in Main.java)
 * demonstrates modular programming: one rule, one place to fix it.
 */
public final class InputValidator {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private InputValidator() {
        // utility class - never instantiated
    }

    public static double parsePositiveAmount(String raw) throws InvalidInputException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidInputException("Amount cannot be empty.");
        }
        double value;
        try {
            value = Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Amount must be a valid number.");
        }
        if (value <= 0) {
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        return value;
    }

    public static String requireNonEmpty(String raw, String fieldName) throws InvalidInputException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
        return raw.trim();
    }

    public static LocalDate parseDate(String raw) throws InvalidInputException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidInputException("Date cannot be empty.");
        }
        try {
            return LocalDate.parse(raw.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date must be in yyyy-MM-dd format, e.g. 2026-09-16.");
        }
    }

    public static int parseMenuChoice(String raw) throws InvalidInputException {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid menu number.");
        }
    }
}
