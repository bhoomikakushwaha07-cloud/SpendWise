package com.spendwise.storage;

import com.spendwise.model.Budget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts Budget objects to/from CSV rows and reads/writes
 * data/budgets.csv.
 *
 * CSV columns: category,limitAmount
 */
public class BudgetStorage {

    private static final String HEADER = "category,limitAmount";
    private final String filePath;
    private final CSVFileHandler fileHandler = new CSVFileHandler();

    public BudgetStorage(String filePath) {
        this.filePath = filePath;
    }

    /** category (case-normalised) -> Budget, stored in a HashMap for O(1) lookups. */
    public Map<String, Budget> loadAll() throws IOException {
        Map<String, Budget> budgets = new LinkedHashMap<>();
        List<String[]> rows = fileHandler.readAll(filePath, true);

        for (String[] row : rows) {
            if (row.length < 2) {
                continue;
            }
            String category = row[0];
            double limit = Double.parseDouble(row[1]);
            budgets.put(category.toLowerCase(), new Budget(category, limit));
        }
        return budgets;
    }

    public void saveAll(Map<String, Budget> budgets) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Budget b : budgets.values()) {
            rows.add(new String[]{b.getCategory(), String.valueOf(b.getLimitAmount())});
        }
        fileHandler.writeAll(filePath, HEADER, rows);
    }
}
