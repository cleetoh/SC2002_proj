package com.internship.system.view;

import com.internship.system.controller.AuthController;
import com.internship.system.controller.StaffController;
import com.internship.system.model.Application;
import com.internship.system.model.FilterCriteria;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.util.ConsoleInput;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * View class for career center staff interface.
 * Handles display and user interaction for managing approvals and reports.
 */
public class StaffView {
    /** Controller for staff operations. */
    private final StaffController staffController;
    /** Controller for authentication. */
    private final AuthController authController;
    /** Current filter criteria for viewing internships. */
    private FilterCriteria filterCriteria = FilterCriteria.builder().build();

    /**
     * Constructs a new StaffView with the specified controllers.
     *
     * @param staffController the staff controller
     * @param authController  the authentication controller
     */
    public StaffView(StaffController staffController, AuthController authController) {
        this.staffController = staffController;
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
                case 1 -> handleManagePendingReps();
                case 2 -> handleManagePendingInternships();
                case 3 -> handleManageWithdrawalRequests();
                case 4 -> handleGenerateReport();
                case 5 -> handleSetFilters();
                case 6 -> {
                    handleChangePassword();
                    if (authController.getCurrentUser().isEmpty()) {
                        running = false;
                    }
                }
                case 7 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        CareerCenterStaff staff = staffController.getCurrentStaff();
        System.out.println();
        System.out.println("==== Career Center Staff Menu ====");
        System.out.println("Logged in as: " + staff.getName() + " (" + staff.getUserId() + ")");
        System.out.println("1. Manage Pending Company Representatives");
        System.out.println("2. Manage Pending Internships");
        System.out.println("3. Manage Withdrawal Requests");
        System.out.println("4. View Internships");
        System.out.println("5. Set/Update Filters");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
    }

    private void handleManagePendingReps() {
        boolean managing = true;
        while (managing) {
            List<CompanyRepresentative> reps = staffController.viewPendingReps();
            if (reps.isEmpty()) {
                System.out.println();
                System.out.println("No pending representatives.");
                System.out.println();
                return;
            }

            System.out.println();
            System.out.println("--- Pending Representatives ---");
            for (CompanyRepresentative rep : reps) {
                System.out.printf("ID: %s | Name: %s | Company: %s | Department: %s | Position: %s%n",
                        rep.getUserId(), rep.getName(), rep.getCompanyName(), rep.getDepartment(), rep.getPosition());
            }

            System.out.println();
            String repId;

            while (true) {
                repId = ConsoleInput.readLine("Enter Representative ID to process (or press Enter to go back): ");
                if (repId.isBlank()) {
                    System.out.println("Returning to main menu.");
                    return;
                }

                final String finalRepId = repId;
                boolean validId = reps.stream()
                        .anyMatch(rep -> rep.getUserId().equals(finalRepId));

                if (validId) {
                    break;
                } else {
                    System.out.println("Invalid Representative ID. Please try again.");
                }
            }

            System.out.println();
            System.out.println("1. Approve Representative");
            System.out.println("2. Reject Representative");
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

            boolean success = false;
            switch (choice) {
                case 1 -> {
                    success = staffController.approveCompanyRep(repId);
                    System.out.println();
                    if (success) {
                        System.out.println("Representative approved.");
                    } else {
                        System.out.println("Unable to approve representative. Check the ID.");
                    }
                    System.out.println();
                }
                case 2 -> {
                    success = staffController.rejectCompanyRep(repId);
                    System.out.println();
                    if (success) {
                        System.out.println("Representative marked as not approved.");
                    } else {
                        System.out.println("Unable to reject representative. Check the ID.");
                    }
                    System.out.println();
                }
                default -> {
                    System.out.println();
                    System.out.println("Invalid option. Operation cancelled.");
                    System.out.println();
                }
            }
        }
    }

