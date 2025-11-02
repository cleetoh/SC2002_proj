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
                case 10 -> handleChangePassword();
                case 11 -> running = false;
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
        System.out.println("9. Generate Internship Report");
        System.out.println("10. Change Password");
        System.out.println("11. Logout");
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
        FilterCriteria criteria = buildCriteriaFromInput();
        List<Internship> report = staffController.generateReport(criteria);
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

    private FilterCriteria buildCriteriaFromInput() {
        System.out.println("Select Status Filter (leave blank for all):");
        for (int i = 0; i < InternshipStatus.values().length; i++) {
            System.out.printf("%d. %s%n", i + 1, InternshipStatus.values()[i]);
        }
        String statusInput = ConsoleInput.readLine("Choice: ");
        InternshipStatus status = null;
        if (!statusInput.isBlank()) {
            try {
                int index = Integer.parseInt(statusInput.trim()) - 1;
                if (index >= 0 && index < InternshipStatus.values().length) {
                    status = InternshipStatus.values()[index];
                }
            } catch (NumberFormatException ignored) {
            }
        }

        System.out.println("Select Level Filter (leave blank for all):");
        for (int i = 0; i < InternshipLevel.values().length; i++) {
            System.out.printf("%d. %s%n", i + 1, InternshipLevel.values()[i]);
        }
        String levelInput = ConsoleInput.readLine("Choice: ");
        InternshipLevel level = null;
        if (!levelInput.isBlank()) {
            try {
                int index = Integer.parseInt(levelInput.trim()) - 1;
                if (index >= 0 && index < InternshipLevel.values().length) {
                    level = InternshipLevel.values()[index];
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String major = ConsoleInput.readLine("Preferred Major Filter (leave blank for all): ");
        LocalDate closingBefore = promptForDate("Closing Date on/before (YYYY-MM-DD, blank for none): ");
        String visibilityInput = ConsoleInput.readLine("Visible Only? (y/n/blank for either): ");
        Boolean visibleOnly = null;
        if (!visibilityInput.isBlank()) {
            if (visibilityInput.equalsIgnoreCase("y")) {
                visibleOnly = true;
            } else if (visibilityInput.equalsIgnoreCase("n")) {
                visibleOnly = false;
            }
        }

        FilterCriteria.Builder builder = FilterCriteria.builder();
        if (status != null) {
            builder.status(status);
        }
        if (level != null) {
            builder.level(level);
        }
        if (!major.isBlank()) {
            builder.preferredMajor(major.trim());
        }
        if (closingBefore != null) {
            builder.closingDateBefore(closingBefore);
        }
        if (visibleOnly != null) {
            builder.visibleOnly(visibleOnly);
        }
        return builder.build();
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
