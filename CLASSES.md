# UML Class Diagram Documentation

## Package: com.internship.system

### Main

```
Main
---
+{static} main(args: String[]): void
```

---

## Package: com.internship.system.controller

### AppController

```
AppController
---
-dataManager: DataManager
-mainMenuView: MainMenuView
-authController: AuthController
---
+AppController()
+run(): void
-handleLogin(): void
-handleRegistration(): void
-dispatchUserSession(user: User): void
```

---

### AuthController

```
AuthController
---
-dataManager: DataManager
-currentUser: User
---
+AuthController(dataManager: DataManager)
+login(userId: String, password: String): Optional<User>
+logout(): void
+getCurrentUser(): Optional<User>
+changePassword(user: User, newPassword: String): boolean
-findUserById(userId: String): Optional<User>
```

---

### CompanyController

```
CompanyController
---
-{static} MAX_INTERNSHIPS_PER_REP: int
-{static} MAX_SLOTS: int
-dataManager: DataManager
-currentRep: CompanyRepresentative
---
+CompanyController(dataManager: DataManager, currentRep: CompanyRepresentative)
+{static} register(dataManager: DataManager, email: String, name: String, companyName: String, department: String, position: String): CompanyRepresentative
+getCurrentRep(): CompanyRepresentative
+getInternships(criteria: FilterCriteria): List<Internship>
+canCreateMoreInternships(): boolean
+createInternship(title: String, description: String, level: InternshipLevel, preferredMajor: String, openingDate: LocalDate, closingDate: LocalDate, slots: int): Optional<Internship>
+viewInternships(): List<Internship>
+updateInternship(internshipId: int, title: String, description: String, level: InternshipLevel, preferredMajor: String, openingDate: LocalDate, closingDate: LocalDate, slots: int): boolean
+toggleInternshipVisibility(internshipId: int): boolean
+viewApplicationsForInternship(internshipId: int): List<Application>
+processApplication(applicationId: int, newStatus: ApplicationStatus): boolean
-ensureOwnership(internshipId: int): Optional<Internship>
```

---

### StaffController

```
StaffController
---
-dataManager: DataManager
-currentStaff: CareerCenterStaff
---
+StaffController(dataManager: DataManager, currentStaff: CareerCenterStaff)
+getCurrentStaff(): CareerCenterStaff
+viewPendingReps(): List<CompanyRepresentative>
+approveCompanyRep(repId: String): boolean
+rejectCompanyRep(repId: String): boolean
+viewPendingInternships(): List<Internship>
+approveInternship(internshipId: int): boolean
+rejectInternship(internshipId: int): boolean
+getPendingWithdrawalRequests(): List<Application>
+processWithdrawalRequest(applicationId: int, approve: boolean): boolean
+generateReport(criteria: FilterCriteria): List<Internship>
```

---

### StudentController

```
StudentController
---
-{static} MAX_ACTIVE_APPLICATIONS: int
-dataManager: DataManager
-currentStudent: Student
---
+StudentController(dataManager: DataManager, currentStudent: Student)
+getCurrentStudent(): Student
+getVisibleInternships(criteria: FilterCriteria): List<Internship>
+viewAppliedInternships(): List<Application>
+applyForInternship(internshipId: int): boolean
+acceptOffer(applicationId: int): boolean
+rejectOffer(applicationId: int): boolean
+withdrawApplication(applicationId: int): boolean
-canApplyToInternship(internship: Internship): boolean
-isLevelEligible(internship: Internship): boolean
-hasReachedApplicationLimit(): boolean
-hasAcceptedOffer(): boolean
-withdrawOtherApplications(acceptedApplicationId: int): void
```

---

## Package: com.internship.system.data

### DataManager

