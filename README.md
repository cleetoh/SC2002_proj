# Internship Placement Management System

## 1. Overview

The Internship Placement Management System is a command-line interface (CLI) application designed to serve as a centralized hub for Students, Company Representatives, and Career Center Staff. It facilitates the process of internship posting, application, and management. The system is built with a focus on Object-Oriented Design and Programming (OODP) principles, without the use of databases, JSON, or XML for data persistence.

## 2. System Architecture

The application follows a **Layered Architecture** integrated with the **Model-View-Controller (MVC)** pattern. This design ensures a clear separation of concerns, enhancing maintainability, scalability, and ease of development.

- **Presentation Layer (View):** Responsible for all user interactions via the command line. This includes displaying menus, formatting data for output, and capturing user input. It does not contain any business logic.
- **Business Logic Layer (Controller/Service):** The core of the application. It handles user input from the View, processes it according to the business rules, manipulates the Model, and determines the next view to be displayed.
- **Data Access Layer (Model & Persistence):** Manages the application's data. It includes the data entities (Model) and the logic for reading from and writing to CSV files (Persistence).

## 3. Application File Structure

The project is organized into packages to reflect the layered architecture.

```
com.internship.system
│
├── Main.java              // The entry point of the application
│
├── controller/            // Business Logic Layer
│   ├── AppController.java   // Main controller to manage application flow
│   ├── AuthController.java  // Handles login, logout, password changes
│   ├── StudentController.java // Handles student-specific actions
│   ├── CompanyController.java // Handles company rep actions
│   └── StaffController.java   // Handles career center staff actions
│
├── model/                 // Data Entities
│   ├── user/              // User-related classes
│   │   ├── User.java        // Abstract base class for all users
│   │   ├── Student.java     // Extends User
│   │   ├── CompanyRepresentative.java // Extends User
│   │   └── CareerCenterStaff.java // Extends User
│   │
│   ├── Internship.java    // Represents an internship opportunity
│   ├── Application.java   // Represents a student's application for an internship
│   ├── FilterCriteria.java// Represents filtering criteria for reports
│   └── enums/             // Enumerations for fixed sets of values
│       ├── InternshipLevel.java  // (BASIC, INTERMEDIATE, ADVANCED)
│       ├── ApplicationStatus.java // (PENDING, SUCCESSFUL_PENDING, SUCCESSFUL_ACCEPTED, SUCCESSFUL_REJECTED, SUCCESSFUL_WITHDRAWN, UNSUCCESSFUL)
│       └── InternshipStatus.java // (PENDING, APPROVED, REJECTED, FILLED)
│
├── view/                  // Presentation Layer
│   ├── MainMenuView.java    // Displays the main menu after login
│   ├── StudentView.java     // Displays menus and views for students
│   ├── CompanyView.java     // Displays menus and views for company reps
│   └── StaffView.java       // Displays menus and views for staff
│
├── data/                  // Data Access Layer
│   ├── DataManager.java     // Central class to manage in-memory data
│   ├── UserLoader.java      // Logic to read/write user data from files
│   └── InternshipLoader.java // Logic to read/write internship/application data
│
└── util/                  // Utility classes
    ├── ConsoleInput.java    // Handles console input
    ├── CsvUtils.java        // Utilities for CSV parsing and generation
    └── IdGenerator.java     // Generates unique IDs
```

## 4. Class Specifications

### Model Package (`com.internship.system.model`)

#### `user.User` (Abstract Class)

- **Description:** A base class for all user types in the system.
- **Attributes:**
  - `String userId`
  - `String name`
  - `String password`
- **Methods:**
  - `getUserId()`: `String`
  - `getName()`: `String`
  - `getPassword()`: `String`
  - `setPassword(String newPassword)`: `void`

#### `user.Student` (extends `User`)

- **Description:** Represents a student user.
- **Attributes:**
  - `int yearOfStudy`
  - `String major`
- **Methods:**
  - `getYearOfStudy()`: `int`
  - `getMajor()`: `String`

#### `user.CompanyRepresentative` (extends `User`)

- **Description:** Represents a company representative user.
- **Attributes:**
  - `String companyName`
  - `String department`
  - `String position`
  - `boolean approved`
- **Methods:**
  - `getCompanyName()`: `String`
  - `getDepartment()`: `String`
  - `getPosition()`: `String`
  - `isApproved()`: `boolean`
  - `setApproved(boolean status)`: `void`

