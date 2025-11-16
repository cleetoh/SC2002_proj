package com.internship.system.model;

import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;

import java.time.LocalDate;
import java.util.Optional;

public class FilterCriteria {
    private final InternshipStatus status;
    private final InternshipLevel level;
    private final String preferredMajor;
    private final String companyName;
    private final LocalDate closingDateBefore;
    private final Boolean visibleOnly;

    private FilterCriteria(Builder builder) {
        this.status = builder.status;
        this.level = builder.level;
        this.preferredMajor = builder.preferredMajor;
        this.companyName = builder.companyName;
        this.closingDateBefore = builder.closingDateBefore;
        this.visibleOnly = builder.visibleOnly;
    }

    public Optional<InternshipStatus> getStatus() {
        return Optional.ofNullable(status);
    }

    public Optional<InternshipLevel> getLevel() {
        return Optional.ofNullable(level);
    }

    public Optional<String> getPreferredMajor() {
        return Optional.ofNullable(preferredMajor);
    }

    public Optional<LocalDate> getClosingDateBefore() {
        return Optional.ofNullable(closingDateBefore);
    }

    public Optional<Boolean> getVisibleOnly() {
        return Optional.ofNullable(visibleOnly);
    }

    public Optional<String> getCompanyName() {
        return Optional.ofNullable(companyName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InternshipStatus status;
        private InternshipLevel level;
        private String preferredMajor;
        private String companyName;
        private LocalDate closingDateBefore;
        private Boolean visibleOnly;

        public Builder status(InternshipStatus status) {
            this.status = status;
            return this;
        }

        public Builder level(InternshipLevel level) {
            this.level = level;
            return this;
        }

        public Builder preferredMajor(String preferredMajor) {
            this.preferredMajor = preferredMajor;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder closingDateBefore(LocalDate closingDateBefore) {
            this.closingDateBefore = closingDateBefore;
            return this;
        }

        public Builder visibleOnly(Boolean visibleOnly) {
            this.visibleOnly = visibleOnly;
            return this;
        }

        public FilterCriteria build() {
            return new FilterCriteria(this);
        }
    }
}
