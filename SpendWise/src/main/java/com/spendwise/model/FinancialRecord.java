package com.spendwise.model;

import java.time.LocalDate;

/**
 * Common shape shared by every kind of money-related record in
 * SpendWise. Transaction (a one-time entry) and Subscription (a
 * recurring charge) both extend this class.
 *
 * Demonstrates: abstraction + inheritance. FinancialRecord itself is
 * never instantiated directly (note the "abstract" keyword) - it only
 * exists so that shared fields (id, amount, date, description) and
 * shared behaviour live in one place instead of being duplicated.
 *
 * getMonthlyImpact() is declared here but has no body: each subclass
 * MUST provide its own implementation (method overriding), because a
 * one-time transaction and a recurring subscription affect a monthly
 * budget in very different ways.
 */
public abstract class FinancialRecord {

    protected String id;
    protected double amount;
    protected LocalDate date;
    protected String description;

    protected FinancialRecord(String id, double amount, LocalDate date, String description) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    /**
     * How much this record contributes to a typical month's cash flow.
     * Positive = adds money, negative = removes money.
     * Implemented differently by Transaction and Subscription
     * (polymorphism: same method name, different behaviour).
     */
    public abstract double getMonthlyImpact();

    /** Short label used in reports, e.g. "TRANSACTION" or "SUBSCRIPTION". */
    public abstract String getRecordType();

    // ---- Encapsulation: private-ish state, controlled access via getters/setters ----

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s | Rs.%.2f | %s | %s", id, amount, date, description);
    }
}