```
DataManager
---
-userLoader: UserLoader
-internshipLoader: InternshipLoader
-idGenerator: IdGenerator
-students: List<Student>
-companyReps: List<CompanyRepresentative>
-staffMembers: List<CareerCenterStaff>
-internships: List<Internship>
-applications: List<Application>
-studentsById: Map<String, Student>
-companyRepsById: Map<String, CompanyRepresentative>
-staffById: Map<String, CareerCenterStaff>
-internshipsById: Map<Integer, Internship>
-applicationsById: Map<Integer, Application>
-studentFile: Path
-staffFile: Path
-companyRepFile: Path
-internshipFile: Path
-applicationFile: Path
---
+DataManager()
+loadAllData(): void
+saveAllData(): void
+getStudents(): List<Student>
+findStudentById(studentId: String): Optional<Student>
+getStaffMembers(): List<CareerCenterStaff>
+findStaffById(staffId: String): Optional<CareerCenterStaff>
+getCompanyRepresentatives(): List<CompanyRepresentative>
+findCompanyRepresentativeById(repId: String): Optional<CompanyRepresentative>
+getInternships(): List<Internship>
+findInternshipById(internshipId: int): Optional<Internship>
+getApplications(): List<Application>
+findApplicationById(applicationId: int): Optional<Application>
+getApplicationsForStudent(studentId: String): List<Application>
+getApplicationsForInternship(internshipId: int): List<Application>
+addCompanyRepresentative(representative: CompanyRepresentative): void
+updateCompanyRepresentativeApproval(repId: String, approved: boolean): void
+addInternship(internship: Internship): void
+updateInternship(internship: Internship): void
+removeInternship(internshipId: int): void
+addApplication(application: Application): void
+updateApplication(application: Application): void
+removeApplication(applicationId: int): void
+getFilteredInternships(criteria: FilterCriteria): List<Internship>
+nextInternshipId(): int
+nextApplicationId(): int
-reconcileConfirmedOffers(): void
-clearCaches(): void
```

---

### InternshipLoader

```
InternshipLoader
---
-{static} DATE_FORMATTER: DateTimeFormatter
---
+loadInternshipsFromFile(filePath: String): List<Internship>
+loadApplicationsFromFile(filePath: String): List<Application>
+saveInternshipsToFile(filePath: String, internships: List<Internship>): void
+saveApplicationsToFile(filePath: String, applications: List<Application>): void
-parseLevel(raw: String): InternshipLevel
-parseStatus(raw: String): InternshipStatus
-parseApplicationStatus(raw: String): ApplicationStatus
-parseDate(raw: String): LocalDate
-formatDate(date: LocalDate): String
-parseInt(value: String, fallback: int): int
-writeLines(filePath: String, lines: List<String>): void
```

---

### UserLoader

```
UserLoader
---
-{static} DEFAULT_PASSWORD: String
---
+loadStudentsFromFile(filePath: String): List<Student>
+loadStaffFromFile(filePath: String): List<CareerCenterStaff>
+loadCompanyRepsFromFile(filePath: String): List<CompanyRepresentative>
+saveStudentsToFile(filePath: String, students: List<Student>): void
+saveStaffToFile(filePath: String, staff: List<CareerCenterStaff>): void
+saveCompanyRepsToFile(filePath: String, reps: List<CompanyRepresentative>): void
-parseInt(value: String, fallback: int): int
-writeLines(filePath: String, lines: List<String>): void
```

---

## Package: com.internship.system.model

### Application

```
Application
---
-applicationId: int
-studentId: String
-internshipId: int
-status: ApplicationStatus
-withdrawalRequested: boolean
---
+Application(applicationId: int, studentId: String, internshipId: int, status: ApplicationStatus)
+Application(applicationId: int, studentId: String, internshipId: int, status: ApplicationStatus, withdrawalRequested: boolean)
+getApplicationId(): int
+setApplicationId(applicationId: int): void
+getStudentId(): String
+setStudentId(studentId: String): void
+getInternshipId(): int
+setInternshipId(internshipId: int): void
+getStatus(): ApplicationStatus
+setStatus(status: ApplicationStatus): void
+isWithdrawalRequested(): boolean
+setWithdrawalRequested(withdrawalRequested: boolean): void
```

