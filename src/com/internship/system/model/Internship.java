package com.internship.system.model;

import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;

import java.time.LocalDate;

public class Internship {
    private int internshipId;
    private String title;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private InternshipStatus status;
    private String companyName;
    private String representativeInChargeId;
    private int slots;
    private boolean visible;
    private int confirmedOffers;

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

    public int getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InternshipLevel getLevel() {
        return level;
    }

    public void setLevel(InternshipLevel level) {
        this.level = level;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }

    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRepresentativeInChargeId() {
        return representativeInChargeId;
    }

    public void setRepresentativeInChargeId(String representativeInChargeId) {
        this.representativeInChargeId = representativeInChargeId;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getConfirmedOffers() {
        return confirmedOffers;
    }

    public void setConfirmedOffers(int confirmedOffers) {
        this.confirmedOffers = confirmedOffers;
        if (this.confirmedOffers >= slots && status == InternshipStatus.APPROVED) {
            status = InternshipStatus.FILLED;
        } else if (this.confirmedOffers < slots && status == InternshipStatus.FILLED) {
            status = InternshipStatus.APPROVED;
        }
    }

    public boolean hasAvailableSlots() {
        return confirmedOffers < slots;
    }

    public void registerConfirmedOffer() {
        if (confirmedOffers < slots) {
            confirmedOffers++;
            if (confirmedOffers >= slots) {
                status = InternshipStatus.FILLED;
            }
        }
    }

    public void revokeConfirmedOffer() {
        if (confirmedOffers > 0) {
            confirmedOffers--;
            if (status == InternshipStatus.FILLED && confirmedOffers < slots) {
                status = InternshipStatus.APPROVED;
            }
        }
    }

    public void toggleVisibility() {
        this.visible = !this.visible;
    }

    public boolean isOpenOn(LocalDate date) {
        return (openingDate == null || !date.isBefore(openingDate))
                && (closingDate == null || !date.isAfter(closingDate));
    }
}
