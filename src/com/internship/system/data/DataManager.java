package com.internship.system.data;

import com.internship.system.model.Application;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.util.IdGenerator;
import com.internship.system.model.FilterCriteria;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Central data management class for the internship system.
 * Handles loading, saving, and querying of all entities (users, internships, applications).
 * Maintains in-memory caches with ID-based lookups for efficient access.
 */
public class DataManager {
    /** Loader for user data (students, staff, company representatives). */
    private final UserLoader userLoader = new UserLoader();
    /** Loader for internship and application data. */
    private final InternshipLoader internshipLoader = new InternshipLoader();
    /** Generator for unique IDs. */
    private final IdGenerator idGenerator = new IdGenerator();

    /** In-memory list of all students. */
    private final List<Student> students = new ArrayList<>();
    /** In-memory list of all company representatives. */
    private final List<CompanyRepresentative> companyReps = new ArrayList<>();
    /** In-memory list of all career center staff. */
    private final List<CareerCenterStaff> staffMembers = new ArrayList<>();
    /** In-memory list of all internships. */
    private final List<Internship> internships = new ArrayList<>();
    /** In-memory list of all applications. */
    private final List<Application> applications = new ArrayList<>();

    /** Map for fast student lookup by ID. */
    private final Map<String, Student> studentsById = new HashMap<>();
    /** Map for fast company representative lookup by ID. */
    private final Map<String, CompanyRepresentative> companyRepsById = new HashMap<>();
    /** Map for fast staff lookup by ID. */
    private final Map<String, CareerCenterStaff> staffById = new HashMap<>();
    /** Map for fast internship lookup by ID. */
    private final Map<Integer, Internship> internshipsById = new HashMap<>();
    /** Map for fast application lookup by ID. */
    private final Map<Integer, Application> applicationsById = new HashMap<>();

    /** Path to the students CSV file. */
    private final Path studentFile;
    /** Path to the staff CSV file. */
    private final Path staffFile;
    /** Path to the company representatives CSV file. */
    private final Path companyRepFile;
    /** Path to the internships CSV file. */
    private final Path internshipFile;
    /** Path to the applications CSV file. */
    private final Path applicationFile;

    /**
     * Constructs a new DataManager and initializes file paths.
     * Files are expected to be in the current working directory.
     */
    public DataManager() {
        Path baseDir = Path.of("").toAbsolutePath();
        this.studentFile = baseDir.resolve("student_list.csv");
        this.staffFile = baseDir.resolve("staff_list.csv");
        this.companyRepFile = baseDir.resolve("company_representative_list.csv");
        this.internshipFile = baseDir.resolve("internships.csv");
        this.applicationFile = baseDir.resolve("applications.csv");
    }

    /**
     * Loads all data from CSV files into memory.
     * Clears existing caches, loads all entities, builds ID maps, and reconciles confirmed offers.
     */
    public void loadAllData() {
        clearCaches();

        students.addAll(userLoader.loadStudentsFromFile(studentFile.toString()));
        for (Student student : students) {
            studentsById.put(student.getUserId(), student);
        }

        staffMembers.addAll(userLoader.loadStaffFromFile(staffFile.toString()));
        for (CareerCenterStaff staff : staffMembers) {
            staffById.put(staff.getUserId(), staff);
        }

        companyReps.addAll(userLoader.loadCompanyRepsFromFile(companyRepFile.toString()));
        for (CompanyRepresentative rep : companyReps) {
            companyRepsById.put(rep.getUserId(), rep);
        }

        internships.addAll(internshipLoader.loadInternshipsFromFile(internshipFile.toString()));
        int maxInternshipId = 0;
        for (Internship internship : internships) {
            internshipsById.put(internship.getInternshipId(), internship);
            maxInternshipId = Math.max(maxInternshipId, internship.getInternshipId());
        }
        idGenerator.seed("internship", maxInternshipId);

        applications.addAll(internshipLoader.loadApplicationsFromFile(applicationFile.toString()));
        int maxApplicationId = 0;
        for (Application application : applications) {
            applicationsById.put(application.getApplicationId(), application);
            maxApplicationId = Math.max(maxApplicationId, application.getApplicationId());
        }
        idGenerator.seed("application", maxApplicationId);

        reconcileConfirmedOffers();
    }

