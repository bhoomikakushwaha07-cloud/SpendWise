package com.spendwise.service;

import com.spendwise.model.Reportable;
import com.spendwise.model.Transaction;
import com.spendwise.model.Subscription;

import java.util.*;

/**
 * MODULE 4 - Financial Reports.
 *
 * Reads data from the other three services (composition) and turns it
 * into human-readable summaries: totals, category breakdowns, and a
 * combined activity feed built using polymorphism - a List<Reportable>
 * can hold both Transaction and Subscription objects and call
 * getSummaryLine() on each without caring which concrete class it is.
 */
public class ReportService {

    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final SubscriptionService subscriptionService;

    public ReportService(TransactionService transactionService,
                          BudgetService budgetService,
                          SubscriptionService subscriptionService) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.subscriptionService = subscriptionService;
    }

    /** category -> total amount spent, sorted highest spend first. */
    public LinkedHashMap<String, Double> spendingByCategory() {
        Map<String, Double> totals = new HashMap<>();
        for (Transaction t : transactionService.getAllExpenseTransactions()) {
            totals.merge(t.getCategory(), t.getAmount(), Double::sum);
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<>(totals.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue())); // descending

        LinkedHashMap<String, Double> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    /**
     * Combines transactions and subscriptions into one polymorphic list,
     * sorted by date, so the report shows every financial event together.
     */
    public List<Reportable> combinedActivityFeed() {
        List<Reportable> feed = new ArrayList<>();
        feed.addAll(transactionService.viewAll());
        feed.addAll(subscriptionService.viewAll());
        return feed;
    }

    public double getOverallMonthlyOutlook() {
        double income = transactionService.getTotalIncome();
        double expenses = transactionService.getTotalExpense();
        double subscriptionCost = subscriptionService.getTotalMonthlyCost();
        return income - expenses - subscriptionCost;
    }

    public double getTotalIncome() {
        return transactionService.getTotalIncome();
    }

    public double getTotalExpense() {
        return transactionService.getTotalExpense();
    }

    public double getSubscriptionMonthlyCost() {
        return subscriptionService.getTotalMonthlyCost();
    }

    public double getSubscriptionYearlyCost() {
        return subscriptionService.getTotalYearlyCost();
    }

    public List<BudgetService.BudgetStatus> getBudgetStatuses() {
        return budgetService.checkAllUtilization();
    }
}
