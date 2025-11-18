package com.internship.system.model;

import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;

import java.time.LocalDate;

/**
 * Represents an internship posting in the system.
 * Contains all details about the internship including requirements, dates, and
 * status.
 */
public class Internship {
    /** Unique identifier for this internship. */
    private int internshipId;
    /** Title of the internship position. */
    private String title;
    /** Detailed description of the internship. */
    private String description;
    /** Difficulty level of the internship. */
    private InternshipLevel level;
    /** Preferred major field of study for applicants. */
    private String preferredMajor;
    /** Date when applications open (null if no restriction). */
    private LocalDate openingDate;
    /** Date when applications close (null if no restriction). */
    private LocalDate closingDate;
    /** Current approval status of the internship. */
    private InternshipStatus status;
    /** Name of the company offering the internship. */
    private String companyName;
    /** ID of the company representative managing this internship. */
    private String representativeInChargeId;
    /** Total number of available slots. */
    private int slots;
    /** Whether the internship is visible to students. */
    private boolean visible;
    /** Number of confirmed offers (students who accepted). */
    private int confirmedOffers;

    /**
     * Constructs a new Internship with all specified parameters.
     *
     * @param internshipId             unique identifier
     * @param title                    title of the position
     * @param description              detailed description
     * @param level                    difficulty level
     * @param preferredMajor           preferred major field
     * @param openingDate              date applications open (can be null)
     * @param closingDate              date applications close (can be null)
     * @param status                   approval status
     * @param companyName              name of the company
     * @param representativeInChargeId ID of the managing representative
     * @param slots                    total number of slots available
     * @param visible                  whether visible to students
     * @param confirmedOffers          number of confirmed offers
     */
    public Internship(int internshipId,
            String title,
            String description,
            InternshipLevel level,
            String preferredMajor,
            LocalDate openingDate,
            LocalDate closingDate,
            InternshipStatus status,
            String companyName,
            String representativeInChargeId,
            int slots,
            boolean visible,
            int confirmedOffers) {
        this.internshipId = internshipId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = status;
        this.companyName = companyName;
        this.representativeInChargeId = representativeInChargeId;
        this.slots = slots;
        this.visible = visible;
        this.confirmedOffers = confirmedOffers;
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
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the level.
     *
     * @return the difficulty level
     */
    public InternshipLevel getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level the new difficulty level
     */
    public void setLevel(InternshipLevel level) {
        this.level = level;
    }

    /**
     * Gets the preferred major.
     *
     * @return the preferred major
     */
    public String getPreferredMajor() {
        return preferredMajor;
    }

    /**
     * Sets the preferred major.
     *
     * @param preferredMajor the new preferred major
     */
    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    /**
     * Gets the opening date.
     *
     * @return the opening date, or null if not set
     */
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    /**
     * Sets the opening date.
     *
     * @param openingDate the new opening date (can be null)
     */
    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    /**
     * Gets the closing date.
     *
     * @return the closing date, or null if not set
     */
    public LocalDate getClosingDate() {
        return closingDate;
    }

    /**
     * Sets the closing date.
     *
     * @param closingDate the new closing date (can be null)
     */
    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    /**
     * Gets the status.
     *
     * @return the approval status
     */
    public InternshipStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new approval status
     */
    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    /**
     * Gets the company name.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the company name.
     *
     * @param companyName the new company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Gets the representative in charge ID.
     *
     * @return the representative ID
     */
    public String getRepresentativeInChargeId() {
        return representativeInChargeId;
    }

    /**
     * Sets the representative in charge ID.
     *
     * @param representativeInChargeId the new representative ID
     */
    public void setRepresentativeInChargeId(String representativeInChargeId) {
        this.representativeInChargeId = representativeInChargeId;
    }

    /**
     * Gets the number of slots.
     *
     * @return the total number of slots
     */
    public int getSlots() {
        return slots;
    }

    /**
     * Sets the number of slots.
     *
     * @param slots the new number of slots
     */
    public void setSlots(int slots) {
        this.slots = slots;
    }

    /**
     * Checks if the internship is visible.
     *
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility.
     *
     * @param visible true to make visible, false to hide
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the number of confirmed offers.
     *
     * @return the number of confirmed offers
     */
    public int getConfirmedOffers() {
        return confirmedOffers;
    }

    /**
     * Sets the number of confirmed offers.
     * Automatically updates status to FILLED if all slots are taken,
     * or back to APPROVED if slots become available.
     *
     * @param confirmedOffers the new number of confirmed offers
     */
    public void setConfirmedOffers(int confirmedOffers) {
        this.confirmedOffers = confirmedOffers;
        if (this.confirmedOffers >= slots && status == InternshipStatus.APPROVED) {
            status = InternshipStatus.FILLED;
        } else if (this.confirmedOffers < slots && status == InternshipStatus.FILLED) {
            status = InternshipStatus.APPROVED;
        }
    }

    /**
     * Checks if there are available slots.
     *
     * @return true if there are slots available, false otherwise
     */
    public boolean hasAvailableSlots() {
        return confirmedOffers < slots;
    }

    /**
     * Registers a new confirmed offer.
     * Automatically updates status to FILLED if all slots are taken.
     */
    public void registerConfirmedOffer() {
        if (confirmedOffers < slots) {
            confirmedOffers++;
            if (confirmedOffers >= slots) {
                status = InternshipStatus.FILLED;
            }
        }
    }

    /**
     * Revokes a confirmed offer.
     * Automatically updates status back to APPROVED if slots become available.
     */
    public void revokeConfirmedOffer() {
        if (confirmedOffers > 0) {
            confirmedOffers--;
            if (status == InternshipStatus.FILLED && confirmedOffers < slots) {
                status = InternshipStatus.APPROVED;
            }
        }
    }

    /**
     * Toggles the visibility of the internship.
     */
    public void toggleVisibility() {
        this.visible = !this.visible;
    }

    /**
     * Checks if the internship is open for applications on the given date.
     *
     * @param date the date to check
     * @return true if open on that date, false otherwise
     */
    public boolean isOpenOn(LocalDate date) {
        return (openingDate == null || !date.isBefore(openingDate))
                && (closingDate == null || !date.isAfter(closingDate));
    }

    /**
     * Validates that the date range is valid (closing date not before opening
     * date).
     *
     * @throws IllegalArgumentException if closing date is before opening date
     */
    public void validateDates() {
        if (openingDate != null && closingDate != null) {
            if (closingDate.isBefore(openingDate)) {
                throw new IllegalArgumentException(
                        "Invalid date range: closing date cannot be before opening date. " +
                                "Opening: " + openingDate + ", Closing: " + closingDate);
            }
        }
    }
}