    /**
     * Saves all in-memory data to CSV files.
     * Writes students, staff, company representatives, internships, and applications.
     */
    public void saveAllData() {
        userLoader.saveStudentsToFile(studentFile.toString(), students);
        userLoader.saveStaffToFile(staffFile.toString(), staffMembers);
        userLoader.saveCompanyRepsToFile(companyRepFile.toString(), companyReps);
        internshipLoader.saveInternshipsToFile(internshipFile.toString(), internships);
        internshipLoader.saveApplicationsToFile(applicationFile.toString(), applications);
    }

    /**
     * Gets all students.
     *
     * @return unmodifiable list of all students
     */
    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    /**
     * Finds a student by ID.
     *
     * @param studentId the student ID to search for
     * @return Optional containing the student if found, empty otherwise
     */
    public Optional<Student> findStudentById(String studentId) {
        return Optional.ofNullable(studentsById.get(studentId));
    }

    /**
     * Gets all career center staff members.
     *
     * @return unmodifiable list of all staff members
     */
    public List<CareerCenterStaff> getStaffMembers() {
        return Collections.unmodifiableList(staffMembers);
    }

    /**
     * Finds a staff member by ID.
     *
     * @param staffId the staff ID to search for
     * @return Optional containing the staff member if found, empty otherwise
     */
    public Optional<CareerCenterStaff> findStaffById(String staffId) {
        return Optional.ofNullable(staffById.get(staffId));
    }

    /**
     * Gets all company representatives.
     *
     * @return unmodifiable list of all company representatives
     */
    public List<CompanyRepresentative> getCompanyRepresentatives() {
        return Collections.unmodifiableList(companyReps);
    }

    /**
     * Finds a company representative by ID.
     *
     * @param repId the representative ID to search for
     * @return Optional containing the representative if found, empty otherwise
     */
    public Optional<CompanyRepresentative> findCompanyRepresentativeById(String repId) {
        return Optional.ofNullable(companyRepsById.get(repId));
    }

    /**
     * Gets all internships.
     *
     * @return unmodifiable list of all internships
     */
    public List<Internship> getInternships() {
        return Collections.unmodifiableList(internships);
    }

    /**
     * Finds an internship by ID.
     *
     * @param internshipId the internship ID to search for
     * @return Optional containing the internship if found, empty otherwise
     */
    public Optional<Internship> findInternshipById(int internshipId) {
        return Optional.ofNullable(internshipsById.get(internshipId));
    }

    /**
     * Gets all applications.
     *
     * @return unmodifiable list of all applications
     */
    public List<Application> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    /**
     * Finds an application by ID.
     *
     * @param applicationId the application ID to search for
     * @return Optional containing the application if found, empty otherwise
     */
    public Optional<Application> findApplicationById(int applicationId) {
        return Optional.ofNullable(applicationsById.get(applicationId));
    }