#### `user.CareerCenterStaff` (extends `User`)

- **Description:** Represents a career center staff member.
- **Attributes:**
  - `String staffDepartment`
- **Methods:**
  - `getStaffDepartment()`: `String`

#### `Internship`

- **Description:** Represents an internship opportunity.
- **Attributes:**
  - `int internshipId`
  - `String title`
  - `String description`
  - `InternshipLevel level`
  - `String preferredMajor`
  - `LocalDate openingDate`
  - `LocalDate closingDate`
  - `InternshipStatus status`
  - `String companyName`
  - `String representativeInChargeId`
  - `int slots`
  - `boolean visible`
  - `int confirmedOffers`
- **Methods:**
  - Getters and setters for all attributes.
  - `toggleVisibility()`: `void`
  - `hasAvailableSlots()`: `boolean`
  - `registerConfirmedOffer()`: `void`
  - `revokeConfirmedOffer()`: `void`
  - `isOpenOn(LocalDate date)`: `boolean`

#### `Application`

- **Description:** Represents a student's application to an internship.
- **Attributes:**
  - `int applicationId`
  - `String studentId`
  - `int internshipId`
  - `ApplicationStatus status`
  - `boolean withdrawalRequested`
- **Methods:**
  - Getters and setters for all attributes.

#### Application Status Flow

The application status follows this lifecycle:

1. **PENDING** - Initial state when student submits application. Company representative has not yet reviewed it.

2. **SUCCESSFUL_PENDING** - Company representative has approved the application and extended an offer. Student has not yet responded.

3. **SUCCESSFUL_ACCEPTED** - Student has accepted the offer from SUCCESSFUL_PENDING status. This registers a confirmed offer.

4. **SUCCESSFUL_REJECTED** - Student has rejected the offer from SUCCESSFUL_PENDING status.

5. **SUCCESSFUL_WITHDRAWN** - Application has been withdrawn. This can happen when:
   - Student withdraws an accepted offer (SUCCESSFUL_ACCEPTED) - immediately processed, revokes confirmed offer
   - Student requests withdrawal of other statuses (PENDING, SUCCESSFUL_PENDING) - requires staff approval
   - When staff approves any withdrawal request, the status becomes SUCCESSFUL_WITHDRAWN

6. **UNSUCCESSFUL** - Company representative has rejected the application.

#### `FilterCriteria`

- **Description:** Represents the criteria for filtering internships when generating reports.
- **Attributes:**
  - `InternshipStatus status`
  - `InternshipLevel level`
  - `String preferredMajor`
  - `LocalDate closingDateBefore`
  - `Boolean visibleOnly`
- **Methods:**
  - Uses a builder pattern for instantiation.
  - Getters for all attributes return `Optional`.

### Data Package (`com.internship.system.data`)

#### `DataManager`

- **Description:** Holds all application data in memory after loading it from files.
- **Attributes:**
  - `List<Student> students`
  - `List<CompanyRepresentative> companyReps`
  - `List<CareerCenterStaff> staffMembers`
  - `List<Internship> internships`
  - `List<Application> applications`
- **Methods:**
  - `loadAllData()`: `void`
  - `saveAllData()`: `void`
  - Methods to get, find, add, update, and remove users, internships, and applications.
  - `nextInternshipId()`: `int`
  - `nextApplicationId()`: `int`

#### `UserLoader`

- **Description:** Handles reading and writing user data to/from CSV files.
- **Methods:**
  - `loadStudentsFromFile(String filePath)`: `List<Student>`
  - `loadStaffFromFile(String filePath)`: `List<CareerCenterStaff>`
  - `loadCompanyRepsFromFile(String filePath)`: `List<CompanyRepresentative>`
  - `saveStudentsToFile(...)`: `void`
  - `saveStaffToFile(...)`: `void`
  - `saveCompanyRepsToFile(...)`: `void`

#### `InternshipLoader`

- **Description:** Handles reading and writing internship and application data.
- **Methods:**
  - `loadInternshipsFromFile(String filePath)`: `List<Internship>`
  - `saveInternshipsToFile(...)`: `void`
  - `loadApplicationsFromFile(String filePath)`: `List<Application>`
  - `saveApplicationsToFile(...)`: `void`

### Controller Package (`com.internship.system.controller`)

#### `AppController`

