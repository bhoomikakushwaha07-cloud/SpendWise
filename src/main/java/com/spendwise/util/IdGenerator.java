package com.spendwise.util;

/**
 * Produces simple, human-readable, sequential IDs such as T0001, B0001,
 * S0001. Each service owns one IdGenerator instance for its own record
 * type, seeded from the highest ID already found on disk so that IDs
 * never collide after restarting the program.
 */
public class IdGenerator {

    private final String prefix;
    private int counter;

    public IdGenerator(String prefix) {
        this(prefix, 0);
    }

    public IdGenerator(String prefix, int startingCounter) {
        this.prefix = prefix;
        this.counter = startingCounter;
    }

    /** Call this once while loading existing records to avoid duplicate IDs. */
    public void reportExistingId(String existingId) {
        String numericPart = existingId.replace(prefix, "");
        try {
            int n = Integer.parseInt(numericPart);
            if (n > counter) {
                counter = n;
            }
        } catch (NumberFormatException ignored) {
            // Any ID that doesn't match our pattern is simply ignored.
        }
    }

    public String nextId() {
        counter++;
        return String.format("%s%04d", prefix, counter);
    }
}
