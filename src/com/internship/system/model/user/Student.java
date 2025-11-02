package com.internship.system.model.user;

public class Student extends User {
    private final int yearOfStudy;
    private final String major;

    public Student(String userId, String name, String password, int yearOfStudy, String major) {
        super(userId, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public String getMajor() {
        return major;
    }
}
