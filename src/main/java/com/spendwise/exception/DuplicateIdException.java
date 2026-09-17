package com.spendwise.exception;

/**
 * Thrown when an operation would create two records with the same ID.
 * In normal use this should never happen because IDs are generated
 * internally, but it protects the CSV-loading code against corrupted
 * or hand-edited data files.
 */
public class DuplicateIdException extends Exception {

    public DuplicateIdException(String message) {
        super(message);
    }
}