    /**
     * Gets all applications for a specific student.
     *
     * @param studentId the student ID
     * @return unmodifiable list of applications for that student
     */
    public List<Application> getApplicationsForStudent(String studentId) {
        return applications.stream()
                .filter(application -> application.getStudentId().equals(studentId))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all applications for a specific internship.
     *
     * @param internshipId the internship ID
     * @return unmodifiable list of applications for that internship
     */
    public List<Application> getApplicationsForInternship(int internshipId) {
        return applications.stream()
                .filter(application -> application.getInternshipId() == internshipId)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Adds a new company representative to the system.
     *
     * @param representative the representative to add
     */
    public void addCompanyRepresentative(CompanyRepresentative representative) {
        companyReps.add(representative);
        companyRepsById.put(representative.getUserId(), representative);
    }

    /**
     * Updates the approval status of a company representative.
     *
     * @param repId the representative ID
     * @param approved the new approval status
     */
    public void updateCompanyRepresentativeApproval(String repId, boolean approved) {
        CompanyRepresentative representative = companyRepsById.get(repId);
        if (representative != null) {
            representative.setApproved(approved);
        }
    }

    /**
     * Adds a new internship to the system.
     *
     * @param internship the internship to add
     */
    public void addInternship(Internship internship) {
        internships.add(internship);
        internshipsById.put(internship.getInternshipId(), internship);
    }

    /**
     * Updates an existing internship in the system.
     *
     * @param internship the internship with updated data
     */
    public void updateInternship(Internship internship) {
        internshipsById.put(internship.getInternshipId(), internship);
    }

    /**
     * Removes an internship from the system.
     *
     * @param internshipId the ID of the internship to remove
     */
    public void removeInternship(int internshipId) {
        internships.removeIf(internship -> internship.getInternshipId() == internshipId);
        internshipsById.remove(internshipId);
    }

    /**
     * Adds a new application to the system.
     *
     * @param application the application to add
     */
    public void addApplication(Application application) {
        applications.add(application);
        applicationsById.put(application.getApplicationId(), application);
    }

    /**
     * Updates an existing application in the system.
     *
     * @param application the application with updated data
     */
    public void updateApplication(Application application) {
        applicationsById.put(application.getApplicationId(), application);
    }

    /**
     * Removes an application from the system.
     *
     * @param applicationId the ID of the application to remove
     */
    public void removeApplication(int applicationId) {
        applications.removeIf(application -> application.getApplicationId() == applicationId);
        applicationsById.remove(applicationId);
    }

    /**
     * Gets internships filtered by the specified criteria.
     *
     * @param criteria the filtering criteria
     * @return list of internships matching the criteria, sorted by title
     */
    public List<Internship> getFilteredInternships(FilterCriteria criteria) {
        return internships.stream()
                .filter(internship -> criteria.getStatus().map(status -> internship.getStatus() == status).orElse(true))
                .filter(internship -> criteria.getLevel().map(level -> internship.getLevel() == level).orElse(true))
                .filter(internship -> criteria.getPreferredMajor()
                        .map(major -> internship.getPreferredMajor().equalsIgnoreCase(major))
                        .orElse(true))
                .filter(internship -> criteria.getCompanyName()
                        .map(company -> internship.getCompanyName().equalsIgnoreCase(company))
                        .orElse(true))
                .filter(internship -> criteria.getClosingDateBefore()
                        .map(date -> internship.getClosingDate() != null && !internship.getClosingDate().isAfter(date))
                        .orElse(true))
                .filter(internship -> criteria.getVisibleOnly()
                        .map(visible -> visible == internship.isVisible())
                        .orElse(true))
                .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .collect(Collectors.toList());
    }

    /**
     * Generates the next available internship ID.
     *
     * @return the next internship ID
     */
    public int nextInternshipId() {
        return idGenerator.next("internship");
    }

    /**
     * Generates the next available application ID.
     *
     * @return the next application ID
     */
    public int nextApplicationId() {
        return idGenerator.next("application");
    }

    /**
     * Reconciles confirmed offers count for internships based on accepted applications.
     * Updates internship status to FILLED if all slots are taken.
     */
    private void reconcileConfirmedOffers() {
        Map<Integer, Long> acceptedCounts = applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.SUCCESSFUL_ACCEPTED)
                .collect(Collectors.groupingBy(
                        Application::getInternshipId,
                        Collectors.counting()));
                        
        Map<Integer, Long> confirmedCounts = applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.SUCCESSFUL_ACCEPTED)
                .filter(application -> {
                    List<Application> studentApps = getApplicationsForStudent(application.getStudentId());
                    long activeCount = studentApps.stream()
                            .filter(app -> app.getStatus() == ApplicationStatus.PENDING
                                    || app.getStatus() == ApplicationStatus.SUCCESSFUL_ACCEPTED
                                    || app.getStatus() == ApplicationStatus.SUCCESSFUL_REJECTED)
                            .count();
                    return activeCount == 1; 
                })
                .collect(Collectors.groupingBy(Application::getInternshipId, Collectors.counting()));

        confirmedCounts.forEach((internshipId, count) -> findInternshipById(internshipId).ifPresent(internship -> {
            internship.setConfirmedOffers(count.intValue());
            if (internship.getConfirmedOffers() >= internship.getSlots()) {
                internship.setStatus(InternshipStatus.FILLED);
            }
        }));
    }

    /**
     * Clears all in-memory caches.
     * Used when reloading data from files.
     */
    private void clearCaches() {
        students.clear();
        companyReps.clear();
        staffMembers.clear();
        internships.clear();
        applications.clear();

        studentsById.clear();
        companyRepsById.clear();
        staffById.clear();
        internshipsById.clear();
        applicationsById.clear();
    }
}
