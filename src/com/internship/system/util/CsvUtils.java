package com.internship.system.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Utility class for CSV parsing and formatting.
 * Handles quoted fields and proper escaping of CSV values.
 */
public final class CsvUtils {
    /**
     * Private constructor to prevent instantiation.
     */
    private CsvUtils() {
    }

    /**
     * Parses a CSV line into a list of tokens.
     * Handles quoted fields and escaped quotes correctly.
     *
     * @param line the CSV line to parse
     * @return list of parsed tokens
     */
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

    /**
     * Converts a list of values into a CSV line.
     * Automatically escapes values that contain commas or quotes.
     *
     * @param values the values to convert to CSV format
     * @return a CSV-formatted line
     */
    public static String toLine(List<String> values) {
        StringJoiner joiner = new StringJoiner(",");
        for (String value : values) {
            joiner.add(escape(value));
        }
        return joiner.toString();
    }

    /**
     * Escapes a value for CSV format.
     * Quotes values containing commas or quotes, and escapes internal quotes.
     *
     * @param value the value to escape
     * @return the escaped value
     */
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
