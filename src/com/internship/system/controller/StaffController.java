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

public class StaffController {
    private final DataManager dataManager;
    private final CareerCenterStaff currentStaff;

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

            // Mark as SUCCESSFUL_WITHDRAWN
            application.setStatus(ApplicationStatus.SUCCESSFUL_WITHDRAWN);

            // If student had accepted an offer, revoke the confirmed offer
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
}
