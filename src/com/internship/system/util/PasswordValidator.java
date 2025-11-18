package com.internship.system.util;

/**
 * Utility class for password validation and management.
 * Provides methods to validate passwords and compare them.
 */
public class PasswordValidator {

    /** Default password used for new accounts. */
    private static final String DEFAULT_PASSWORD = "password";

    /**
     * Validates that a password is not null or blank.
     *
     * @param password the password to validate
     * @return true if the password is valid (non-null and non-blank), false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && !password.isBlank();
    }

    /**
     * Gets the default password for new accounts.
     *
     * @return the default password string
     */
    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }

    /**
     * Compares a stored password with an input password.
     *
     * @param storedPassword the stored password
     * @param inputPassword the input password to compare
     * @return true if passwords match, false otherwise (including if either is null)
     */
    public static boolean matches(String storedPassword, String inputPassword) {
        if (storedPassword == null || inputPassword == null) {
            return false;
        }
        return storedPassword.equals(inputPassword);
    }
}