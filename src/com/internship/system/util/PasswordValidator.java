package com.internship.system.util;

public class PasswordValidator {

    private static final String DEFAULT_PASSWORD = "password";

    public static boolean isValidPassword(String password) {
        return password != null && !password.isBlank();
    }

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }

    public static boolean matches(String storedPassword, String inputPassword) {
        if (storedPassword == null || inputPassword == null) {
            return false;
        }
        return storedPassword.equals(inputPassword);
    }
}