---

### FilterCriteria

```
FilterCriteria
---
-status: InternshipStatus
-level: InternshipLevel
-preferredMajor: String
-closingDateBefore: LocalDate
-visibleOnly: Boolean
---
-FilterCriteria(builder: Builder)
+getStatus(): Optional<InternshipStatus>
+getLevel(): Optional<InternshipLevel>
+getPreferredMajor(): Optional<String>
+getClosingDateBefore(): Optional<LocalDate>
+getVisibleOnly(): Optional<Boolean>
+{static} builder(): Builder
```

---

### FilterCriteria.Builder

```
FilterCriteria.Builder
---
-status: InternshipStatus
-level: InternshipLevel
-preferredMajor: String
-closingDateBefore: LocalDate
-visibleOnly: Boolean
---
+status(status: InternshipStatus): Builder
+level(level: InternshipLevel): Builder
+preferredMajor(preferredMajor: String): Builder
+closingDateBefore(closingDateBefore: LocalDate): Builder
+visibleOnly(visibleOnly: Boolean): Builder
+build(): FilterCriteria
```

---

### Internship

```
Internship
---
-internshipId: int
-title: String
-description: String
-level: InternshipLevel
-preferredMajor: String
-openingDate: LocalDate
-closingDate: LocalDate
-status: InternshipStatus
-companyName: String
-representativeInChargeId: String
-slots: int
-visible: boolean
-confirmedOffers: int
---
+Internship(internshipId: int, title: String, description: String, level: InternshipLevel, preferredMajor: String, openingDate: LocalDate, closingDate: LocalDate, status: InternshipStatus, companyName: String, representativeInChargeId: String, slots: int, visible: boolean, confirmedOffers: int)
+getInternshipId(): int
+setInternshipId(internshipId: int): void
+getTitle(): String
+setTitle(title: String): void
+getDescription(): String
+setDescription(description: String): void
+getLevel(): InternshipLevel
+setLevel(level: InternshipLevel): void
+getPreferredMajor(): String
+setPreferredMajor(preferredMajor: String): void
+getOpeningDate(): LocalDate
+setOpeningDate(openingDate: LocalDate): void
+getClosingDate(): LocalDate
+setClosingDate(closingDate: LocalDate): void
+getStatus(): InternshipStatus
+setStatus(status: InternshipStatus): void
+getCompanyName(): String
+setCompanyName(companyName: String): void
+getRepresentativeInChargeId(): String
+setRepresentativeInChargeId(representativeInChargeId: String): void
+getSlots(): int
+setSlots(slots: int): void
+isVisible(): boolean
+setVisible(visible: boolean): void
+getConfirmedOffers(): int
+setConfirmedOffers(confirmedOffers: int): void
+hasAvailableSlots(): boolean
+registerConfirmedOffer(): void
+revokeConfirmedOffer(): void
+toggleVisibility(): void
+isOpenOn(date: LocalDate): boolean
```

---

## Package: com.internship.system.model.enums

### ApplicationStatus

```
<<enumeration>>
ApplicationStatus
---
PENDING
PENDING_WITHDRAWN
SUCCESSFUL_PENDING
SUCCESSFUL_ACCEPTED
SUCCESSFUL_REJECTED
SUCCESSFUL_WITHDRAWN
UNSUCCESSFUL

```

---

### InternshipLevel

```
<<enumeration>>
InternshipLevel
---
BASIC
INTERMEDIATE
ADVANCED
```

---

### InternshipStatus

```
<<enumeration>>
InternshipStatus
---
PENDING
APPROVED
REJECTED
FILLED
```

---

## Package: com.internship.system.model.user

### User

