package com.spendwise;

import com.spendwise.exception.InvalidInputException;
import com.spendwise.exception.RecordNotFoundException;
import com.spendwise.model.*;
import com.spendwise.service.BudgetService;
import com.spendwise.service.ReportService;
import com.spendwise.service.SubscriptionService;
import com.spendwise.service.TransactionService;
import com.spendwise.storage.BudgetStorage;
import com.spendwise.storage.SubscriptionStorage;
import com.spendwise.storage.TransactionStorage;
import com.spendwise.util.InputValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * SpendWise - Personal Finance & Subscription Manager
 * Console entry point: shows menus, reads user input, calls the
 * service layer, and prints results. Main.java intentionally contains
 * NO business logic of its own - that all lives in the service
 * classes, which keeps this class focused purely on user interaction.
 */
public class Main {

    private static final String DATA_DIR = "data";
    private static final Scanner scanner = new Scanner(System.in);

    private static TransactionService transactionService;
    private static BudgetService budgetService;
    private static SubscriptionService subscriptionService;
    private static ReportService reportService;

    public static void main(String[] args) {
        printBanner();
        if (!initializeServices()) {
            return;
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readMenuChoice();
            try {
                switch (choice) {
                    case 1 -> transactionMenu();
                    case 2 -> budgetMenu();
                    case 3 -> subscriptionMenu();
                    case 4 -> reportsMenu();
                    case 0 -> {
                        System.out.println("\nThank you for using SpendWise. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please choose a number from the menu.");
                }
            } catch (Exception e) {
                // Last-resort safety net: the app should never crash on bad input.
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static boolean initializeServices() {
        try {
            TransactionStorage transactionStorage = new TransactionStorage(DATA_DIR + "/transactions.csv");
            BudgetStorage budgetStorage = new BudgetStorage(DATA_DIR + "/budgets.csv");
            SubscriptionStorage subscriptionStorage = new SubscriptionStorage(DATA_DIR + "/subscriptions.csv");

            transactionService = new TransactionService(transactionStorage);
            budgetService = new BudgetService(budgetStorage, transactionService);
            subscriptionService = new SubscriptionService(subscriptionStorage);
            reportService = new ReportService(transactionService, budgetService, subscriptionService);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to start SpendWise: could not load data files.");
            System.out.println("Reason: " + e.getMessage());
            return false;
        }
    }

    private static void printBanner() {
        System.out.println("=========================================");
        System.out.println("   SpendWise - Personal Finance Manager  ");
        System.out.println("=========================================");
    }

    private static void printMainMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. Transaction Management");
        System.out.println("2. Budget Management");
        System.out.println("3. Subscription Management");
        System.out.println("4. Financial Reports");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    // ----------------------------------------------------------------
    // MODULE 1: TRANSACTION MANAGEMENT
    // ----------------------------------------------------------------

    private static void transactionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------- Transaction Management -------");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View All Transactions");
            System.out.println("4. Search Transactions");
            System.out.println("5. Filter by Category");
            System.out.println("6. Sort by Amount (highest first)");
            System.out.println("7. Delete Transaction");
            System.out.println("8. View Totals (Income / Expense / Balance)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readMenuChoice();
            switch (choice) {
                case 1 -> addTransaction(TransactionType.INCOME);
                case 2 -> addTransaction(TransactionType.EXPENSE);
                case 3 -> printTransactions(transactionService.viewAll());
                case 4 -> {
                    System.out.print("Enter search keyword (category/description/ID): ");
                    printTransactions(transactionService.search(readLine()));
                }
                case 5 -> {
                    System.out.print("Enter category: ");
                    printTransactions(transactionService.filterByCategory(readLine()));
                }
                case 6 -> printTransactions(transactionService.sortByAmountDescending());
                case 7 -> deleteTransaction();
                case 8 -> printTotals();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void addTransaction(TransactionType type) {
        try {
            System.out.print("Amount (Rs.): ");
            double amount = InputValidator.parsePositiveAmount(readLine());

            System.out.print("Category (e.g. Food, Transport, Education, Entertainment, "
                    + "Shopping, Bills, Health, Salary, Other): ");
            String category = InputValidator.requireNonEmpty(readLine(), "Category");

            System.out.print("Date (yyyy-MM-dd), or press Enter for today: ");
            String dateInput = readLine();
            LocalDate date = dateInput.isBlank() ? LocalDate.now() : InputValidator.parseDate(dateInput);

            System.out.print("Description: ");
            String description = InputValidator.requireNonEmpty(readLine(), "Description");

            Transaction t = transactionService.addTransaction(type, amount, category, date, description);
            System.out.println("Saved: " + t);
        } catch (InvalidInputException e) {
            System.out.println("Could not add transaction: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not save transaction: " + e.getMessage());
        }
    }

    private static void deleteTransaction() {
        try {
            System.out.print("Enter transaction ID to delete: ");
            String id = readLine();
            transactionService.deleteById(id);
            System.out.println("Transaction " + id + " deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not delete transaction: " + e.getMessage());
        }
    }

    private static void printTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("\nID     TYPE     CATEGORY      AMOUNT        DATE         DESCRIPTION");
        System.out.println("---------------------------------------------------------------------");
        for (Transaction t : transactions) {
            System.out.println(t.getSummaryLine());
        }
    }

    private static void printTotals() {
        System.out.printf("Total Income   : Rs.%.2f%n", transactionService.getTotalIncome());
        System.out.printf("Total Expense  : Rs.%.2f%n", transactionService.getTotalExpense());
        System.out.printf("Net Balance    : Rs.%.2f%n", transactionService.getNetBalance());
    }

    // ----------------------------------------------------------------
    // MODULE 2: BUDGET MANAGEMENT
    // ----------------------------------------------------------------

    private static void budgetMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--------- Budget Management ---------");
            System.out.println("1. Set Budget for a Category");
            System.out.println("2. View All Budgets");
            System.out.println("3. Check Budget Utilization (one category)");
            System.out.println("4. Check Utilization for All Budgets");
            System.out.println("5. Delete a Budget");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readMenuChoice();
            switch (choice) {
                case 1 -> setBudget();
                case 2 -> printBudgets(budgetService.viewAll());
                case 3 -> checkOneBudget();
                case 4 -> printAllBudgetStatuses();
                case 5 -> deleteBudget();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void setBudget() {
        try {
            System.out.print("Category: ");
            String category = InputValidator.requireNonEmpty(readLine(), "Category");
            System.out.print("Monthly budget limit (Rs.): ");
            double limit = InputValidator.parsePositiveAmount(readLine());
            budgetService.setBudget(category, limit);
            System.out.println("Budget set: " + category + " = Rs." + limit);
        } catch (InvalidInputException e) {
            System.out.println("Could not set budget: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not save budget: " + e.getMessage());
        }
    }

    private static void printBudgets(List<Budget> budgets) {
        if (budgets.isEmpty()) {
            System.out.println("No budgets set yet.");
            return;
        }
        for (Budget b : budgets) {
            System.out.println(b);
        }
    }

    private static void checkOneBudget() {
        try {
            System.out.print("Category: ");
            String category = readLine();
            printBudgetStatus(budgetService.checkUtilization(category));
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printAllBudgetStatuses() {
        List<BudgetService.BudgetStatus> statuses = budgetService.checkAllUtilization();
        if (statuses.isEmpty()) {
            System.out.println("No budgets set yet.");
            return;
        }
        for (BudgetService.BudgetStatus s : statuses) {
            printBudgetStatus(s);
        }
    }

    private static void printBudgetStatus(BudgetService.BudgetStatus s) {
        System.out.println();
        System.out.println(s.getBudget().getCategory() + " Budget : Rs." + s.getBudget().getLimitAmount());
        System.out.printf("%s Spending : Rs.%.2f%n", s.getBudget().getCategory(), s.getSpent());
        System.out.printf("Budget Used : %.0f%%%n", s.getPercentUsed());
        if (s.isExceeded()) {
            System.out.println("ALERT: You have EXCEEDED your " + s.getBudget().getCategory() + " budget!");
        } else if (s.isWarning()) {
            System.out.printf("Warning: You have used %.0f%% of your %s budget.%n",
                    s.getPercentUsed(), s.getBudget().getCategory());
        } else {
            System.out.println("You are within budget.");
        }
    }

    private static void deleteBudget() {
        try {
            System.out.print("Category to remove budget for: ");
            budgetService.deleteBudget(readLine());
            System.out.println("Budget removed.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not delete budget: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // MODULE 3: SUBSCRIPTION MANAGEMENT
    // ----------------------------------------------------------------

    private static void subscriptionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------- Subscription Management -------");
            System.out.println("1. Add Subscription");
            System.out.println("2. View All Subscriptions");
            System.out.println("3. View Active Subscriptions Only");
            System.out.println("4. Search Subscriptions");
            System.out.println("5. Cancel a Subscription");
            System.out.println("6. Delete a Subscription");
            System.out.println("7. View Total Monthly / Yearly Cost");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readMenuChoice();
            switch (choice) {
                case 1 -> addSubscription();
                case 2 -> printSubscriptions(subscriptionService.viewAll());
                case 3 -> printSubscriptions(subscriptionService.viewActive());
                case 4 -> {
                    System.out.print("Enter search keyword (name/category): ");
                    printSubscriptions(subscriptionService.search(readLine()));
                }
                case 5 -> cancelSubscription();
                case 6 -> deleteSubscription();
                case 7 -> System.out.printf("Total Monthly Cost: Rs.%.2f%nTotal Yearly Cost : Rs.%.2f%n",
                        subscriptionService.getTotalMonthlyCost(), subscriptionService.getTotalYearlyCost());
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void addSubscription() {
        try {
            System.out.print("Subscription name (e.g. Netflix): ");
            String name = InputValidator.requireNonEmpty(readLine(), "Name");

            System.out.print("Amount charged per cycle (Rs.): ");
            double amount = InputValidator.parsePositiveAmount(readLine());

            System.out.print("Billing cycle (WEEKLY / MONTHLY / QUARTERLY / YEARLY): ");
            BillingCycle cycle = parseBillingCycle(readLine());

            System.out.print("Category (e.g. Entertainment, Bills, Health): ");
            String category = InputValidator.requireNonEmpty(readLine(), "Category");

            System.out.print("Start date (yyyy-MM-dd), or press Enter for today: ");
            String dateInput = readLine();
            LocalDate startDate = dateInput.isBlank() ? LocalDate.now() : InputValidator.parseDate(dateInput);

            Subscription s = subscriptionService.addSubscription(name, amount, cycle, category, startDate);
            System.out.println("Saved: " + s);
        } catch (InvalidInputException e) {
            System.out.println("Could not add subscription: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not save subscription: " + e.getMessage());
        }
    }

    private static BillingCycle parseBillingCycle(String raw) throws InvalidInputException {
        try {
            return BillingCycle.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new InvalidInputException("Billing cycle must be one of WEEKLY, MONTHLY, QUARTERLY, YEARLY.");
        }
    }

    private static void printSubscriptions(List<Subscription> subs) {
        if (subs.isEmpty()) {
            System.out.println("No subscriptions found.");
            return;
        }
        for (Subscription s : subs) {
            System.out.println(s.getSummaryLine());
        }
    }

    private static void cancelSubscription() {
        try {
            System.out.print("Subscription ID to cancel: ");
            subscriptionService.cancelSubscription(readLine());
            System.out.println("Subscription cancelled.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not cancel subscription: " + e.getMessage());
        }
    }

    private static void deleteSubscription() {
        try {
            System.out.print("Subscription ID to delete: ");
            subscriptionService.deleteById(readLine());
            System.out.println("Subscription deleted.");
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Could not delete subscription: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // MODULE 4: FINANCIAL REPORTS
    // ----------------------------------------------------------------

    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n----------- Financial Reports -----------");
            System.out.println("1. Spending by Category");
            System.out.println("2. Combined Activity Feed (Transactions + Subscriptions)");
            System.out.println("3. Monthly Financial Outlook");
            System.out.println("4. Budget Status Summary");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readMenuChoice();
            switch (choice) {
                case 1 -> printSpendingByCategory();
                case 2 -> printCombinedFeed();
                case 3 -> printMonthlyOutlook();
                case 4 -> printAllBudgetStatuses();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void printSpendingByCategory() {
        Map<String, Double> byCategory = reportService.spendingByCategory();
        if (byCategory.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        System.out.println("\nCategory       Total Spent");
        System.out.println("---------------------------");
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            System.out.printf("%-14s Rs.%.2f%n", entry.getKey(), entry.getValue());
        }
    }

    private static void printCombinedFeed() {
        List<Reportable> feed = reportService.combinedActivityFeed();
        if (feed.isEmpty()) {
            System.out.println("No activity recorded yet.");
            return;
        }
        // Polymorphism in action: every item might be a Transaction or a
        // Subscription, but we call the same method on all of them.
        for (Reportable item : feed) {
            System.out.println(item.getSummaryLine());
        }
    }

    private static void printMonthlyOutlook() {
        System.out.printf("Total Income             : Rs.%.2f%n", reportService.getTotalIncome());
        System.out.printf("Total Expenses            : Rs.%.2f%n", reportService.getTotalExpense());
        System.out.printf("Recurring Subscription Cost: Rs.%.2f / month%n", reportService.getSubscriptionMonthlyCost());
        System.out.printf("Estimated Monthly Outlook : Rs.%.2f%n", reportService.getOverallMonthlyOutlook());
        if (reportService.getOverallMonthlyOutlook() < 0) {
            System.out.println("Note: Your recorded outflow currently exceeds your recorded income.");
        }
    }

    // ----------------------------------------------------------------
    // Small shared helpers
    // ----------------------------------------------------------------

    private static String readLine() {
        return scanner.nextLine().trim();
    }

    private static int readMenuChoice() {
        String raw = scanner.nextLine();
        try {
            return InputValidator.parseMenuChoice(raw);
        } catch (InvalidInputException e) {
            return -1; // triggers "Invalid option" in the calling menu
        }
    }
}
