package com.internship.system.view;

import com.internship.system.controller.StudentController;
import com.internship.system.controller.AuthController;
import com.internship.system.model.Application;
import com.internship.system.model.FilterCriteria;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.user.Student;
import com.internship.system.util.ConsoleInput;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * View class for student interface.
 * Handles display and user interaction for browsing and applying to internships.
 */
public class StudentView {
    /** Controller for student operations. */
    private final StudentController studentController;
    /** Controller for authentication. */
    private final AuthController authController;
    /** Current filter criteria for viewing internships. */
    private FilterCriteria filterCriteria = FilterCriteria.builder().build();

    /**
     * Constructs a new StudentView with the specified controllers.
     *
     * @param studentController the student controller
     * @param authController the authentication controller
     */
    public StudentView(StudentController studentController, AuthController authController) {
        this.studentController = studentController;
        this.authController = authController;
    }

    public void run() {
        boolean running = true;
        while (running) {

            if (authController.getCurrentUser().isEmpty()) {
                running = false;
                break;
            }
            displayMenu();
            int choice = ConsoleInput.readInt("Select an option: ");
            switch (choice) {
                case 1 -> handleBrowseAndApply();
                case 2 -> handleManageApplications();
                case 3 -> handleSetFilters();
                case 4 -> {
                    handleChangePassword();
                    if (authController.getCurrentUser().isEmpty()) {
                        running = false;
                    }
                }
                case 5 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        Student student = studentController.getCurrentStudent();
        System.out.println();
        System.out.println("==== Student Menu ====");
        System.out.println("Logged in as: " + student.getName() + " (" + student.getUserId() + ")");
        System.out.println("1. Browse and Apply for Internships");
        System.out.println("2. View and Manage My Applications");
        System.out.println("3. Set/Update Filters");
        System.out.println("4. Change Password");
        System.out.println("5. Logout");
    }

    private void handleBrowseAndApply() {
        boolean browsing = true;
        while (browsing) {
            List<Internship> internships = studentController.getVisibleInternships(filterCriteria);
            if (internships.isEmpty()) {
                System.out.println();
                System.out.println("No internships available based on your profile and selected filters.");
                System.out.println();
                return;
            }

            System.out.println();
            System.out.println("--- Available Internships ---");
            for (Internship internship : internships) {
                System.out.printf("ID: %d | %s | Level: %s | Major: %s | Slots: %d%n",
                        internship.getInternshipId(),
                        internship.getTitle(),
                        internship.getLevel(),
                        internship.getPreferredMajor(),
                        internship.getSlots() - internship.getConfirmedOffers());
            }

            System.out.println();
            int internshipId;

            while (true) {
                String input = ConsoleInput.readLine("Enter Internship ID to apply (or press Enter to go back): ");
                if (input.isBlank()) {
                    System.out.println("Returning to main menu.");
                    return;
                }

                try {
                    internshipId = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Internship ID. Please enter a valid number.");
                    continue;
                }

                final int finalInternshipId = internshipId;
                boolean validId = internships.stream()
                        .anyMatch(internship -> internship.getInternshipId() == finalInternshipId);

                if (validId) {
                    break;
                } else {
                    System.out.println("Invalid Internship ID. Please try again.");
                }
            }

            boolean success = studentController.applyForInternship(internshipId);
            System.out.println();
            if (success) {
                System.out.println("Application submitted successfully.");
            } else {
                System.out.println(
                        "Unable to submit application. Check eligibility, limits, or internship availability.");
            }
            System.out.println();
        }
    }

    private void handleSetFilters() {
        System.out.println();
        System.out.println("--- Set Internship Filters ---");
        System.out.println("Enter new values or leave blank to keep current filter.");
        System.out.println();

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
                .level(level)
                .preferredMajor(major.isEmpty() ? null : major)
                .closingDateBefore(closingDate)
                .build();
        System.out.println();
        System.out.println("Filters updated.");
        System.out.println();
    }

    private void handleManageApplications() {
        boolean managing = true;
        while (managing) {
            List<Application> applications = studentController.viewAppliedInternships();
            if (applications.isEmpty()) {
                System.out.println();
                System.out.println("You have not submitted any applications yet.");
                System.out.println();
                return;
            }

            System.out.println();
            System.out.println("--- My Applications ---");
            for (Application application : applications) {
                System.out.printf(
                        "Application ID: %d | Internship ID: %d | Status: %s | Withdrawal Requested: %s%n",
                        application.getApplicationId(),
                        application.getInternshipId(),
                        application.getStatus(),
                        application.isWithdrawalRequested() ? "Yes" : "No");
            }

            System.out.println();
            int applicationId;

            while (true) {
                String input = ConsoleInput.readLine("Enter Application ID to manage (or press Enter to go back): ");
                if (input.isBlank()) {
                    System.out.println("Returning to main menu.");
                    return;
                }

                try {
                    applicationId = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Application ID. Please enter a valid number.");
                    continue;
                }

                final int finalApplicationId = applicationId;
                boolean validId = applications.stream()
                        .anyMatch(application -> application.getApplicationId() == finalApplicationId);

                if (validId) {
                    break;
                } else {
                    System.out.println("Invalid Application ID. Please try again.");
                }
            }

            final int finalApplicationIdForFilter = applicationId;
            Application selectedApplication = applications.stream()
                    .filter(app -> app.getApplicationId() == finalApplicationIdForFilter)
                    .findFirst()
                    .orElse(null);

            if (selectedApplication == null) {
                System.out.println("Unable to find application.");
                continue;
            }

            ApplicationStatus status = selectedApplication.getStatus();
            boolean canAcceptRejectOffer = status == ApplicationStatus.SUCCESSFUL_PENDING;
            boolean canWithdraw = status == ApplicationStatus.SUCCESSFUL_ACCEPTED
                    && !selectedApplication.isWithdrawalRequested();

            if (!canAcceptRejectOffer && !canWithdraw) {
                System.out.println();
                System.out.println("No actions available for this application.");
                System.out.println();
                continue;
            }

            System.out.println();
            System.out.println("--- Available Actions ---");
            int optionNumber = 1;
            int acceptOption = -1;
            int rejectOption = -1;
            int withdrawOption = -1;

            if (canAcceptRejectOffer) {
                acceptOption = optionNumber;
                System.out.println(optionNumber + ". Accept Offer");
                optionNumber++;

                rejectOption = optionNumber;
                System.out.println(optionNumber + ". Reject Offer");
                optionNumber++;
            }

            if (canWithdraw) {
                withdrawOption = optionNumber;
                System.out.println(optionNumber + ". Request Withdrawal");
                optionNumber++;
            }

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

            System.out.println();
            if (choice == acceptOption) {
                boolean success = studentController.acceptOffer(applicationId);
                if (success) {
                    System.out.println("Offer accepted successfully. Other pending applications withdrawn.");
                } else {
                    System.out.println(
                            "Unable to accept offer. Ensure the application has an offer pending.");
                }
            } else if (choice == rejectOption) {
                boolean success = studentController.rejectOffer(applicationId);
                if (success) {
                    System.out.println("Offer rejected successfully.");
                } else {
                    System.out.println("Unable to reject offer. Ensure the application has an offer pending.");
                }
            } else if (choice == withdrawOption) {
                boolean success = studentController.withdrawApplication(applicationId);
                if (success) {
                    System.out.println("Withdrawal request submitted. Await staff approval.");
                } else {
                    System.out.println(
                            "Unable to request withdrawal. Ensure the application exists and is not already withdrawn.");
                }
            } else {
                System.out.println("Invalid option. Operation cancelled.");
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

        boolean success = authController.changePassword(studentController.getCurrentStudent(), newPassword);
        System.out.println();
        if (success) {
            System.out.println("Password updated successfully. Please login again with your new password.");
            authController.logout();
        } else {
            System.out.println("Password update failed. Please provide a non-empty value.");
        }
        System.out.println();
    }
}
