package com.internship.system.model.enums;

/**
 * Enumeration representing the approval status of an internship posting.
 */
public enum InternshipStatus {
    /** Internship is pending approval from career center staff. */
    PENDING,
    /** Internship has been approved and can be made visible. */
    APPROVED,
    /** Internship has been rejected by career center staff. */
    REJECTED,
    /** Internship has been filled (all slots confirmed). */
    FILLED
}
