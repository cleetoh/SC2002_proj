package com.internship.system.model.user;

/**
 * Represents a company representative user in the internship system.
 * Company representatives can create and manage internship postings.
 */
public class CompanyRepresentative extends User {
    /** Name of the company the representative works for. */
    private final String companyName;
    /** Department within the company. */
    private final String department;
    /** Position/title of the representative. */
    private final String position;
    /** Whether the representative's account has been approved by staff. */
    private boolean approved;

    /**
     * Constructs a new CompanyRepresentative with the specified information.
     *
     * @param userId unique identifier (typically email)
     * @param name full name of the representative
     * @param password initial password
     * @param companyName name of the company
     * @param department department within the company
     * @param position position/title of the representative
     * @param approved whether the account is approved
     */
    public CompanyRepresentative(
            String userId,
            String name,
            String password,
            String companyName,
            String department,
            String position,
            boolean approved) {
        super(userId, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approved = approved;
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
     * Gets the department.
     *
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Gets the position/title.
     *
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Checks if the representative's account is approved.
     *
     * @return true if approved, false otherwise
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * Sets the approval status of the representative's account.
     *
     * @param approved true if approved, false otherwise
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
