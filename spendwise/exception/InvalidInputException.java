package com.spendwise.exception;

/**
 * Thrown when data typed by the user (or read from a CSV file) does not
 * meet the validation rules of the application - e.g. a negative amount,
 * an empty description, or a badly formatted date.
 *
 * Demonstrates: custom checked exceptions / exception handling.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}
