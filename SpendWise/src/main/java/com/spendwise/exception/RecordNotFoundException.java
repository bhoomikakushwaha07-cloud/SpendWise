package com.spendwise.exception;

/**
 * Thrown when the user tries to view, delete, or update a record
 * (transaction, budget, subscription) whose ID does not exist.
 */
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
