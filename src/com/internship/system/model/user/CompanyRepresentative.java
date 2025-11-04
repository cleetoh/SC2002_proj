package com.internship.system.model.user;

public class CompanyRepresentative extends User {
    private final String companyName;
    private final String department;
    private final String position;
    private boolean approved;

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

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
