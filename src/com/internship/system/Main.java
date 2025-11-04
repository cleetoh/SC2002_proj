package com.internship.system;

import com.internship.system.controller.AppController;

public class Main {
    public static void main(String[] args) {
        AppController appController = new AppController();
        appController.run();
    }
}
