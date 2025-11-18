package com.internship.system;

import com.internship.system.controller.AppController;

/**
 * Main entry point for the Internship Placement Management System.
 * Initializes and runs the application controller.
 */
public class Main {
    /**
     * Main method that starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        AppController appController = new AppController();
        appController.run();
    }
}