    private void handleManagePendingInternships() {
        boolean managing = true;
        while (managing) {
            List<Internship> internships = staffController.viewPendingInternships();
            if (internships.isEmpty()) {
                System.out.println();
                System.out.println("No pending internships.");
                System.out.println();
                return;
            }

            System.out.println();
            System.out.println("--- Pending Internships ---");
            for (Internship internship : internships) {
                System.out.printf("ID: %d | %s | Company: %s | Level: %s | Preferred Major: %s%n",
                        internship.getInternshipId(),
                        internship.getTitle(),
                        internship.getCompanyName(),
                        internship.getLevel(),
                        internship.getPreferredMajor());
            }

            System.out.println();
            int internshipId;

            while (true) {
                String input = ConsoleInput.readLine("Enter Internship ID to process (or press Enter to go back): ");
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

            System.out.println();
            System.out.println("1. Approve Internship");
            System.out.println("2. Reject Internship");
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

            boolean success = false;
            switch (choice) {
                case 1 -> {
                    success = staffController.approveInternship(internshipId);
                    System.out.println();
                    if (success) {
                        System.out.println("Internship approved and made visible.");
                    } else {
                        System.out.println("Unable to approve internship. Check the ID.");
                    }
                    System.out.println();
                }
                case 2 -> {
                    success = staffController.rejectInternship(internshipId);
                    System.out.println();
                    if (success) {
                        System.out.println("Internship rejected.");
                    } else {
                        System.out.println("Unable to reject internship. Check the ID.");
                    }
                    System.out.println();
                }
                default -> {
                    System.out.println();
                    System.out.println("Invalid option. Operation cancelled.");
                    System.out.println();
                }
            }
        }
    }

    private void handleManageWithdrawalRequests() {
        boolean managing = true;
        while (managing) {
            List<Application> requests = staffController.getPendingWithdrawalRequests();
            if (requests.isEmpty()) {
                System.out.println();
                System.out.println("No withdrawal requests at the moment.");
                System.out.println();
                return;
            }

            System.out.println();
            System.out.println("--- Withdrawal Requests ---");
            for (Application application : requests) {
                String companyName = staffController.getCompanyNameForInternship(application.getInternshipId());
                String title = staffController.getTitleForInternship(application.getInternshipId());
                String studentName = staffController.getStudentName(application.getStudentId());
                String studentMajor = staffController.getStudentMajor(application.getStudentId());
                int studentYear = staffController.getStudentYearOfStudy(application.getStudentId());
                System.out.printf(
                        "Application ID: %d | Student ID: %s | Student Name: %s | Major: %s | Year: %s | Internship ID: %d | Title: %s | Company: %s | Status: %s%n",
                        application.getApplicationId(),
                        application.getStudentId(),
                        studentName,
                        studentMajor,
                        studentYear > 0 ? String.valueOf(studentYear) : "Unknown",
                        application.getInternshipId(),
                        title,
                        companyName,
                        application.getStatus());
            }

            System.out.println();
            int applicationId;

            while (true) {
                String input = ConsoleInput.readLine("Enter Application ID to process (or press Enter to go back): ");
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
                boolean validId = requests.stream()
                        .anyMatch(application -> application.getApplicationId() == finalApplicationId);

                if (validId) {
                    break;
                } else {
                    System.out.println("Invalid Application ID. Please try again.");
                }
            }

            System.out.println();
            System.out.println("1. Approve Withdrawal");
            System.out.println("2. Reject Withdrawal");
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

            boolean success = false;
            switch (choice) {
                case 1 -> {
                    success = staffController.processWithdrawalRequest(applicationId, true);
                    System.out.println();
                    if (success) {
                        System.out.println("Withdrawal request approved.");
                    } else {
                        System.out.println("Unable to process request. Check the application ID and status.");
                    }
                    System.out.println();
                }
                case 2 -> {
                    success = staffController.processWithdrawalRequest(applicationId, false);
                    System.out.println();
                    if (success) {
                        System.out.println("Withdrawal request rejected.");
                    } else {
                        System.out.println("Unable to process request. Check the application ID and status.");
                    }
                    System.out.println();
                }
                default -> {
                    System.out.println();
                    System.out.println("Invalid option. Operation cancelled.");
                    System.out.println();
                }
            }
        }
    }

    private void handleGenerateReport() {
        List<Internship> report = staffController.generateReport(filterCriteria);
        if (report.isEmpty()) {
            System.out.println();
            System.out.println("No internships found for selected filters.");
            System.out.println();
            return;
        }

        System.out.println();
        System.out.println("--- Internship Report ---");
        for (Internship internship : report) {
            System.out.printf(
                    "ID: %d | Title: %s | Company: %s | Level: %s | Status: %s | Preferred Major: %s | Visible: %s | Closing Date: %s%n",
                    internship.getInternshipId(),
                    internship.getTitle(),
                    internship.getCompanyName(),
                    internship.getLevel(),
                    internship.getStatus(),
                    internship.getPreferredMajor(),
                    internship.isVisible() ? "Yes" : "No",
                    internship.getClosingDate() != null ? internship.getClosingDate() : "-");
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

        String company = ConsoleInput.readLine("Filter by Company Name: ");

        String dateStr = ConsoleInput.readLine("Closing Date Before (YYYY-MM-DD): ");
        LocalDate closingDate = null;
        if (!dateStr.isEmpty()) {
            try {
                closingDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        String visibilityInput = ConsoleInput.readLine("Visible Only? (y/n/blank for either): ");
        Boolean visibleOnly = null;
        if (!visibilityInput.isBlank()) {
            if (visibilityInput.equalsIgnoreCase("y")) {
                visibleOnly = true;
            } else if (visibilityInput.equalsIgnoreCase("n")) {
                visibleOnly = false;
            }
        }

        filterCriteria = FilterCriteria.builder()
                .status(status)
                .level(level)
                .preferredMajor(major.isEmpty() ? null : major)
                .companyName(company.isEmpty() ? null : company)
                .closingDateBefore(closingDate)
                .visibleOnly(visibleOnly)
                .build();
        System.out.println();
        System.out.println("Filters updated.");
        System.out.println();
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

        boolean success = authController.changePassword(staffController.getCurrentStaff(), newPassword);
        System.out.println();
        if (success) {
            System.out.println("Password updated successfully. Please login again with your new password.");
            authController.logout();
        } else {
            System.out.println("Password update failed. Please provide a non-empty value.");
        }
        System.out.println();
    }

    private LocalDate promptForDate(String prompt) {
        String input = ConsoleInput.readLine(prompt);
        if (input.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(input.trim());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Ignoring date filter.");
            return null;
        }
    }
}
