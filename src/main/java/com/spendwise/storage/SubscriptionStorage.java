package com.spendwise.storage;

import com.spendwise.model.BillingCycle;
import com.spendwise.model.Subscription;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts Subscription objects to/from CSV rows and reads/writes
 * data/subscriptions.csv.
 *
 * CSV columns: id,name,amount,billingCycle,category,startDate,active
 */
public class SubscriptionStorage {

    private static final String HEADER = "id,name,amount,billingCycle,category,startDate,active";
    private final String filePath;
    private final CSVFileHandler fileHandler = new CSVFileHandler();

    public SubscriptionStorage(String filePath) {
        this.filePath = filePath;
    }

    public List<Subscription> loadAll() throws IOException {
        List<Subscription> subscriptions = new ArrayList<>();
        List<String[]> rows = fileHandler.readAll(filePath, true);

        for (String[] row : rows) {
            if (row.length < 7) {
                continue;
            }
            String id = row[0];
            String name = row[1];
            double amount = Double.parseDouble(row[2]);
            BillingCycle cycle = BillingCycle.valueOf(row[3]);
            String category = row[4];
            LocalDate startDate = LocalDate.parse(row[5]);
            boolean active = Boolean.parseBoolean(row[6]);
            subscriptions.add(new Subscription(id, name, amount, cycle, category, startDate, active));
        }
        return subscriptions;
    }

    public void saveAll(List<Subscription> subscriptions) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Subscription s : subscriptions) {
            rows.add(new String[]{
                    s.getId(),
                    s.getName(),
                    String.valueOf(s.getAmount()),
                    s.getBillingCycle().name(),
                    s.getCategory(),
                    s.getDate().toString(),
                    String.valueOf(s.isActive())
            });
        }
        fileHandler.writeAll(filePath, HEADER, rows);
    }
}
