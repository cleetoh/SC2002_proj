package com.internship.system.util;

import java.util.Scanner;

public final class ConsoleInput {
    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleInput() {
    }

    public static String readLine(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        return SCANNER.nextLine().trim();
    }

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
