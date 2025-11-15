package com.internship.system.model;

import com.internship.system.model.enums.ApplicationStatus;

public class Application {
    private int applicationId;
    private String studentId;
    private int internshipId;
    private ApplicationStatus status;
    private boolean withdrawalRequested;

    public Application(int applicationId, String studentId, int internshipId, ApplicationStatus status) {
        this(applicationId, studentId, internshipId, status, false);
    }

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

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    public void setWithdrawalRequested(boolean withdrawalRequested) {
        this.withdrawalRequested = withdrawalRequested;
    }
}
