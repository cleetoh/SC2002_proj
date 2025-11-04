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

public class StaffView {
    private final StaffController staffController;
    private final AuthController authController;
    private FilterCriteria filterCriteria = FilterCriteria.builder().build();

    public StaffView(StaffController staffController, AuthController authController) {
        this.staffController = staffController;
        this.authController = authController;
    }

    public void run() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = ConsoleInput.readInt("Select an option: ");
            switch (choice) {
                case 1 -> showPendingReps();
                case 2 -> handleApproveRep();
                case 3 -> handleRejectRep();
                case 4 -> showPendingInternships();
                case 5 -> handleApproveInternship();
                case 6 -> handleRejectInternship();
                case 7 -> showWithdrawalRequests();
                case 8 -> handleWithdrawalDecision();
                case 9 -> handleGenerateReport();
                case 10 -> handleSetFilters();
                case 11 -> handleChangePassword();
                case 12 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        CareerCenterStaff staff = staffController.getCurrentStaff();
        System.out.println();
        System.out.println("==== Career Center Staff Menu ====");
        System.out.println("Logged in as: " + staff.getName() + " (" + staff.getUserId() + ")");
        System.out.println("1. View Pending Company Representatives");
        System.out.println("2. Approve Company Representative");
        System.out.println("3. Reject Company Representative");
        System.out.println("4. View Pending Internships");
        System.out.println("5. Approve Internship");
        System.out.println("6. Reject Internship");
        System.out.println("7. View Withdrawal Requests");
        System.out.println("8. Process Withdrawal Request");
        System.out.println("9. View Internships");
        System.out.println("10. Set/Update Filters");
        System.out.println("11. Change Password");
        System.out.println("12. Logout");
    }

    private void showPendingReps() {
        List<CompanyRepresentative> reps = staffController.viewPendingReps();
        if (reps.isEmpty()) {
            System.out.println("No pending representatives.");
            return;
        }
        System.out.println("--- Pending Representatives ---");
        for (CompanyRepresentative rep : reps) {
            System.out.printf("ID: %s | Name: %s | Company: %s | Department: %s | Position: %s%n",
                    rep.getUserId(), rep.getName(), rep.getCompanyName(), rep.getDepartment(), rep.getPosition());
        }
    }

    private void handleApproveRep() {
        String repId = ConsoleInput.readLine("Enter Representative ID to approve: ");
        boolean success = staffController.approveCompanyRep(repId);
        if (success) {
            System.out.println("Representative approved.");
        } else {
            System.out.println("Unable to approve representative. Check the ID.");
        }
    }

    private void handleRejectRep() {
        String repId = ConsoleInput.readLine("Enter Representative ID to reject: ");
        boolean success = staffController.rejectCompanyRep(repId);
        if (success) {
            System.out.println("Representative marked as not approved.");
        } else {
            System.out.println("Unable to reject representative. Check the ID.");
        }
    }

    private void showPendingInternships() {
        List<Internship> internships = staffController.viewPendingInternships();
        if (internships.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }
        System.out.println("--- Pending Internships ---");
        for (Internship internship : internships) {
            System.out.printf("ID: %d | %s | Company: %s | Level: %s | Preferred Major: %s%n",
                    internship.getInternshipId(),
                    internship.getTitle(),
                    internship.getCompanyName(),
                    internship.getLevel(),
                    internship.getPreferredMajor());
        }
    }

    private void handleApproveInternship() {
        int internshipId = ConsoleInput.readInt("Enter Internship ID to approve: ");
        boolean success = staffController.approveInternship(internshipId);
        if (success) {
            System.out.println("Internship approved and made visible.");
        } else {
            System.out.println("Unable to approve internship. Check the ID.");
        }
    }

    private void handleRejectInternship() {
        int internshipId = ConsoleInput.readInt("Enter Internship ID to reject: ");
        boolean success = staffController.rejectInternship(internshipId);
        if (success) {
            System.out.println("Internship rejected.");
        } else {
            System.out.println("Unable to reject internship. Check the ID.");
        }
    }

    private void showWithdrawalRequests() {
        List<Application> requests = staffController.getPendingWithdrawalRequests();
        if (requests.isEmpty()) {
            System.out.println("No withdrawal requests at the moment.");
            return;
        }
        System.out.println("--- Withdrawal Requests ---");
        for (Application application : requests) {
            System.out.printf("Application ID: %d | Student ID: %s | Internship ID: %d | Status: %s | Accepted: %s%n",
                    application.getApplicationId(),
                    application.getStudentId(),
                    application.getInternshipId(),
                    application.getStatus(),
                    application.isOfferAccepted() ? "Yes" : "No");
        }
    }

    private void handleWithdrawalDecision() {
        int applicationId = ConsoleInput.readInt("Enter Application ID: ");
        System.out.println("1. Approve Withdrawal");
        System.out.println("2. Reject Withdrawal");
        int choice = ConsoleInput.readInt("Select outcome: ");
        boolean approve = switch (choice) {
            case 1 -> true;
            case 2 -> false;
            default -> {
                System.out.println("Invalid selection.");
                yield false;
            }
        };
        if (choice != 1 && choice != 2) {
            return;
        }
        boolean success = staffController.processWithdrawalRequest(applicationId, approve);
        if (success) {
            System.out.println("Withdrawal request processed.");
        } else {
            System.out.println("Unable to process request. Check the application ID and status.");
        }
    }

    private void handleGenerateReport() {
        List<Internship> report = staffController.generateReport(filterCriteria);
        if (report.isEmpty()) {
            System.out.println("No internships found for selected filters.");
            return;
        }
        System.out.println("--- Internship Report ---");
        for (Internship internship : report) {
            System.out.printf("ID: %d | Title: %s | Company: %s | Level: %s | Status: %s | Preferred Major: %s | Visible: %s | Closing Date: %s%n",
                    internship.getInternshipId(),
                    internship.getTitle(),
                    internship.getCompanyName(),
                    internship.getLevel(),
                    internship.getStatus(),
                    internship.getPreferredMajor(),
                    internship.isVisible() ? "Yes" : "No",
                    internship.getClosingDate() != null ? internship.getClosingDate() : "-");
        }
    }

    private void handleSetFilters() {
        System.out.println("--- Set Internship Filters ---");
        System.out.println("Enter new values or leave blank to keep current filter.");

        String statusStr = ConsoleInput.readLine("Filter by Status (PENDING, APPROVED, REJECTED, FILLED): ").toUpperCase();
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
                .closingDateBefore(closingDate)
                .visibleOnly(visibleOnly)
                .build();
        System.out.println("Filters updated.");
    }

    private void handleChangePassword() {
        String newPassword = ConsoleInput.readLine("Enter new password: ");
        boolean success = authController.changePassword(staffController.getCurrentStaff(), newPassword);
        if (success) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Password update failed. Please provide a non-empty value.");
        }
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
