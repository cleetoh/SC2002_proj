package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.model.user.User;
import java.util.Optional;
import java.util.UUID; 

public class AuthController {
    private final DataManager dataManager;
    private User currentUser;

    public AuthController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

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

    public void logout() {
        this.currentUser = null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean changePassword(User user, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            return false;
        }
        user.setPassword(newPassword);
        dataManager.saveAllData();
        return true;
    }

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

    private String generateTemporaryPassword() {
        String raw = UUID.randomUUID().toString().replace("-", "");
        return raw.substring(0, 8);
    }

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
