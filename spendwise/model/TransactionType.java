package com.spendwise.model;

/**
 * The two kinds of money movement SpendWise tracks.
 * Using an enum (instead of a raw String or int) means the compiler
 * stops us from ever creating a transaction with an invalid type.
 */
public enum TransactionType {
    INCOME,
    EXPENSE
}
