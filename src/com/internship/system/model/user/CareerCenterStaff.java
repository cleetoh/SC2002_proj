package com.internship.system.model.user;

/**
 * Represents a career center staff member in the internship system.
 * Staff members can approve/reject company representatives and internships,
 * and process withdrawal requests.
 */
public class CareerCenterStaff extends User {
    /** Department the staff member belongs to. */
    private final String staffDepartment;

    /**
     * Constructs a new CareerCenterStaff member with the specified information.
     *
     * @param userId unique identifier for the staff member
     * @param name full name of the staff member
     * @param password initial password
     * @param staffDepartment department the staff member belongs to
     */
    public CareerCenterStaff(String userId, String name, String password, String staffDepartment) {
        super(userId, name, password);
        this.staffDepartment = staffDepartment;
    }

    /**
     * Gets the staff department.
     *
     * @return the department
     */
    public String getStaffDepartment() {
        return staffDepartment;
    }
}
