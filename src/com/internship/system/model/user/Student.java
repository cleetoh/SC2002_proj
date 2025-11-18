package com.internship.system.model.user;

/**
 * Represents a student user in the internship system.
 * Students can browse and apply for internships.
 */
public class Student extends User {
    /** The year of study (1-4 typically). */
    private final int yearOfStudy;
    /** The student's major field of study. */
    private final String major;

    /**
     * Constructs a new Student with the specified information.
     *
     * @param userId unique identifier for the student
     * @param name full name of the student
     * @param password initial password
     * @param yearOfStudy year of study (1-4)
     * @param major major field of study
     */
    public Student(String userId, String name, String password, int yearOfStudy, String major) {
        super(userId, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    /**
     * Gets the year of study.
     *
     * @return the year of study
     */
    public int getYearOfStudy() {
        return yearOfStudy;
    }

    /**
     * Gets the student's major.
     *
     * @return the major field of study
     */
    public String getMajor() {
        return major;
    }
}
