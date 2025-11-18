package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.model.user.User;
import java.util.Optional;
import java.util.UUID; 

/**
 * Controller for handling user authentication and authorization.
 * Manages login, logout, password changes, and user lookup.
 */
public class AuthController {
    /** Data manager for accessing user data. */
    private final DataManager dataManager;
    /** Currently logged-in user. */
    private User currentUser;

    /**
     * Constructs a new AuthController with the specified data manager.
     *
     * @param dataManager the data manager to use for user lookups
     */
    public AuthController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Attempts to log in a user with the provided credentials.
     * Rejects login if user doesn't exist, password is incorrect, or company representative is not approved.
     *
     * @param userId the user ID
     * @param password the password
     * @return Optional containing the logged-in user if successful, empty otherwise
     */
    public Optional<User> login(String userId, String password) {
        Optional<User> userOptional = findUserById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        if (user instanceof CompanyRepresentative representative && !representative.isApproved()) {
            return Optional.empty();
        }

        if (!user.getPassword().equals(password)) {
            return Optional.empty();
        }

        this.currentUser = user;
        return Optional.of(user);
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return Optional containing the current user, or empty if no user is logged in
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    /**
     * Changes the password for a user.
     *
     * @param user the user whose password to change
     * @param newPassword the new password (must not be null or blank)
     * @return true if password was changed successfully, false if new password is invalid
     */
    public boolean changePassword(User user, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            return false;
        }
        user.setPassword(newPassword);
        dataManager.saveAllData();
        return true;
    }

    /**
     * Resets a user's password to a temporary password.
     *
     * @param userId the user ID
     * @return Optional containing the temporary password if reset successful, empty otherwise
     */
    public Optional<String> resetPassword(String userId) {
        Optional<User> userOptional = findUserById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        String tempPassword = generateTemporaryPassword();
        user.setPassword(tempPassword);
        //dataManager.saveAllData();
        user.setPassword(tempPassword);

        return Optional.of(tempPassword);
    }

    /**
     * Generates a temporary password (8 characters from UUID).
     *
     * @return a temporary password string
     */
    private String generateTemporaryPassword() {
        String raw = UUID.randomUUID().toString().replace("-", "");
        return raw.substring(0, 8);
    }

    /**
     * Finds a user by ID across all user types (student, company representative, staff).
     *
     * @param userId the user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> findUserById(String userId) {
        Optional<Student> student = dataManager.findStudentById(userId);
        if (student.isPresent()) {
            return student.map(s -> (User) s);
        }

        Optional<CompanyRepresentative> representative = dataManager.findCompanyRepresentativeById(userId);
        if (representative.isPresent()) {
            return representative.map(r -> (User) r);
        }

        Optional<CareerCenterStaff> staff = dataManager.findStaffById(userId);
        return staff.map(s -> (User) s);
    }
}
