package com.internship.system.model.user;

/**
 * Abstract base class representing a user in the internship system.
 * All user types (Student, CompanyRepresentative, CareerCenterStaff) extend this class.
 */
public abstract class User {
    /** Unique identifier for the user. */
    private final String userId;
    /** Full name of the user. */
    private final String name;
    /** User's password for authentication. */
    private String password;

    /**
     * Constructs a new User with the specified credentials.
     *
     * @param userId unique identifier for the user
     * @param name full name of the user
     * @param password initial password
     */
    protected User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for the user.
     *
     * @param newPassword the new password
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
