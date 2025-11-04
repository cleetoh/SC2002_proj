package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.model.user.User;
import com.internship.system.util.ConsoleInput;
import com.internship.system.view.CompanyView;
import com.internship.system.view.MainMenuView;
import com.internship.system.view.StaffView;
import com.internship.system.view.StudentView;

import java.util.Optional;

public class AppController {
    private final DataManager dataManager;
    private final MainMenuView mainMenuView;
    private final AuthController authController;

    public AppController() {
        this.dataManager = new DataManager();
        this.mainMenuView = new MainMenuView();
        this.authController = new AuthController(dataManager);
    }

    public void run() {
        dataManager.loadAllData();
        mainMenuView.displaySplash();

        boolean running = true;
        while (running) {
            mainMenuView.displayMainOptions();
            int choice = mainMenuView.promptForMainSelection();
            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegistration();
                case 3 -> {
                    running = false;
                    mainMenuView.displayExitMessage();
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        dataManager.saveAllData();
    }

    private void handleLogin() {
        String userId = mainMenuView.promptForUserId();
        String password = mainMenuView.promptForPassword();
        Optional<User> userOptional = authController.login(userId, password);
        if (userOptional.isEmpty()) {
            mainMenuView.displayLoginFailure();
            return;
        }
        User user = userOptional.get();
        mainMenuView.displayLoginSuccess(user);
        dispatchUserSession(user);
        authController.logout();
    }

    private void handleRegistration() {
        String email = mainMenuView.promptForEmail();
        String name = mainMenuView.promptForName();
        String company = mainMenuView.promptForCompanyName();
        String department = mainMenuView.promptForDepartment();
        String position = mainMenuView.promptForPosition();

        try {
            CompanyController.register(dataManager, email, name, company, department, position);
            mainMenuView.displayRegistrationSuccess();
        } catch (IllegalArgumentException ex) {
            mainMenuView.displayRegistrationFailure(ex.getMessage());
        }
    }

    private void dispatchUserSession(User user) {
        if (user instanceof Student student) {
            StudentController studentController = new StudentController(dataManager, student);
            StudentView studentView = new StudentView(studentController, authController);
            studentView.run();
        } else if (user instanceof CompanyRepresentative representative) {
            CompanyController companyController = new CompanyController(dataManager, representative);
            CompanyView companyView = new CompanyView(companyController, authController);
            companyView.run();
        } else if (user instanceof CareerCenterStaff staff) {
            StaffController staffController = new StaffController(dataManager, staff);
            StaffView staffView = new StaffView(staffController, authController);
            staffView.run();
        } else {
            System.out.println("Unsupported user role. Logging out.");
        }
    }
}
