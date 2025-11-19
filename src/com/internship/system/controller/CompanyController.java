package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.Application;
import com.internship.system.model.FilterCriteria;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for company representative operations.
 * Handles internship creation, management, and application processing.
 */
public class CompanyController {
    /** Maximum number of internships a representative can create. */
    private static final int MAX_INTERNSHIPS_PER_REP = 5;
    /** Maximum number of slots allowed per internship. */
    private static final int MAX_SLOTS = 10;

    /** Data manager for accessing system data. */
    private final DataManager dataManager;
    /** The currently logged-in company representative. */
    private final CompanyRepresentative currentRep;

    /**
     * Constructs a new CompanyController for the specified representative.
     *
     * @param dataManager the data manager
     * @param currentRep  the company representative using this controller
     */
    public CompanyController(DataManager dataManager, CompanyRepresentative currentRep) {
        this.dataManager = dataManager;
        this.currentRep = currentRep;
    }

    /**
     * Registers a new company representative.
     *
     * @param dataManager the data manager
     * @param email       the email (used as user ID)
     * @param name        the full name
     * @param companyName the company name
     * @param department  the department
     * @param position    the position/title
     * @return the newly created representative
     * @throws IllegalArgumentException if a representative with the same ID already
     *                                  exists
     */
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

    /**
     * Gets the current company representative.
     *
     * @return the current representative
     */
    public CompanyRepresentative getCurrentRep() {
        return currentRep;
    }

    /**
     * Gets internships for the current company, filtered by criteria.
     *
     * @param criteria the filtering criteria
     * @return list of internships matching the criteria and belonging to the
     *         company
     */
    public List<Internship> getInternships(FilterCriteria criteria) {
        List<Internship> filteredInternships = dataManager.getFilteredInternships(criteria);
        return filteredInternships.stream()
                .filter(internship -> internship.getCompanyName().equals(currentRep.getCompanyName()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the representative can create more internships.
     *
     * @return true if under the limit, false otherwise
     */
    public boolean canCreateMoreInternships() {
        long count = viewInternships().stream().count();
        return count < MAX_INTERNSHIPS_PER_REP;
    }

    /**
     * Creates a new internship posting.
     *
     * @param title          the internship title
     * @param description    the description
     * @param level          the difficulty level
     * @param preferredMajor the preferred major
     * @param openingDate    the opening date (can be null)
     * @param closingDate    the closing date (can be null)
     * @param slots          the number of slots (1-10)
     * @return Optional containing the created internship if successful, empty
     *         otherwise
     */
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
        if (title == null || title.isBlank()) {
            return Optional.empty();
        }
        if (description == null || description.isBlank()) {
            return Optional.empty();
        }
        if (preferredMajor == null || preferredMajor.isBlank()) {
            return Optional.empty();
        }
        if (level == null) {
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

    /**
     * Gets all internships managed by the current representative.
     *
     * @return list of internships owned by this representative
     */
    public List<Internship> viewInternships() {
        return dataManager.getInternships().stream()
                .filter(internship -> internship.getRepresentativeInChargeId().equals(currentRep.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if an internship can be updated.
     * An internship can be updated if it exists, is owned by the current
     * representative,
     * and is in PENDING status.
     *
     * @param internshipId the internship ID
     * @return true if the internship can be updated, false otherwise
     */
    public boolean canUpdateInternship(int internshipId) {
        Optional<Internship> internshipOpt = ensureOwnership(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();
        return internship.getStatus() == InternshipStatus.PENDING;
    }

    /**
     * Updates an existing internship.
     * Only works for internships in PENDING status that are owned by the current
     * representative.
     *
     * @param internshipId   the internship ID
     * @param title          the new title
     * @param description    the new description
     * @param level          the new level
     * @param preferredMajor the new preferred major
     * @param openingDate    the new opening date
     * @param closingDate    the new closing date
     * @param slots          the new number of slots
     * @return true if update successful, false otherwise
     */
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

        if (internship.getStatus() != InternshipStatus.PENDING) {
            return false;
        }
        if (slots <= 0 || slots > MAX_SLOTS) {
            return false;
        }
        if (title == null || title.isBlank()) {
            return false;
        }
        if (description == null || description.isBlank()) {
            return false;
        }
        if (preferredMajor == null || preferredMajor.isBlank()) {
            return false;
        }
        if (level == null) {
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

    /**
     * Toggles the visibility of an approved internship.
     *
     * @param internshipId the internship ID
     * @return true if toggle successful, false otherwise
     */
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

    /**
     * Gets all applications for a specific internship.
     *
     * @param internshipId the internship ID
     * @return list of applications, or empty list if internship not found or not
     *         owned
     */
    public List<Application> viewApplicationsForInternship(int internshipId) {
        Optional<Internship> internshipOpt = ensureOwnership(internshipId);
        if (internshipOpt.isEmpty()) {
            return List.of();
        }
        return dataManager.getApplicationsForInternship(internshipId);
    }

    /**
     * Processes an application by updating its status.
     * Can approve (SUCCESSFUL_PENDING) or reject (UNSUCCESSFUL) applications.
     *
     * @param applicationId the application ID
     * @param newStatus     the new status (must be SUCCESSFUL_PENDING or
     *                      UNSUCCESSFUL)
     * @return true if processing successful, false otherwise
     */
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

    /**
     * Deletes an internship.
     * Only works for internships in PENDING status that are owned by the current
     * representative.
     *
     * @param internshipId the internship ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteInternship(int internshipId) {
        Optional<Internship> internshipOpt = ensureOwnership(internshipId);
        if (internshipOpt.isEmpty()) {
            return false;
        }
        Internship internship = internshipOpt.get();
        if (internship.getStatus() != InternshipStatus.PENDING) {
            return false;
        }
        dataManager.removeInternship(internshipId);
        dataManager.saveAllData();
        return true;
    }

    /**
     * Ensures that an internship is owned by the current representative.
     *
     * @param internshipId the internship ID
     * @return Optional containing the internship if owned, empty otherwise
     */
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

    /**
     * Gets the student name for a given student ID.
     *
     * @param studentId the student ID
     * @return the student name, or "Unknown" if not found
     */
    public String getStudentName(String studentId) {
        return dataManager.findStudentById(studentId)
                .map(Student::getName)
                .orElse("Unknown");
    }

    /**
     * Gets the student major for a given student ID.
     *
     * @param studentId the student ID
     * @return the student major, or "Unknown" if not found
     */
    public String getStudentMajor(String studentId) {
        return dataManager.findStudentById(studentId)
                .map(Student::getMajor)
                .orElse("Unknown");
    }

    /**
     * Gets the student year of study for a given student ID.
     *
     * @param studentId the student ID
     * @return the year of study, or -1 if not found
     */
    public int getStudentYearOfStudy(String studentId) {
        return dataManager.findStudentById(studentId)
                .map(Student::getYearOfStudy)
                .orElse(-1);
    }
}
