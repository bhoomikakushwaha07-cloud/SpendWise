package com.spendwise.model;

import java.time.LocalDate;

/**
 * A recurring payment such as Netflix, a gym membership, or a SaaS tool.
 * Extends FinancialRecord (inheritance) and implements Reportable.
 */
public class Subscription extends FinancialRecord implements Reportable {

    private String name;
    private BillingCycle billingCycle;
    private String category;
    private boolean active;

    public Subscription(String id, String name, double amount, BillingCycle billingCycle,
                         String category, LocalDate startDate, boolean active) {
        super(id, amount, startDate, "Subscription: " + name);
        this.name = name;
        this.billingCycle = billingCycle;
        this.category = category;
        this.active = active;
    }

    /**
     * Converts whatever billing cycle this subscription uses into an
     * equivalent monthly cost, e.g. a yearly Rs.1200 plan becomes Rs.100/month.
     * Inactive (cancelled) subscriptions no longer cost anything.
     */
    @Override
    public double getMonthlyImpact() {
        if (!active) {
            return 0.0;
        }
        double monthlyCost = (amount * billingCycle.getTimesPerYear()) / 12.0;
        return -monthlyCost; // subscriptions are always an outflow
    }

    public double getYearlyImpact() {
        return active ? -(amount * billingCycle.getTimesPerYear()) : 0.0;
    }

    @Override
    public String getRecordType() {
        return "SUBSCRIPTION";
    }

    @Override
    public String getSummaryLine() {
        return String.format("[%s] %-15s | %-10s | Rs.%8.2f / %s | %s | %s",
                id, name, category, amount, billingCycle,
                active ? "ACTIVE" : "CANCELLED", date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("%s %-15s Rs.%.2f/%s [%s]", id, name, amount, billingCycle,
                active ? "ACTIVE" : "CANCELLED");
    }
}
