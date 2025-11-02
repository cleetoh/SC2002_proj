package com.internship.system.view;

import com.internship.system.controller.AuthController;
import com.internship.system.controller.CompanyController;
import com.internship.system.model.Application;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.util.ConsoleInput;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CompanyView {
    private final CompanyController companyController;
    private final AuthController authController;

    public CompanyView(CompanyController companyController, AuthController authController) {
        this.companyController = companyController;
        this.authController = authController;
    }

    public void run() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = ConsoleInput.readInt("Select an option: ");
            switch (choice) {
                case 1 -> showMyInternships();
                case 2 -> handleCreateInternship();
                case 3 -> handleUpdateInternship();
                case 4 -> handleToggleVisibility();
                case 5 -> handleViewApplications();
                case 6 -> handleProcessApplication();
                case 7 -> handleChangePassword();
                case 8 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        CompanyRepresentative rep = companyController.getCurrentRep();
        System.out.println();
        System.out.println("==== Company Representative Menu ====");
        System.out.println("Logged in as: " + rep.getName() + " (" + rep.getUserId() + ")");
        System.out.println("1. View My Internships");
        System.out.println("2. Create Internship");
        System.out.println("3. Update Internship");
        System.out.println("4. Toggle Internship Visibility");
        System.out.println("5. View Internship Applications");
        System.out.println("6. Process Application");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
    }

    private void showMyInternships() {
        List<Internship> internships = companyController.viewMyInternships();
        if (internships.isEmpty()) {
            System.out.println("You have not created any internships yet.");
            return;
        }
        System.out.println("--- My Internships ---");
        for (Internship internship : internships) {
            System.out.printf("ID: %d | %s | Level: %s | Status: %s | Visible: %s | Slots: %d | Confirmed: %d%n",
                    internship.getInternshipId(),
                    internship.getTitle(),
                    internship.getLevel(),
                    internship.getStatus(),
                    internship.isVisible() ? "Yes" : "No",
                    internship.getSlots(),
                    internship.getConfirmedOffers());
        }
    }

    private void handleCreateInternship() {
        if (!companyController.canCreateMoreInternships()) {
            System.out.println("Internship limit reached. You may only have up to 5 active internships.");
            return;
        }
        String title = ConsoleInput.readLine("Title: ");
        String description = ConsoleInput.readLine("Description: ");
        InternshipLevel level = promptForLevel();
        String preferredMajor = ConsoleInput.readLine("Preferred Major: ");
        LocalDate openingDate = promptForDate("Opening Date (YYYY-MM-DD, leave blank for none): ");
        LocalDate closingDate = promptForDate("Closing Date (YYYY-MM-DD, leave blank for none): ");
        int slots = promptForSlots();

        companyController.createInternship(title, description, level, preferredMajor, openingDate, closingDate, slots)
                .ifPresentOrElse(
                        internship -> System.out.println("Internship created with ID: " + internship.getInternshipId()),
                        () -> System.out.println("Failed to create internship. Check inputs and limits."));
    }

    private void handleUpdateInternship() {
        int internshipId = ConsoleInput.readInt("Enter Internship ID to update: ");
        String title = ConsoleInput.readLine("New Title: ");
        String description = ConsoleInput.readLine("New Description: ");
        InternshipLevel level = promptForLevel();
        String preferredMajor = ConsoleInput.readLine("Preferred Major: ");
        LocalDate openingDate = promptForDate("Opening Date (YYYY-MM-DD, leave blank for none): ");
        LocalDate closingDate = promptForDate("Closing Date (YYYY-MM-DD, leave blank for none): ");
        int slots = promptForSlots();

        boolean success = companyController.updateInternship(internshipId, title, description, level, preferredMajor,
                openingDate, closingDate, slots);
        if (success) {
            System.out.println("Internship updated successfully.");
        } else {
            System.out.println("Failed to update internship. Ensure it exists, belongs to you, and is not filled.");
        }
    }

    private void handleToggleVisibility() {
        int internshipId = ConsoleInput.readInt("Enter Internship ID to toggle visibility: ");
        boolean success = companyController.toggleInternshipVisibility(internshipId);
        if (success) {
            System.out.println("Visibility updated.");
        } else {
            System.out.println("Unable to toggle visibility. Ensure internship is approved and owned by you.");
        }
    }

    private void handleViewApplications() {
        int internshipId = ConsoleInput.readInt("Enter Internship ID to view applications: ");
        List<Application> applications = companyController.viewApplicationsForInternship(internshipId);
        if (applications.isEmpty()) {
            System.out.println("No applications for this internship or invalid internship.");
            return;
        }
        System.out.println("--- Applications ---");
        for (Application application : applications) {
            System.out.printf(
                    "Application ID: %d | Student ID: %s | Status: %s | Withdrawal Requested: %s | Accepted: %s%n",
                    application.getApplicationId(),
                    application.getStudentId(),
                    application.getStatus(),
                    application.isWithdrawalRequested() ? "Yes" : "No",
                    application.isOfferAccepted() ? "Yes" : "No");
        }
    }

    private void handleProcessApplication() {
        int applicationId = ConsoleInput.readInt("Enter Application ID to process: ");
        System.out.println("1. Mark Successful");
        System.out.println("2. Mark Unsuccessful");
        int choice = ConsoleInput.readInt("Select outcome: ");
        ApplicationStatus newStatus = switch (choice) {
            case 1 -> ApplicationStatus.SUCCESSFUL;
            case 2 -> ApplicationStatus.UNSUCCESSFUL;
            default -> null;
        };
        if (newStatus == null) {
            System.out.println("Invalid selection.");
            return;
        }
        boolean success = companyController.processApplication(applicationId, newStatus);
        if (success) {
            System.out.println("Application updated.");
        } else {
            System.out.println("Unable to update application. Check ownership, slots, or IDs.");
        }
    }

    private void handleChangePassword() {
        String newPassword = ConsoleInput.readLine("Enter new password: ");
        boolean success = authController.changePassword(companyController.getCurrentRep(), newPassword);
        if (success) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Password update failed. Please provide a non-empty value.");
        }
    }

    private InternshipLevel promptForLevel() {
        System.out.println("Select Internship Level:");
        System.out.println("1. BASIC");
        System.out.println("2. INTERMEDIATE");
        System.out.println("3. ADVANCED");
        int choice = ConsoleInput.readInt("Choice: ");
        return switch (choice) {
            case 2 -> InternshipLevel.INTERMEDIATE;
            case 3 -> InternshipLevel.ADVANCED;
            default -> InternshipLevel.BASIC;
        };
    }

    private LocalDate promptForDate(String prompt) {
        while (true) {
            String input = ConsoleInput.readLine(prompt);
            if (input.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private int promptForSlots() {
        while (true) {
            int slots = ConsoleInput.readInt("Number of slots (1-10): ");
            if (slots > 0 && slots <= 10) {
                return slots;
            }
            System.out.println("Slots must be between 1 and 10. Please try again.");
        }
    }
}
