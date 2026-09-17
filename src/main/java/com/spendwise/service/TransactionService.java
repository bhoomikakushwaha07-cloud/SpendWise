package com.spendwise.service;

import com.spendwise.exception.RecordNotFoundException;
import com.spendwise.model.Transaction;
import com.spendwise.model.TransactionType;
import com.spendwise.storage.TransactionStorage;
import com.spendwise.util.IdGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MODULE 1 - Transaction Management.
 *
 * Holds all transactions in memory (an ArrayList - a Java Collection)
 * and offers add / view / search / delete / total operations. Every
 * mutating method immediately persists the change to CSV, so the
 * in-memory list and the file on disk never drift apart.
 */
public class TransactionService {

    private final List<Transaction> transactions;
    private final TransactionStorage storage;
    private final IdGenerator idGenerator = new IdGenerator("T");

    public TransactionService(TransactionStorage storage) throws IOException {
        this.storage = storage;
        this.transactions = storage.loadAll();
        for (Transaction t : transactions) {
            idGenerator.reportExistingId(t.getId());
        }
    }

    public Transaction addTransaction(TransactionType type, double amount, String category,
                                       LocalDate date, String description) throws IOException {
        Transaction transaction = new Transaction(idGenerator.nextId(), type, amount, category, date, description);
        transactions.add(transaction);
        persist();
        return transaction;
    }

    /** Returns a defensive copy sorted by date, oldest first. */
    public List<Transaction> viewAll() {
        List<Transaction> copy = new ArrayList<>(transactions);
        copy.sort(Comparator.comparing(Transaction::getDate));
        return copy;
    }

    public List<Transaction> sortByAmountDescending() {
        List<Transaction> copy = new ArrayList<>(transactions);
        copy.sort(Comparator.comparingDouble(Transaction::getAmount).reversed());
        return copy;
    }

    /** Linear search across category and description - simple and easy to explain in a viva. */
    public List<Transaction> search(String keyword) {
        List<Transaction> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Transaction t : transactions) {
            if (t.getCategory().toLowerCase().contains(lowerKeyword)
                    || t.getDescription().toLowerCase().contains(lowerKeyword)
                    || t.getId().toLowerCase().contains(lowerKeyword)) {
                results.add(t);
            }
        }
        return results;
    }

    public List<Transaction> filterByCategory(String category) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory().equalsIgnoreCase(category)) {
                results.add(t);
            }
        }
        return results;
    }

    public void deleteById(String id) throws RecordNotFoundException, IOException {
        Transaction toRemove = null;
        for (Transaction t : transactions) {
            if (t.getId().equalsIgnoreCase(id)) {
                toRemove = t;
                break;
            }
        }
        if (toRemove == null) {
            throw new RecordNotFoundException("No transaction found with ID " + id);
        }
        transactions.remove(toRemove);
        persist();
    }

    public double getTotalIncome() {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getTotalExpense() {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getNetBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    /** Total expenses recorded under one category - used by the Budget module. */
    public double getSpendingForCategory(String category) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE && t.getCategory().equalsIgnoreCase(category)) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public List<Transaction> getAllExpenseTransactions() {
        List<Transaction> results = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                results.add(t);
            }
        }
        return results;
    }

    public int count() {
        return transactions.size();
    }

    private void persist() throws IOException {
        storage.saveAll(transactions);
    }
}
