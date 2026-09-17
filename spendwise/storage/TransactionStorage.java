package com.spendwise.storage;

import com.spendwise.model.Transaction;
import com.spendwise.model.TransactionType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts Transaction objects to/from CSV rows and reads/writes
 * data/transactions.csv through the generic CSVFileHandler.
 *
 * CSV columns: id,type,amount,category,date,description
 */
public class TransactionStorage {

    private static final String HEADER = "id,type,amount,category,date,description";
    private final String filePath;
    private final CSVFileHandler fileHandler = new CSVFileHandler();

    public TransactionStorage(String filePath) {
        this.filePath = filePath;
    }

    public List<Transaction> loadAll() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        List<String[]> rows = fileHandler.readAll(filePath, true);

        for (String[] row : rows) {
            if (row.length < 6) {
                continue; // skip malformed/corrupted rows instead of crashing
            }
            String id = row[0];
            TransactionType type = TransactionType.valueOf(row[1]);
            double amount = Double.parseDouble(row[2]);
            String category = row[3];
            LocalDate date = LocalDate.parse(row[4]);
            String description = row[5];
            transactions.add(new Transaction(id, type, amount, category, date, description));
        }
        return transactions;
    }

    public void saveAll(List<Transaction> transactions) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : transactions) {
            rows.add(new String[]{
                    t.getId(),
                    t.getType().name(),
                    String.valueOf(t.getAmount()),
                    t.getCategory(),
                    t.getDate().toString(),
                    t.getDescription()
            });
        }
        fileHandler.writeAll(filePath, HEADER, rows);
    }
}
