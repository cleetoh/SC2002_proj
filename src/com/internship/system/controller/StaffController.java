package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.Application;
import com.internship.system.model.FilterCriteria;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for career center staff operations.
 * Handles approval/rejection of company representatives and internships, and
 * withdrawal request processing.
 */
public class StaffController {
    /** Data manager for accessing system data. */
    private final DataManager dataManager;
    /** The currently logged-in staff member. */
    private final CareerCenterStaff currentStaff;

    /**
     * Constructs a new StaffController for the specified staff member.
     *
     * @param dataManager  the data manager
     * @param currentStaff the staff member using this controller
     */
    public StaffController(DataManager dataManager, CareerCenterStaff currentStaff) {
        this.dataManager = dataManager;
        this.currentStaff = currentStaff;
    }

    public CareerCenterStaff getCurrentStaff() {
        return currentStaff;
    }

    public List<CompanyRepresentative> viewPendingReps() {
        return dataManager.getCompanyRepresentatives().stream()
                .filter(rep -> !rep.isApproved())
                .collect(Collectors.toList());
    }

    public boolean approveCompanyRep(String repId) {
        Optional<CompanyRepresentative> repOpt = dataManager.findCompanyRepresentativeById(repId);
        if (repOpt.isEmpty()) {
            return false;
        }
        CompanyRepresentative rep = repOpt.get();
        rep.setApproved(true);
        dataManager.updateCompanyRepresentativeApproval(repId, true);
        dataManager.saveAllData();
        return true;
    }

    public boolean rejectCompanyRep(String repId) {
        Optional<CompanyRepresentative> repOpt = dataManager.findCompanyRepresentativeById(repId);
        if (repOpt.isEmpty()) {
            return false;
        }
        CompanyRepresentative rep = repOpt.get();
        rep.setApproved(false);
        dataManager.updateCompanyRepresentativeApproval(repId, false);
        dataManager.saveAllData();
        return true;
    }

    public List<Internship> viewPendingInternships() {
        return dataManager.getInternships().stream()
                .filter(internship -> internship.getStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }

    public boolean approveInternship(int internshipId) {
        Optional<Internship> internshipOpt = dataManager.findInternshipById(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();
        internship.setStatus(InternshipStatus.APPROVED);
        internship.setVisible(true);
        dataManager.updateInternship(internship);
        dataManager.saveAllData();
        return true;
    }

    public boolean rejectInternship(int internshipId) {
        Optional<Internship> internshipOpt = dataManager.findInternshipById(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();
        internship.setStatus(InternshipStatus.REJECTED);
        internship.setVisible(false);
        dataManager.updateInternship(internship);
        dataManager.saveAllData();
        return true;
    }

    public List<Application> getPendingWithdrawalRequests() {
        return dataManager.getApplications().stream()
                .filter(Application::isWithdrawalRequested)
                .collect(Collectors.toList());
    }

    public boolean processWithdrawalRequest(int applicationId, boolean approve) {
        Optional<Application> applicationOpt = dataManager.findApplicationById(applicationId);
        if (applicationOpt.isEmpty()) {
            return false;
        }
        Application application = applicationOpt.get();
        if (!application.isWithdrawalRequested()) {
            return false;
        }

        if (approve) {
            ApplicationStatus oldStatus = application.getStatus();

            application.setStatus(ApplicationStatus.SUCCESSFUL_WITHDRAWN);

            if (oldStatus == ApplicationStatus.SUCCESSFUL_ACCEPTED) {
                dataManager.findInternshipById(application.getInternshipId())
                        .ifPresent(internship -> {
                            if (internship.getConfirmedOffers() > 0) {
                                internship.revokeConfirmedOffer();
                                dataManager.updateInternship(internship);
                            }
                        });
            }
        }
        application.setWithdrawalRequested(false);
        dataManager.updateApplication(application);
        dataManager.saveAllData();
        return true;
    }

    public List<Internship> generateReport(FilterCriteria criteria) {
        return dataManager.getFilteredInternships(criteria);
    }

    /**
     * Gets the company name for a given internship ID.
     *
     * @param internshipId the internship ID
     * @return the company name, or "Unknown" if not found
     */
    public String getCompanyNameForInternship(int internshipId) {
        return dataManager.findInternshipById(internshipId)
                .map(Internship::getCompanyName)
                .orElse("Unknown");
    }

    /**
     * Gets the title for a given internship ID.
     *
     * @param internshipId the internship ID
     * @return the internship title, or "Unknown" if not found
     */
    public String getTitleForInternship(int internshipId) {
        return dataManager.findInternshipById(internshipId)
                .map(Internship::getTitle)
                .orElse("Unknown");
    }
}
