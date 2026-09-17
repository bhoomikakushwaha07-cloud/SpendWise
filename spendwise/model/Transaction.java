package com.spendwise.model;

import java.time.LocalDate;

/**
 * A single income or expense entry recorded by the user.
 * Extends FinancialRecord (inheritance) and implements Reportable
 * so it can appear in generic report listings (interfaces).
 */
public class Transaction extends FinancialRecord implements Reportable {

    private TransactionType type;
    private String category;

    public Transaction(String id, TransactionType type, double amount,
                        String category, LocalDate date, String description) {
        super(id, amount, date, description);
        this.type = type;
        this.category = category;
    }

    @Override
    public double getMonthlyImpact() {
        // A one-time transaction affects the month it happened in:
        // income adds to cash flow, expense subtracts from it.
        return (type == TransactionType.INCOME) ? amount : -amount;
    }

    @Override
    public String getRecordType() {
        return "TRANSACTION";
    }

    @Override
    public String getSummaryLine() {
        return String.format("[%s] %-7s | %-12s | Rs.%10.2f | %s | %s",
                id, type, category, amount, date, description);
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %-12s Rs.%.2f on %s - %s",
                id, type, category, amount, date, description);
    }
}
