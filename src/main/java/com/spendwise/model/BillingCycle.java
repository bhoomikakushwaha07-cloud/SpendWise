package com.spendwise.model;

/**
 * How often a subscription charges the user. Each constant carries the
 * number of times it bills per year, which lets us convert any cycle
 * into a single comparable "monthly cost" (see Subscription.getMonthlyImpact()).
 */
public enum BillingCycle {
    WEEKLY(52),
    MONTHLY(12),
    QUARTERLY(4),
    YEARLY(1);

    private final int timesPerYear;

    BillingCycle(int timesPerYear) {
        this.timesPerYear = timesPerYear;
    }

    public int getTimesPerYear() {
        return timesPerYear;
    }
}
