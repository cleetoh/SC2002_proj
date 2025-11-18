package com.internship.system.model.enums;

/**
 * Enumeration representing the possible statuses of an internship application.
 */
public enum ApplicationStatus {
    /** Application is pending review by the company. */
    PENDING,
    /** Application was withdrawn while still pending. */
    PENDING_WITHDRAWN,
    /** Application was successful and an offer is pending student acceptance. */
    SUCCESSFUL_PENDING,
    /** Application was successful and the student accepted the offer. */
    SUCCESSFUL_ACCEPTED,
    /** Application was successful but the student rejected the offer. */
    SUCCESSFUL_REJECTED,
    /** Application was successful but later withdrawn. */
    SUCCESSFUL_WITHDRAWN,
    /** Application was unsuccessful. */
    UNSUCCESSFUL
}
