package com.internship.system.view;

import com.internship.system.model.user.User;
import com.internship.system.util.ConsoleInput;

public class MainMenuView {

    public void displaySplash() {
        System.out.println("============================================");
        System.out.println("  Internship Placement Management System");
        System.out.println("============================================");
    }

    public void displayMainOptions() {
        System.out.println();
        System.out.println("1. Login");
        System.out.println("2. Register as Company Representative");
        System.out.println("3. Exit");
    }

    public int promptForMainSelection() {
        return ConsoleInput.readInt("Select an option: ");
    }

    public String promptForUserId() {
        return ConsoleInput.readLine("Enter User ID: ");
    }

    public String promptForPassword() {
        return ConsoleInput.readLine("Enter Password: ");
    }

    public String promptForEmail() {
        return ConsoleInput.readLine("Enter Company Email (will be your login ID): ");
    }

    public String promptForName() {
        return ConsoleInput.readLine("Enter Full Name: ");
    }

    public String promptForCompanyName() {
        return ConsoleInput.readLine("Enter Company Name: ");
    }

    public String promptForDepartment() {
        return ConsoleInput.readLine("Enter Department: ");
    }

    public String promptForPosition() {
        return ConsoleInput.readLine("Enter Position: ");
    }

    public void displayLoginSuccess(User user) {
        System.out.println("Welcome, " + user.getName() + "!");
    }

    public void displayLoginFailure() {
        System.out.println("Invalid credentials or account not yet approved. Please try again.");
    }

    public void displayRegistrationSuccess() {
        System.out.println("Registration submitted. Await approval from Career Center Staff.");
    }

    public void displayRegistrationFailure(String message) {
        System.out.println("Registration failed: " + message);
    }

    public void displayExitMessage() {
        System.out.println("Goodbye!");
    }
}
