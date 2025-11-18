package com.internship.system.model;

import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents filtering criteria for searching internships.
 * Uses the Builder pattern for flexible construction.
 */
public class FilterCriteria {
    /** Filter by internship status. */
    private final InternshipStatus status;
    /** Filter by difficulty level. */
    private final InternshipLevel level;
    /** Filter by preferred major. */
    private final String preferredMajor;
    /** Filter by company name. */
    private final String companyName;
    /** Filter by closing date (before this date). */
    private final LocalDate closingDateBefore;
    /** Filter by visibility (true = visible only, false = hidden only, null = either). */
    private final Boolean visibleOnly;

    /**
     * Constructs a FilterCriteria from a Builder.
     *
     * @param builder the builder containing filter values
     */
    private FilterCriteria(Builder builder) {
        this.status = builder.status;
        this.level = builder.level;
        this.preferredMajor = builder.preferredMajor;
        this.companyName = builder.companyName;
        this.closingDateBefore = builder.closingDateBefore;
        this.visibleOnly = builder.visibleOnly;
    }

    /**
     * Gets the status filter.
     *
     * @return Optional containing the status filter, or empty if not set
     */
    public Optional<InternshipStatus> getStatus() {
        return Optional.ofNullable(status);
    }

    /**
     * Gets the level filter.
     *
     * @return Optional containing the level filter, or empty if not set
     */
    public Optional<InternshipLevel> getLevel() {
        return Optional.ofNullable(level);
    }

    /**
     * Gets the preferred major filter.
     *
     * @return Optional containing the preferred major, or empty if not set
     */
    public Optional<String> getPreferredMajor() {
        return Optional.ofNullable(preferredMajor);
    }

    /**
     * Gets the closing date before filter.
     *
     * @return Optional containing the closing date, or empty if not set
     */
    public Optional<LocalDate> getClosingDateBefore() {
        return Optional.ofNullable(closingDateBefore);
    }

    /**
     * Gets the visible only filter.
     *
     * @return Optional containing the visibility filter, or empty if not set
     */
    public Optional<Boolean> getVisibleOnly() {
        return Optional.ofNullable(visibleOnly);
    }

    /**
     * Gets the company name filter.
     *
     * @return Optional containing the company name, or empty if not set
     */
    public Optional<String> getCompanyName() {
        return Optional.ofNullable(companyName);
    }

    /**
     * Creates a new Builder instance.
     *
     * @return a new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing FilterCriteria instances.
     */
    public static class Builder {
        /** Filter by internship status. */
        private InternshipStatus status;
        /** Filter by difficulty level. */
        private InternshipLevel level;
        /** Filter by preferred major. */
        private String preferredMajor;
        /** Filter by company name. */
        private String companyName;
        /** Filter by closing date (before this date). */
        private LocalDate closingDateBefore;
        /** Filter by visibility. */
        private Boolean visibleOnly;

        /**
         * Sets the status filter.
         *
         * @param status the status to filter by
         * @return this builder for method chaining
         */
        public Builder status(InternshipStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the level filter.
         *
         * @param level the level to filter by
         * @return this builder for method chaining
         */
        public Builder level(InternshipLevel level) {
            this.level = level;
            return this;
        }

        /**
         * Sets the preferred major filter.
         *
         * @param preferredMajor the major to filter by
         * @return this builder for method chaining
         */
        public Builder preferredMajor(String preferredMajor) {
            this.preferredMajor = preferredMajor;
            return this;
        }

        /**
         * Sets the company name filter.
         *
         * @param companyName the company name to filter by
         * @return this builder for method chaining
         */
        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        /**
         * Sets the closing date before filter.
         *
         * @param closingDateBefore filter for internships closing before this date
         * @return this builder for method chaining
         */
        public Builder closingDateBefore(LocalDate closingDateBefore) {
            this.closingDateBefore = closingDateBefore;
            return this;
        }

        /**
         * Sets the visible only filter.
         *
         * @param visibleOnly true for visible only, false for hidden only, null for either
         * @return this builder for method chaining
         */
        public Builder visibleOnly(Boolean visibleOnly) {
            this.visibleOnly = visibleOnly;
            return this;
        }

        /**
         * Builds a FilterCriteria instance with the configured filters.
         *
         * @return a new FilterCriteria instance
         */
        public FilterCriteria build() {
            return new FilterCriteria(this);
        }
    }
}