```
<<abstract>>
User
---
-userId: String
-name: String
-password: String
---
#{protected} User(userId: String, name: String, password: String)
+getUserId(): String
+getName(): String
+getPassword(): String
+setPassword(newPassword: String): void
```

---

### CareerCenterStaff

```
CareerCenterStaff
---
-staffDepartment: String
---
+CareerCenterStaff(userId: String, name: String, password: String, staffDepartment: String)
+getStaffDepartment(): String
```

---

### CompanyRepresentative

```
CompanyRepresentative
---
-companyName: String
-department: String
-position: String
-approved: boolean
---
+CompanyRepresentative(userId: String, name: String, password: String, companyName: String, department: String, position: String, approved: boolean)
+getCompanyName(): String
+getDepartment(): String
+getPosition(): String
+isApproved(): boolean
+setApproved(approved: boolean): void
```

---

### Student

```
Student
---
-yearOfStudy: int
-major: String
---
+Student(userId: String, name: String, password: String, yearOfStudy: int, major: String)
+getYearOfStudy(): int
+getMajor(): String
```

---

## Package: com.internship.system.util

### ConsoleInput

```
<<final>>
ConsoleInput
---
-{static} SCANNER: Scanner
---
-ConsoleInput()
+{static} readLine(prompt: String): String
+{static} readInt(prompt: String): int
+{static} readYesNo(prompt: String): boolean
```

---

### CsvUtils

```
<<final>>
CsvUtils
---
---
-CsvUtils()
+{static} parseLine(line: String): List<String>
+{static} toLine(values: List<String>): String
-{static} escape(value: String): String
```

---

### IdGenerator

```
IdGenerator
---
-counters: Map<String, Integer>
---
+{synchronized} next(key: String): int
+{synchronized} seed(key: String, value: int): void
```

---

## Package: com.internship.system.view

### CompanyView

```
CompanyView
---
-companyController: CompanyController
-authController: AuthController
-filterCriteria: FilterCriteria
---
+CompanyView(companyController: CompanyController, authController: AuthController)
+run(): void
-displayMenu(): void
-handleManageInternships(): void
-handleUpdateInternshipDetails(internshipId: int): void
-handleToggleVisibilityForId(internshipId: int, internships: List<Internship>): void
-handleSetFilters(): void
-handleCreateInternship(): void
-handleManageApplications(): void
-handleChangePassword(): void
-promptForLevel(): InternshipLevel
-promptForDate(prompt: String): LocalDate
-promptForSlots(): int
```

---

### MainMenuView

```
MainMenuView
---
---
+displaySplash(): void
+displayMainOptions(): void
+promptForMainSelection(): int
+promptForUserId(): String
+promptForPassword(): String
+promptForEmail(): String
+promptForName(): String
+promptForCompanyName(): String
+promptForDepartment(): String
+promptForPosition(): String
+displayLoginSuccess(user: User): void
+displayLoginFailure(): void
+displayRegistrationSuccess(): void
+displayRegistrationFailure(message: String): void
+displayExitMessage(): void
```

---

### StaffView

```
StaffView
---
-staffController: StaffController
-authController: AuthController
-filterCriteria: FilterCriteria
---
+StaffView(staffController: StaffController, authController: AuthController)
+run(): void
-displayMenu(): void
-handleManagePendingReps(): void
-handleManagePendingInternships(): void
-handleManageWithdrawalRequests(): void
-handleGenerateReport(): void
-handleSetFilters(): void
-handleChangePassword(): void
-promptForDate(prompt: String): LocalDate
```

---

### StudentView

```
StudentView
---
-studentController: StudentController
-authController: AuthController
-filterCriteria: FilterCriteria
---
+StudentView(studentController: StudentController, authController: AuthController)
+run(): void
-displayMenu(): void
-handleBrowseAndApply(): void
-handleSetFilters(): void
-handleManageApplications(): void
-handleChangePassword(): void
```

---
