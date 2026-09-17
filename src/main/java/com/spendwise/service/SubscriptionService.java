package com.spendwise.service;

import com.spendwise.exception.RecordNotFoundException;
import com.spendwise.model.BillingCycle;
import com.spendwise.model.Subscription;
import com.spendwise.storage.SubscriptionStorage;
import com.spendwise.util.IdGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MODULE 3 - Subscription Management.
 *
 * Tracks recurring charges and converts them into comparable
 * monthly / yearly totals using Subscription.getMonthlyImpact(),
 * which relies on polymorphism (FinancialRecord's abstract method).
 */
public class SubscriptionService {

    private final List<Subscription> subscriptions;
    private final SubscriptionStorage storage;
    private final IdGenerator idGenerator = new IdGenerator("S");

    public SubscriptionService(SubscriptionStorage storage) throws IOException {
        this.storage = storage;
        this.subscriptions = storage.loadAll();
        for (Subscription s : subscriptions) {
            idGenerator.reportExistingId(s.getId());
        }
    }

    public Subscription addSubscription(String name, double amount, BillingCycle cycle,
                                         String category, LocalDate startDate) throws IOException {
        Subscription subscription = new Subscription(idGenerator.nextId(), name, amount, cycle,
                category, startDate, true);
        subscriptions.add(subscription);
        persist();
        return subscription;
    }

    public List<Subscription> viewAll() {
        List<Subscription> copy = new ArrayList<>(subscriptions);
        copy.sort(Comparator.comparing(Subscription::getName));
        return copy;
    }

    public List<Subscription> viewActive() {
        List<Subscription> results = new ArrayList<>();
        for (Subscription s : subscriptions) {
            if (s.isActive()) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Subscription> search(String keyword) {
        List<Subscription> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Subscription s : subscriptions) {
            if (s.getName().toLowerCase().contains(lower) || s.getCategory().toLowerCase().contains(lower)) {
                results.add(s);
            }
        }
        return results;
    }

    public void cancelSubscription(String id) throws RecordNotFoundException, IOException {
        Subscription subscription = findById(id);
        subscription.setActive(false);
        persist();
    }

    public void deleteById(String id) throws RecordNotFoundException, IOException {
        Subscription subscription = findById(id);
        subscriptions.remove(subscription);
        persist();
    }

    public Subscription findById(String id) throws RecordNotFoundException {
        for (Subscription s : subscriptions) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        throw new RecordNotFoundException("No subscription found with ID " + id);
    }

    /** Sum of getMonthlyImpact() across all active subscriptions (a negative number). */
    public double getTotalMonthlyCost() {
        double total = 0;
        for (Subscription s : subscriptions) {
            total += s.getMonthlyImpact();
        }
        return -total; // flip sign so callers get a positive "cost" figure
    }

    public double getTotalYearlyCost() {
        double total = 0;
        for (Subscription s : subscriptions) {
            total += s.getYearlyImpact();
        }
        return -total;
    }

    public int count() {
        return subscriptions.size();
    }

    private void persist() throws IOException {
        storage.saveAll(subscriptions);
    }
}
