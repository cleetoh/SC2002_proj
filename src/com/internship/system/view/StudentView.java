package com.internship.system.view;

import com.internship.system.controller.StudentController;
import com.internship.system.controller.AuthController;
import com.internship.system.model.Application;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.user.Student;
import com.internship.system.util.ConsoleInput;

import java.util.List;

public class StudentView {
    private final StudentController studentController;
    private final AuthController authController;

    public StudentView(StudentController studentController, AuthController authController) {
        this.studentController = studentController;
        this.authController = authController;
    }

    public void run() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = ConsoleInput.readInt("Select an option: ");
            switch (choice) {
                case 1 -> showEligibleInternships();
                case 2 -> showMyApplications();
                case 3 -> handleApplyForInternship();
                case 4 -> handleAcceptOffer();
                case 5 -> handleWithdrawalRequest();
                case 6 -> handleChangePassword();
                case 7 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayMenu() {
        Student student = studentController.getCurrentStudent();
        System.out.println();
        System.out.println("==== Student Menu ====");
        System.out.println("Logged in as: " + student.getName() + " (" + student.getUserId() + ")");
        System.out.println("1. View Available Internships");
        System.out.println("2. View My Applications");
        System.out.println("3. Apply for Internship");
        System.out.println("4. Accept Offer");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
    }

    private void showEligibleInternships() {
        List<Internship> internships = studentController.getVisibleInternships();
        if (internships.isEmpty()) {
            System.out.println("No internships available based on your profile.");
            return;
        }
        System.out.println("--- Available Internships ---");
        for (Internship internship : internships) {
            System.out.printf("ID: %d | %s | Level: %s | Major: %s | Slots: %d",
                    internship.getInternshipId(),
                    internship.getTitle(),
                    internship.getLevel(),
                    internship.getPreferredMajor(),
                    internship.getSlots() - internship.getConfirmedOffers());
        }
    }

    private void showMyApplications() {
        List<Application> applications = studentController.viewAppliedInternships();
        if (applications.isEmpty()) {
            System.out.println("You have not submitted any applications yet.");
            return;
        }
        System.out.println("--- My Applications ---");
        for (Application application : applications) {
            System.out.printf(
                    "Application ID: %d | Internship ID: %d | Status: %s | Withdrawal Requested: %s | Accepted: %s%n",
                    application.getApplicationId(),
                    application.getInternshipId(),
                    application.getStatus(),
                    application.isWithdrawalRequested() ? "Yes" : "No",
                    application.isOfferAccepted() ? "Yes" : "No");
        }
    }

    private void handleApplyForInternship() {
        int internshipId = ConsoleInput.readInt("Enter Internship ID to apply: ");
        boolean success = studentController.applyForInternship(internshipId);
        if (success) {
            System.out.println("Application submitted successfully.");
        } else {
            System.out.println("Unable to submit application. Check eligibility, limits, or internship availability.");
        }
    }

    private void handleAcceptOffer() {
        int applicationId = ConsoleInput.readInt("Enter Application ID to accept offer: ");
        boolean success = studentController.acceptOffer(applicationId);
        if (success) {
            System.out.println("Offer accepted successfully. Other pending applications withdrawn.");
        } else {
            System.out
                    .println("Unable to accept offer. Ensure the application is successful and not already accepted.");
        }
    }

    private void handleWithdrawalRequest() {
        int applicationId = ConsoleInput.readInt("Enter Application ID to request withdrawal: ");
        boolean success = studentController.withdrawApplication(applicationId);
        if (success) {
            System.out.println("Withdrawal request submitted. Await staff approval.");
        } else {
            System.out.println(
                    "Unable to request withdrawal. Ensure the application exists and is not already withdrawn.");
        }
    }

    private void handleChangePassword() {
        String newPassword = ConsoleInput.readLine("Enter new password: ");
        boolean success = authController.changePassword(studentController.getCurrentStudent(), newPassword);
        if (success) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Password update failed. Please provide a non-empty value.");
        }
    }
}
