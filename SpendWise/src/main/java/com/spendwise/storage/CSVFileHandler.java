package com.spendwise.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Low-level file I/O helper shared by every *Storage class.
 * It knows nothing about Transactions, Budgets, or Subscriptions -
 * it only knows how to turn a text file into rows of Strings and back.
 * This separation (generic file handling vs. domain-specific storage)
 * is what makes the storage package modular.
 */
public class CSVFileHandler {

    private static final String DELIMITER = ",";

    /**
     * Reads every line of a CSV file into a list of String arrays.
     * If the file does not exist yet, an empty list is returned
     * (a brand-new SpendWise install starts with no data, not a crash).
     */
    public List<String[]> readAll(String filePath, boolean hasHeader) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return rows; // nothing saved yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine && hasHeader) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;
                if (line.trim().isEmpty()) {
                    continue;
                }
                rows.add(line.split(DELIMITER, -1));
            }
        }
        return rows;
    }

    /**
     * Overwrites the file with the given rows. Used after every
     * add/delete/update so the CSV on disk always matches memory.
     */
    public void writeAll(String filePath, String header, List<String[]> rows) throws IOException {
        ensureParentDirectoryExists(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }
            for (String[] row : rows) {
                writer.write(String.join(DELIMITER, row));
                writer.newLine();
            }
        }
    }

    private void ensureParentDirectoryExists(String filePath) throws IOException {
        Path parent = Path.of(filePath).toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
