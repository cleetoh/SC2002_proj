package com.internship.system.view;

import com.internship.system.controller.AuthController;
import com.internship.system.controller.CompanyController;
import com.internship.system.model.Application;
import com.internship.system.model.FilterCriteria;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.util.ConsoleInput;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyView {
    private final CompanyController companyController;
    private final AuthController authController;
    private FilterCriteria filterCriteria = FilterCriteria.builder().build();

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
                case 1 -> handleManageInternships();
                case 2 -> handleCreateInternship();
                case 3 -> handleManageApplications();
                case 4 -> handleSetFilters();
                case 5 -> handleChangePassword();
                case 6 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        CompanyRepresentative rep = companyController.getCurrentRep();
        System.out.println();
        System.out.println("==== Company Representative Menu ====");
        System.out.println("Logged in as: " + rep.getName() + " (" + rep.getUserId() + ")");
        System.out.println("1. View and Manage Company Internships");
        System.out.println("2. Create Internship");
        System.out.println("3. View and Process Applications");
        System.out.println("4. Set/Update Filters");
        System.out.println("5. Change Password");
        System.out.println("6. Logout");
    }

    private void handleManageInternships() {
        boolean managing = true;
        while (managing) {
            List<Internship> internships = companyController.getInternships(filterCriteria);
            if (internships.isEmpty()) {
                System.out.println();
                System.out.println("No internships match the current filters.");
                System.out.println();
                return;
            }

            System.out.println();
            System.out.println("--- Company Internships ---");
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

            System.out.println();
            int internshipId;

            // Loop until a valid ID is entered or user cancels
            while (true) {
                String input = ConsoleInput.readLine("Enter Internship ID to manage (or press Enter to go back): ");
                if (input.isBlank()) {
                    System.out.println("Returning to main menu.");
                    return;
                }

                // Try to parse the input as an integer
                try {
                    internshipId = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Internship ID. Please enter a valid number.");
                    continue;
                }

                // Validate the internship ID
                final int finalInternshipId = internshipId;
                boolean validId = internships.stream()
                        .anyMatch(internship -> internship.getInternshipId() == finalInternshipId);

                if (validId) {
                    break;
                } else {
                    System.out.println("Invalid Internship ID. Please try again.");
                }
            }

            System.out.println();
            System.out.println("1. Update Internship Details");
            System.out.println("2. Toggle Visibility");
            System.out.println();
            String actionInput = ConsoleInput.readLine("Select action (or press Enter to cancel): ");
            if (actionInput.isBlank()) {
                System.out.println("Operation cancelled.");
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(actionInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Operation cancelled.");
                continue;
            }

            switch (choice) {
                case 1 -> handleUpdateInternshipDetails(internshipId);
                case 2 -> handleToggleVisibilityForId(internshipId, internships);
                default -> System.out.println("Invalid option. Operation cancelled.");
            }
        }
    }

    private void handleUpdateInternshipDetails(int internshipId) {
        String title = ConsoleInput.readLine("New Title: ");
        String description = ConsoleInput.readLine("New Description: ");
        InternshipLevel level = promptForLevel();
        String preferredMajor = ConsoleInput.readLine("Preferred Major: ");
        LocalDate openingDate = promptForDate("Opening Date (YYYY-MM-DD, leave blank for none): ");
        LocalDate closingDate = promptForDate("Closing Date (YYYY-MM-DD, leave blank for none): ");
        int slots = promptForSlots();

        boolean success = companyController.updateInternship(internshipId, title, description, level, preferredMajor,
                openingDate, closingDate, slots);
        System.out.println();
        if (success) {
            System.out.println("Internship updated successfully.");
        } else {
            System.out.println("Failed to update internship. Ensure it exists, belongs to you, and is not filled.");
        }
        System.out.println();
    }

    private void handleToggleVisibilityForId(int internshipId, List<Internship> internships) {
        // Find the internship to check its current visibility
        Internship internship = internships.stream()
                .filter(i -> i.getInternshipId() == internshipId)
                .findFirst()
                .orElse(null);

        if (internship == null) {
            System.out.println("Unable to find internship.");
            return;
        }

        boolean wasVisible = internship.isVisible();
        boolean success = companyController.toggleInternshipVisibility(internshipId);

        System.out.println();
        if (success) {
            String newVisibility = wasVisible ? "not visible" : "visible";
            System.out.println("Visibility updated. Internship ID" + internshipId + " is now " + newVisibility + ".");
        } else {
            System.out.println("Unable to toggle visibility. Ensure internship ID" + internshipId
                    + " is approved and owned by you.");
        }
        System.out.println();
    }

    private void handleSetFilters() {
        System.out.println();
        System.out.println("--- Set Internship Filters ---");
        System.out.println("Enter new values or leave blank to keep current filter.");
        System.out.println();

        String statusStr = ConsoleInput.readLine("Filter by Status (PENDING, APPROVED, REJECTED, FILLED): ")
                .toUpperCase();
        InternshipStatus status = statusStr.isEmpty() ? null : InternshipStatus.valueOf(statusStr);

        String levelStr = ConsoleInput.readLine("Filter by Level (BASIC, ADVANCED): ").toUpperCase();
        InternshipLevel level = levelStr.isEmpty() ? null : InternshipLevel.valueOf(levelStr);

        String major = ConsoleInput.readLine("Filter by Preferred Major: ");

        String dateStr = ConsoleInput.readLine("Closing Date Before (YYYY-MM-DD): ");
        LocalDate closingDate = null;
        if (!dateStr.isEmpty()) {
            try {
                closingDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        filterCriteria = FilterCriteria.builder()
                .status(status)
                .level(level)
                .preferredMajor(major.isEmpty() ? null : major)
                .closingDateBefore(closingDate)
                .build();
        System.out.println();
        System.out.println("Filters updated.");
        System.out.println();
    }

    private void handleCreateInternship() {
        if (!companyController.canCreateMoreInternships()) {
            System.out.println();
            System.out.println("Internship limit reached. You may only have up to 5 active internships.");
            System.out.println();
            return;
        }

        System.out.println();
        String title = ConsoleInput.readLine("Title: ");
        String description = ConsoleInput.readLine("Description: ");
        InternshipLevel level = promptForLevel();
        String preferredMajor = ConsoleInput.readLine("Preferred Major: ");
        LocalDate openingDate = promptForDate("Opening Date (YYYY-MM-DD, leave blank for none): ");
        LocalDate closingDate = promptForDate("Closing Date (YYYY-MM-DD, leave blank for none): ");
        int slots = promptForSlots();

        companyController.createInternship(title, description, level, preferredMajor, openingDate, closingDate, slots)
                .ifPresentOrElse(
                        internship -> {
                            System.out.println();
                            System.out.println("Internship created with ID: " + internship.getInternshipId());
                            System.out.println();
                        },
                        () -> {
                            System.out.println();
                            System.out.println("Failed to create internship. Check inputs and limits.");
                            System.out.println();
                        });
    }

    private void handleManageApplications() {
        boolean managing = true;
        while (managing) {
            // Get all internships for this company
            List<Internship> internships = companyController.getInternships(FilterCriteria.builder().build());
            if (internships.isEmpty()) {
                System.out.println();
                System.out.println("No internships found for your company.");
                System.out.println();
                return;
            }

            // Collect all applications across all internships, sorted by internship
            List<Application> allApplications = new ArrayList<>();
            Map<Integer, String> internshipTitles = new HashMap<>();

            for (Internship internship : internships) {
                List<Application> applications = companyController
                        .viewApplicationsForInternship(internship.getInternshipId());
                allApplications.addAll(applications);
                internshipTitles.put(internship.getInternshipId(), internship.getTitle());
            }

            if (allApplications.isEmpty()) {
                System.out.println();
                System.out.println("No applications found for any of your internships.");
                System.out.println();
                return;
            }

            // Sort by internship ID
            allApplications.sort(Comparator.comparingInt(Application::getInternshipId));

            System.out.println();
            System.out.println("--- All Applications (Sorted by Internship) ---");
            int currentInternshipId = -1;
            for (Application application : allApplications) {
                if (application.getInternshipId() != currentInternshipId) {
                    currentInternshipId = application.getInternshipId();
                    System.out.println("\n[Internship ID: " + currentInternshipId + " - " +
                            internshipTitles.get(currentInternshipId) + "]");
                }
                System.out.printf("  App ID: %d | Student ID: %s | Status: %s | Withdrawal Req: %s%n",
                        application.getApplicationId(),
                        application.getStudentId(),
                        application.getStatus(),
                        application.isWithdrawalRequested() ? "Yes" : "No");
            }

            System.out.println();
            int applicationId;

            // Loop until a valid ID is entered or user cancels
            while (true) {
                String input = ConsoleInput.readLine("Enter Application ID to process (or press Enter to go back): ");
                if (input.isBlank()) {
                    System.out.println("Returning to main menu.");
                    return;
                }

                // Try to parse the input as an integer
                try {
                    applicationId = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Application ID. Please enter a valid number.");
                    continue;
                }

                // Validate the application ID
                final int finalApplicationId = applicationId;
                boolean validId = allApplications.stream()
                        .anyMatch(application -> application.getApplicationId() == finalApplicationId);

                if (validId) {
                    break;
                } else {
                    System.out.println("Invalid Application ID. Please try again.");
                }
            }

            System.out.println();
            System.out.println("1. Approve Application (Offer Extended)");
            System.out.println("2. Reject Application");
            System.out.println();
            String actionInput = ConsoleInput.readLine("Select action (or press Enter to cancel): ");
            if (actionInput.isBlank()) {
                System.out.println("Operation cancelled.");
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(actionInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Operation cancelled.");
                continue;
            }

            ApplicationStatus newStatus = switch (choice) {
                case 1 -> ApplicationStatus.SUCCESSFUL_PENDING;
                case 2 -> ApplicationStatus.UNSUCCESSFUL;
                default -> null;
            };

            if (newStatus == null) {
                System.out.println("Operation cancelled.");
                continue;
            }

            boolean success = companyController.processApplication(applicationId, newStatus);
            System.out.println();
            if (success) {
                System.out.println("Application updated to " + newStatus + ".");
            } else {
                System.out.println("Unable to update application. Check ownership, slots, or IDs.");
            }
            System.out.println();
        }
    }

    private void handleChangePassword() {
        System.out.println();
        String newPassword = ConsoleInput.readLine("Enter new password (or press Enter to cancel): ");
        if (newPassword.isBlank()) {
            System.out.println();
            System.out.println("Operation cancelled.");
            System.out.println();
            return;
        }

        boolean success = authController.changePassword(companyController.getCurrentRep(), newPassword);
        System.out.println();
        if (success) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Password update failed. Please provide a non-empty value.");
        }
        System.out.println();
    }

    private InternshipLevel promptForLevel() {
        System.out.println();
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
