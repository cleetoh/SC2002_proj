package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.Application;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentController {
    private static final int MAX_ACTIVE_APPLICATIONS = 3;

    private final DataManager dataManager;
    private final Student currentStudent;

    public StudentController(DataManager dataManager, Student currentStudent) {
        this.dataManager = dataManager;
        this.currentStudent = currentStudent;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public List<Internship> getVisibleInternships() {
        LocalDate today = LocalDate.now();
        return dataManager.getInternships().stream()
                .filter(internship -> internship.getStatus() == InternshipStatus.APPROVED)
                .filter(Internship::isVisible)
                .filter(Internship::hasAvailableSlots)
                .filter(internship -> internship.isOpenOn(today))
                .filter(this::isLevelEligible)
                .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .collect(Collectors.toList());
    }

    public List<Application> viewAppliedInternships() {
        return dataManager.getApplicationsForStudent(currentStudent.getUserId());
    }

    public boolean applyForInternship(int internshipId) {
        Optional<Internship> internshipOpt = dataManager.findInternshipById(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();

        if (!canApplyToInternship(internship)) {
            return false;
        }

        if (hasReachedApplicationLimit()) {
            return false;
        }

        boolean alreadyApplied = viewAppliedInternships().stream()
                .anyMatch(application -> application.getInternshipId() == internshipId);
        if (alreadyApplied) {
            return false;
        }

        int applicationId = dataManager.nextApplicationId();
        Application application = new Application(applicationId,
                currentStudent.getUserId(),
                internshipId,
                ApplicationStatus.PENDING);
        dataManager.addApplication(application);
        dataManager.saveAllData();
        return true;
    }

    public boolean acceptOffer(int applicationId) {
        Optional<Application> applicationOpt = dataManager.findApplicationById(applicationId);
        if (applicationOpt.isEmpty()) {
            return false;
        }
        Application application = applicationOpt.get();
        if (!application.getStudentId().equals(currentStudent.getUserId())) {
            return false;
        }
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }
        if (application.isOfferAccepted()) {
            return false;
        }
        if (hasAcceptedOffer()) {
            return false;
        }

        application.setOfferAccepted(true);
        dataManager.updateApplication(application);
        dataManager.findInternshipById(application.getInternshipId())
                .ifPresent(internship -> {
                    internship.registerConfirmedOffer();
                    dataManager.updateInternship(internship);
                });

        withdrawOtherApplications(applicationId);

        dataManager.saveAllData();
        return true;
    }

    public boolean withdrawApplication(int applicationId) {
        Optional<Application> applicationOpt = dataManager.findApplicationById(applicationId);
        if (applicationOpt.isEmpty()) {
            return false;
        }
        Application application = applicationOpt.get();
        if (!application.getStudentId().equals(currentStudent.getUserId())) {
            return false;
        }
        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            return false;
        }
        application.setWithdrawalRequested(true);
        dataManager.updateApplication(application);
        dataManager.saveAllData();
        return true;
    }

    private boolean canApplyToInternship(Internship internship) {
        if (internship.getStatus() != InternshipStatus.APPROVED) {
            return false;
        }
        if (!internship.isVisible()) {
            return false;
        }
        if (!internship.hasAvailableSlots()) {
            return false;
        }
        if (!internship.isOpenOn(LocalDate.now())) {
            return false;
        }
        return isLevelEligible(internship);
    }

    private boolean isLevelEligible(Internship internship) {
        InternshipLevel level = internship.getLevel();
        int year = currentStudent.getYearOfStudy();
        if (year <= 2) {
            return level == InternshipLevel.BASIC;
        }
        return true;
    }

    private boolean hasReachedApplicationLimit() {
        long activeApplications = viewAppliedInternships().stream()
                .filter(application -> application.getStatus() == ApplicationStatus.PENDING
                        || application.getStatus() == ApplicationStatus.SUCCESSFUL)
                .count();
        return activeApplications >= MAX_ACTIVE_APPLICATIONS;
    }

    private boolean hasAcceptedOffer() {
        return viewAppliedInternships().stream().anyMatch(Application::isOfferAccepted);
    }

    private void withdrawOtherApplications(int acceptedApplicationId) {
        List<Application> studentApplications = viewAppliedInternships();
        for (Application application : studentApplications) {
            if (application.getApplicationId() == acceptedApplicationId) {
                continue;
            }
            if (application.getStatus() == ApplicationStatus.PENDING
                    || application.getStatus() == ApplicationStatus.SUCCESSFUL) {
                application.setStatus(ApplicationStatus.WITHDRAWN);
                application.setWithdrawalRequested(false);
                dataManager.updateApplication(application);
            }
        }
    }
}