- **Description:** The main controller that manages application flow. It initializes the system and delegates tasks to specialized controllers after user authentication.
- **Attributes:**
  - `DataManager dataManager`
  - `MainMenuView mainMenuView`
  - `AuthController authController`
- **Methods:**
  - `run()`: `void` - Starts the main application loop, handles login/registration, and directs to the appropriate user menu.

#### `AuthController`

- **Attributes:**
  - `User currentUser`
  - `DataManager dataManager`
- **Methods:**
  - `login(String userId, String password)`: `Optional<User>`
  - `logout()`: `void`
  - `getCurrentUser()`: `Optional<User>`
  - `changePassword(User user, String newPassword)`: `boolean`

#### `StudentController`

- **Description:** Manages all actions for a logged-in student. It is stateful and holds the context of the current student.
- **Attributes:**
  - `Student currentStudent`
  - `DataManager dataManager`
- **Methods:**
  - `getVisibleInternships()`: `List<Internship>`
  - `applyForInternship(int internshipId)`: `boolean`
  - `viewAppliedInternships()`: `List<Application>`
  - `acceptOffer(int applicationId)`: `boolean`
  - `withdrawApplication(int applicationId)`: `boolean`

#### `CompanyController`

- **Description:** Manages all actions for a logged-in company representative. It is stateful.
- **Attributes:**
  - `CompanyRepresentative currentRep`
  - `DataManager dataManager`
- **Methods:**
  - `register(DataManager dataManager, String email, ...)`: `CompanyRepresentative` (static)
  - `createInternship(...)`: `Optional<Internship>`
  - `viewInternships()`: `List<Internship>`
  - `updateInternship(...)`: `boolean`
  - `toggleInternshipVisibility(int internshipId)`: `boolean`
  - `viewApplicationsForInternship(int internshipId)`: `List<Application>`
  - `processApplication(int applicationId, ApplicationStatus newStatus)`: `boolean`

#### `StaffController`

- **Description:** Manages all actions for a logged-in staff member. It is stateful.
- **Attributes:**
  - `CareerCenterStaff currentStaff`
  - `DataManager dataManager`
- **Methods:**
  - `approveCompanyRep(String repId)`: `boolean`
  - `rejectCompanyRep(String repId)`: `boolean`
  - `viewPendingReps()`: `List<CompanyRepresentative>`
  - `approveInternship(int internshipId)`: `boolean`
  - `rejectInternship(int internshipId)`: `boolean`
  - `viewPendingInternships()`: `List<Internship>`
  - `getPendingWithdrawalRequests()`: `List<Application>`
  - `processWithdrawalRequest(int applicationId, boolean approve)`: `boolean`
  - `generateReport(FilterCriteria criteria)`: `List<Internship>`

### View Package (`com.internship.system.view`)

Each view class is responsible for displaying a specific set of menus and information. They contain a `run()` method to start their respective user interaction loops.

- **Methods:**
  - `displayMenu()`: `void`
  - `displayInternshipList(List<Internship> internships)`: `void`
  - `displayApplicationList(List<Application> applications)`: `void`
  - Methods to prompt for user input (`promptForLogin()`, `promptForSelection()`, etc.).
  - `displaySuccessMessage(String message)`: `void`
  - `displayErrorMessage(String message)`: `void`

### Util Package (`com.internship.system.util`)

- **`ConsoleInput`**: A utility class for handling user input from the console.
- **`CsvUtils`**: A utility class for parsing and creating CSV-formatted strings.
- **`IdGenerator`**: A class for generating auto-incrementing IDs for internships and applications.

## 5. Assumptions and Clarifications

- **ID Generation:** `internshipId` and `applicationId` are generated by the `IdGenerator` class, which produces a simple auto-incrementing integer sequence.
- **Date Format:** All dates are handled as `LocalDate` objects, and formatted as `YYYY-MM-DD` in CSV files.
- **Data Files:** The application will assume that the initial data files (`student_list.csv`, `staff_list.csv`, `company_representative_list.csv`, `internships.csv`, `applications.csv`) are present in the project's root directory at startup.
- **User State:** Filter settings for viewing internships are temporarily stored in the respective controller (e.g., `StudentController`) for the duration of a user's session.
- **User Deletion:** The current requirements do not specify functionality for deleting users. As such, it is assumed that user accounts are persistent.

## 6. Commands

javac -d out $(find src -name "*.java")
java -cp out com.internship.system.Main
