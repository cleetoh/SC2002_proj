package com.internship.system.util;

import java.util.HashMap;
import java.util.Map;

public class IdGenerator {
    private final Map<String, Integer> counters = new HashMap<>();

    public synchronized int next(String key) {
        int current = counters.getOrDefault(key, 0) + 1;
        counters.put(key, current);
        return current;
    }

    public synchronized void seed(String key, int value) {
        counters.put(key, Math.max(counters.getOrDefault(key, 0), value));
    }
}
