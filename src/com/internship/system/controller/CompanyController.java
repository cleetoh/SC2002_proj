package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.Application;
import com.internship.system.model.FilterCriteria;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.CompanyRepresentative;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompanyController {
    private static final int MAX_INTERNSHIPS_PER_REP = 5;
    private static final int MAX_SLOTS = 10;

    private final DataManager dataManager;
    private final CompanyRepresentative currentRep;

    public CompanyController(DataManager dataManager, CompanyRepresentative currentRep) {
        this.dataManager = dataManager;
        this.currentRep = currentRep;
    }

    public static CompanyRepresentative register(DataManager dataManager,
            String email,
            String name,
            String companyName,
            String department,
            String position) {
        if (dataManager.findCompanyRepresentativeById(email).isPresent()) {
            throw new IllegalArgumentException("Representative with ID already exists");
        }
        CompanyRepresentative representative = new CompanyRepresentative(
                email,
                name,
                "password",
                companyName,
                department,
                position,
                false);
        dataManager.addCompanyRepresentative(representative);
        dataManager.saveAllData();
        return representative;
    }

    public CompanyRepresentative getCurrentRep() {
        return currentRep;
    }

    public List<Internship> getInternships(FilterCriteria criteria) {
        List<Internship> filteredInternships = dataManager.getFilteredInternships(criteria);
        return filteredInternships.stream()
                .filter(internship -> internship.getCompanyName().equals(currentRep.getCompanyName()))
                .collect(Collectors.toList());
    }

    public boolean canCreateMoreInternships() {
        long count = viewInternships().stream().count();
        return count < MAX_INTERNSHIPS_PER_REP;
    }

    public Optional<Internship> createInternship(String title,
            String description,
            InternshipLevel level,
            String preferredMajor,
            LocalDate openingDate,
            LocalDate closingDate,
            int slots) {
        if (!canCreateMoreInternships()) {
            return Optional.empty();
        }
        if (slots <= 0 || slots > MAX_SLOTS) {
            return Optional.empty();
        }

        int internshipId = dataManager.nextInternshipId();
        Internship internship = new Internship(internshipId,
                title,
                description,
                level,
                preferredMajor,
                openingDate,
                closingDate,
                InternshipStatus.PENDING,
                currentRep.getCompanyName(),
                currentRep.getUserId(),
                slots,
                false,
                0);
        dataManager.addInternship(internship);
        dataManager.saveAllData();
        return Optional.of(internship);
    }

    public List<Internship> viewInternships() {
        return dataManager.getInternships().stream()
                .filter(internship -> internship.getRepresentativeInChargeId().equals(currentRep.getUserId()))
                .collect(Collectors.toList());
    }

    public boolean updateInternship(int internshipId,
            String title,
            String description,
            InternshipLevel level,
            String preferredMajor,
            LocalDate openingDate,
            LocalDate closingDate,
            int slots) {
        Optional<Internship> internshipOpt = ensureOwnership(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();
        if (internship.getStatus() == InternshipStatus.FILLED) {
            return false;
        }
        if (slots <= 0 || slots > MAX_SLOTS) {
            return false;
        }
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setLevel(level);
        internship.setPreferredMajor(preferredMajor);
        internship.setOpeningDate(openingDate);
        internship.setClosingDate(closingDate);
        internship.setSlots(slots);
        internship.setStatus(InternshipStatus.PENDING);
        internship.setVisible(false);
        internship.setConfirmedOffers(0);
        dataManager.updateInternship(internship);
        dataManager.saveAllData();
        return true;
    }

    public boolean toggleInternshipVisibility(int internshipId) {
        Optional<Internship> internshipOpt = ensureOwnership(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();
        if (internship.getStatus() != InternshipStatus.APPROVED) {
            return false;
        }
        internship.toggleVisibility();
        dataManager.updateInternship(internship);
        dataManager.saveAllData();
        return true;
    }

    public List<Application> viewApplicationsForInternship(int internshipId) {
        Optional<Internship> internshipOpt = ensureOwnership(internshipId);
        if (internshipOpt.isEmpty()) {
            return List.of();
        }
        return dataManager.getApplicationsForInternship(internshipId);
    }

    public boolean processApplication(int applicationId, ApplicationStatus newStatus) {
        if (newStatus != ApplicationStatus.SUCCESSFUL_PENDING
                && newStatus != ApplicationStatus.UNSUCCESSFUL) {
            return false;
        }
        Optional<Application> applicationOpt = dataManager.findApplicationById(applicationId);
        if (applicationOpt.isEmpty()) {
            return false;
        }
        Application application = applicationOpt.get();

        // Cannot process withdrawn applications
        if (application.getStatus() == ApplicationStatus.PENDING_WITHDRAWN
                || application.getStatus() == ApplicationStatus.SUCCESSFUL_WITHDRAWN) {
            return false;
        }

        Optional<Internship> internshipOpt = ensureOwnership(application.getInternshipId());
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();

        if (newStatus == ApplicationStatus.SUCCESSFUL_PENDING && !internship.hasAvailableSlots()) {
            return false;
        }

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(newStatus);
        application.setWithdrawalRequested(false);

        // If changing from SUCCESSFUL_ACCEPTED to something else, may need to revoke
        // offer
        if (oldStatus == ApplicationStatus.SUCCESSFUL_ACCEPTED
                && newStatus != ApplicationStatus.SUCCESSFUL_ACCEPTED
                && internship.getConfirmedOffers() > 0) {
            internship.revokeConfirmedOffer();
        }

        dataManager.updateApplication(application);
        dataManager.updateInternship(internship);
        dataManager.saveAllData();
        return true;
    }

    private Optional<Internship> ensureOwnership(int internshipId) {
        Optional<Internship> internshipOpt = dataManager.findInternshipById(internshipId);
        if (internshipOpt.isEmpty()) {
            return Optional.empty();
        }
        Internship internship = internshipOpt.get();
        if (!internship.getRepresentativeInChargeId().equals(currentRep.getUserId())) {
            return Optional.empty();
        }
        return Optional.of(internship);
    }
}
