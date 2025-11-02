package com.internship.system.model.user;

public class CareerCenterStaff extends User {
    private final String staffDepartment;

    public CareerCenterStaff(String userId, String name, String password, String staffDepartment) {
        super(userId, name, password);
        this.staffDepartment = staffDepartment;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }
}
