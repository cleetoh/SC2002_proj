package com.internship.system.util;

import java.util.Scanner;

/**
 * Utility class for reading input from the console.
 * Provides methods for reading strings, integers, and yes/no responses.
 */
public final class ConsoleInput {
    /** Shared Scanner instance for reading from System.in. */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Private constructor to prevent instantiation.
     */
    private ConsoleInput() {
    }

    /**
     * Reads a line of text from the console.
     *
     * @param prompt the prompt to display (can be null or empty)
     * @return the trimmed input line
     */
    public static String readLine(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        return SCANNER.nextLine().trim();
    }

    /**
     * Reads an integer from the console.
     * Prompts repeatedly until a valid integer is entered.
     *
     * @param prompt the prompt to display
     * @return the integer value entered
     */
    public static int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number, please try again.");
            }
        }
    }

    /**
     * Reads a yes/no response from the console.
     * Prompts repeatedly until a valid response (y/yes/n/no) is entered.
     *
     * @param prompt the prompt to display
     * @return true if yes, false if no
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            String raw = readLine(prompt + " (y/n): ");
            if (raw.equalsIgnoreCase("y") || raw.equalsIgnoreCase("yes")) {
                return true;
            }
            if (raw.equalsIgnoreCase("n") || raw.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Please enter 'y' or 'n'.");
        }
    }
}
