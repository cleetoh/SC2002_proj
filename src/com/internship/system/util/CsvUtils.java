package com.internship.system.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public final class CsvUtils {
    private CsvUtils() {
    }

    public static List<String> parseLine(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null || line.isEmpty()) {
            return tokens;
        }
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString().trim());
        return tokens;
    }

    public static String toLine(List<String> values) {
        StringJoiner joiner = new StringJoiner(",");
        for (String value : values) {
            joiner.add(escape(value));
        }
        return joiner.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.replace("\"", "\"\"");
        if (sanitized.contains(",") || sanitized.contains("\"")) {
            return '"' + sanitized + '"';
        }
        return sanitized;
    }
}
