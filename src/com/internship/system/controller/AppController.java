package com.internship.system.controller;

import com.internship.system.data.DataManager;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.model.user.User;
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
        Optional<User> userOpt = authController.findUserById(userId);

        if (userOpt.isEmpty()) {
            mainMenuView.displayUserNotFound(userId);
            return;
        }

        User user = userOpt.get();

        if (user instanceof CompanyRepresentative representative && !representative.isApproved()) {
            mainMenuView.displayAccountNotApproved();
            return;
        }

        boolean keepTrying = true;

        while (keepTrying) {
            String password = mainMenuView.promptForPassword();

            if (user.getPassword().equals(password)) {
                Optional<User> loggedInOpt = authController.login(userId, password);

                if (loggedInOpt.isEmpty()) {
                    mainMenuView.displayLoginFailure();
                } else {
                    User loggedUser = loggedInOpt.get();
                    mainMenuView.displayLoginSuccess(loggedUser);
                    dispatchUserSession(loggedUser);
                    authController.logout();
                }

                keepTrying = false;

            } else {
                mainMenuView.displayWrongPassword();
                mainMenuView.displayPasswordRetryMenu();
                int choice = mainMenuView.promptForPasswordRetrySelection();

                switch (choice) {
                    case 1: { 
                        Optional<String> tempPasswordOpt = authController.resetPassword(userId);
                        if (tempPasswordOpt.isPresent()) {
                            mainMenuView.displayTemporaryPassword(tempPasswordOpt.get());
                        } else {
                            mainMenuView.displayLoginFailure();
                        }
                        keepTrying = false; 
                        break;
                    }
                    case 2: {
                        break;
                    }
                    case 3: {
                        keepTrying = false;
                        break;
                    }
                    default: {
                        System.out.println("Invalid option. Returning to main menu.");
                        keepTrying = false;
                        break;
                    }
                }
            }
        }
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
