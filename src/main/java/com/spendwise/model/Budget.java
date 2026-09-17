package com.spendwise.model;

/**
 * A spending limit the user sets for one expense category
 * (e.g. Food = Rs.5000 per month).
 *
 * Budget deliberately does NOT extend FinancialRecord: it is not a
 * record of money that moved, it is a target/ceiling. Keeping it
 * separate keeps the class hierarchy honest instead of forcing an
 * inheritance relationship that doesn't really make sense.
 */
public class Budget {

    private String category;
    private double limitAmount;

    public Budget(String category, double limitAmount) {
        this.category = category;
        this.limitAmount = limitAmount;
    }

    public String getCategory() {
        return category;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    @Override
    public String toString() {
        return String.format("%-12s | Limit: Rs.%.2f", category, limitAmount);
    }
}
