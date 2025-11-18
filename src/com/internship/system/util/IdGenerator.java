package com.internship.system.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates unique sequential IDs for different entity types.
 * Maintains separate counters for each entity type (e.g., "internship", "application").
 */
public class IdGenerator {
    /** Map of entity type to current counter value. */
    private final Map<String, Integer> counters = new HashMap<>();

    /**
     * Generates the next ID for the specified entity type.
     * Thread-safe operation.
     *
     * @param key the entity type (e.g., "internship", "application")
     * @return the next sequential ID
     */
    public synchronized int next(String key) {
        int current = counters.getOrDefault(key, 0) + 1;
        counters.put(key, current);
        return current;
    }

    /**
     * Seeds the counter for a given entity type with a minimum value.
     * Useful when loading existing data to ensure IDs don't conflict.
     * Thread-safe operation.
     *
     * @param key the entity type
     * @param value the minimum value to set (only sets if higher than current)
     */
    public synchronized void seed(String key, int value) {
        counters.put(key, Math.max(counters.getOrDefault(key, 0), value));
    }
}
