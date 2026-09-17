package com.spendwise.service;

import com.spendwise.exception.RecordNotFoundException;
import com.spendwise.model.Budget;
import com.spendwise.storage.BudgetStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MODULE 2 - Budget Management.
 *
 * Budgets are stored in a HashMap keyed by lower-cased category name,
 * which gives fast O(1) lookup when checking "does Food have a budget?"
 * instead of scanning a list every time.
 *
 * This service depends on TransactionService to find out how much has
 * actually been spent in a category - a simple example of composition
 * (one service using another) rather than inheritance.
 */
public class BudgetService {

    /** Spending at or above this fraction of the limit triggers a warning. */
    public static final double WARNING_THRESHOLD = 0.80;

    private final Map<String, Budget> budgets;
    private final BudgetStorage storage;
    private final TransactionService transactionService;

    public BudgetService(BudgetStorage storage, TransactionService transactionService) throws IOException {
        this.storage = storage;
        this.transactionService = transactionService;
        this.budgets = storage.loadAll();
    }

    public Budget setBudget(String category, double limitAmount) throws IOException {
        Budget budget = new Budget(category, limitAmount);
        budgets.put(category.toLowerCase(), budget);
        persist();
        return budget;
    }

    public List<Budget> viewAll() {
        return new ArrayList<>(budgets.values());
    }

    public Budget getBudget(String category) throws RecordNotFoundException {
        Budget budget = budgets.get(category.toLowerCase());
        if (budget == null) {
            throw new RecordNotFoundException("No budget set for category: " + category);
        }
        return budget;
    }

    public boolean hasBudget(String category) {
        return budgets.containsKey(category.toLowerCase());
    }

    public void deleteBudget(String category) throws RecordNotFoundException, IOException {
        if (!budgets.containsKey(category.toLowerCase())) {
            throw new RecordNotFoundException("No budget set for category: " + category);
        }
        budgets.remove(category.toLowerCase());
        persist();
    }

    /**
     * Compares actual spending (from TransactionService) against the
     * budget limit and returns a small report object with the
     * percentage used and whether a warning/exceeded flag should show.
     */
    public BudgetStatus checkUtilization(String category) throws RecordNotFoundException {
        Budget budget = getBudget(category);
        double spent = transactionService.getSpendingForCategory(category);
        double percentUsed = (budget.getLimitAmount() == 0) ? 0 : (spent / budget.getLimitAmount()) * 100.0;
        boolean exceeded = spent > budget.getLimitAmount();
        boolean warning = !exceeded && (percentUsed / 100.0) >= WARNING_THRESHOLD;
        return new BudgetStatus(budget, spent, percentUsed, exceeded, warning);
    }

    public List<BudgetStatus> checkAllUtilization() {
        List<BudgetStatus> statuses = new ArrayList<>();
        for (Budget b : budgets.values()) {
            try {
                statuses.add(checkUtilization(b.getCategory()));
            } catch (RecordNotFoundException ignored) {
                // cannot happen here - we are iterating existing budgets
            }
        }
        return statuses;
    }

    private void persist() throws IOException {
        storage.saveAll(budgets);
    }

    /**
     * Small, immutable, read-only result object (no setters) describing
     * how a category's spending compares to its budget. Keeping this as
     * a nested class avoids scattering tiny "report" classes everywhere.
     */
    public static class BudgetStatus {
        private final Budget budget;
        private final double spent;
        private final double percentUsed;
        private final boolean exceeded;
        private final boolean warning;

        public BudgetStatus(Budget budget, double spent, double percentUsed, boolean exceeded, boolean warning) {
            this.budget = budget;
            this.spent = spent;
            this.percentUsed = percentUsed;
            this.exceeded = exceeded;
            this.warning = warning;
        }

        public Budget getBudget() {
            return budget;
        }

        public double getSpent() {
            return spent;
        }

        public double getPercentUsed() {
            return percentUsed;
        }

        public boolean isExceeded() {
            return exceeded;
        }

        public boolean isWarning() {
            return warning;
        }
    }
}
