package com.internship.system.model;

import com.internship.system.model.enums.ApplicationStatus;

/**
 * Represents a student's application for an internship position.
 */
public class Application {
    /** Unique identifier for this application. */
    private int applicationId;
    /** ID of the student who submitted this application. */
    private String studentId;
    /** ID of the internship this application is for. */
    private int internshipId;
    /** Current status of the application. */
    private ApplicationStatus status;
    /** Whether a withdrawal has been requested for this application. */
    private boolean withdrawalRequested;

    /**
     * Constructs a new Application with the specified parameters.
     *
     * @param applicationId unique identifier for this application
     * @param studentId ID of the student submitting the application
     * @param internshipId ID of the internship being applied to
     * @param status initial status of the application
     */
    public Application(int applicationId, String studentId, int internshipId, ApplicationStatus status) {
        this(applicationId, studentId, internshipId, status, false);
    }

    /**
     * Constructs a new Application with all parameters including withdrawal status.
     *
     * @param applicationId unique identifier for this application
     * @param studentId ID of the student submitting the application
     * @param internshipId ID of the internship being applied to
     * @param status initial status of the application
     * @param withdrawalRequested whether a withdrawal has been requested
     */
    public Application(int applicationId,
            String studentId,
            int internshipId,
            ApplicationStatus status,
            boolean withdrawalRequested) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.status = status;
        this.withdrawalRequested = withdrawalRequested;
    }

    /**
     * Gets the application ID.
     *
     * @return the application ID
     */
    public int getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the application ID.
     *
     * @param applicationId the new application ID
     */
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     *
     * @param studentId the new student ID
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * Gets the internship ID.
     *
     * @return the internship ID
     */
    public int getInternshipId() {
        return internshipId;
    }

    /**
     * Sets the internship ID.
     *
     * @param internshipId the new internship ID
     */
    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    /**
     * Gets the application status.
     *
     * @return the current status
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Sets the application status.
     *
     * @param status the new status
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    /**
     * Checks if a withdrawal has been requested.
     *
     * @return true if withdrawal has been requested, false otherwise
     */
    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    /**
     * Sets whether a withdrawal has been requested.
     *
     * @param withdrawalRequested true if withdrawal is requested, false otherwise
     */
    public void setWithdrawalRequested(boolean withdrawalRequested) {
        this.withdrawalRequested = withdrawalRequested;
    }
}
