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

public class DataManager {
    private final UserLoader userLoader = new UserLoader();
    private final InternshipLoader internshipLoader = new InternshipLoader();
    private final IdGenerator idGenerator = new IdGenerator();

    private final List<Student> students = new ArrayList<>();
    private final List<CompanyRepresentative> companyReps = new ArrayList<>();
    private final List<CareerCenterStaff> staffMembers = new ArrayList<>();
    private final List<Internship> internships = new ArrayList<>();
    private final List<Application> applications = new ArrayList<>();

    private final Map<String, Student> studentsById = new HashMap<>();
    private final Map<String, CompanyRepresentative> companyRepsById = new HashMap<>();
    private final Map<String, CareerCenterStaff> staffById = new HashMap<>();
    private final Map<Integer, Internship> internshipsById = new HashMap<>();
    private final Map<Integer, Application> applicationsById = new HashMap<>();

    private final Path studentFile;
    private final Path staffFile;
    private final Path companyRepFile;
    private final Path internshipFile;
    private final Path applicationFile;

    public DataManager() {
        Path baseDir = Path.of("").toAbsolutePath();
        this.studentFile = baseDir.resolve("student_list.csv");
        this.staffFile = baseDir.resolve("staff_list.csv");
        this.companyRepFile = baseDir.resolve("company_representative_list.csv");
        this.internshipFile = baseDir.resolve("internships.csv");
        this.applicationFile = baseDir.resolve("applications.csv");
    }

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

    public void saveAllData() {
        userLoader.saveStudentsToFile(studentFile.toString(), students);
        userLoader.saveStaffToFile(staffFile.toString(), staffMembers);
        userLoader.saveCompanyRepsToFile(companyRepFile.toString(), companyReps);
        internshipLoader.saveInternshipsToFile(internshipFile.toString(), internships);
        internshipLoader.saveApplicationsToFile(applicationFile.toString(), applications);
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    public Optional<Student> findStudentById(String studentId) {
        return Optional.ofNullable(studentsById.get(studentId));
    }

    public List<CareerCenterStaff> getStaffMembers() {
        return Collections.unmodifiableList(staffMembers);
    }

    public Optional<CareerCenterStaff> findStaffById(String staffId) {
        return Optional.ofNullable(staffById.get(staffId));
    }

    public List<CompanyRepresentative> getCompanyRepresentatives() {
        return Collections.unmodifiableList(companyReps);
    }

    public Optional<CompanyRepresentative> findCompanyRepresentativeById(String repId) {
        return Optional.ofNullable(companyRepsById.get(repId));
    }

    public List<Internship> getInternships() {
        return Collections.unmodifiableList(internships);
    }

    public Optional<Internship> findInternshipById(int internshipId) {
        return Optional.ofNullable(internshipsById.get(internshipId));
    }

    public List<Application> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    public Optional<Application> findApplicationById(int applicationId) {
        return Optional.ofNullable(applicationsById.get(applicationId));
    }

    public List<Application> getApplicationsForStudent(String studentId) {
        return applications.stream()
                .filter(application -> application.getStudentId().equals(studentId))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Application> getApplicationsForInternship(int internshipId) {
        return applications.stream()
                .filter(application -> application.getInternshipId() == internshipId)
                .collect(Collectors.toUnmodifiableList());
    }

    public void addCompanyRepresentative(CompanyRepresentative representative) {
        companyReps.add(representative);
        companyRepsById.put(representative.getUserId(), representative);
    }

    public void updateCompanyRepresentativeApproval(String repId, boolean approved) {
        CompanyRepresentative representative = companyRepsById.get(repId);
        if (representative != null) {
            representative.setApproved(approved);
        }
    }

    public void addInternship(Internship internship) {
        internships.add(internship);
        internshipsById.put(internship.getInternshipId(), internship);
    }

    public void updateInternship(Internship internship) {
        internshipsById.put(internship.getInternshipId(), internship);
    }

    public void removeInternship(int internshipId) {
        internships.removeIf(internship -> internship.getInternshipId() == internshipId);
        internshipsById.remove(internshipId);
    }

    public void addApplication(Application application) {
        applications.add(application);
        applicationsById.put(application.getApplicationId(), application);
    }

    public void updateApplication(Application application) {
        applicationsById.put(application.getApplicationId(), application);
    }

    public void removeApplication(int applicationId) {
        applications.removeIf(application -> application.getApplicationId() == applicationId);
        applicationsById.remove(applicationId);
    }

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

    public int nextInternshipId() {
        return idGenerator.next("internship");
    }

    public int nextApplicationId() {
        return idGenerator.next("application");
    }

    private void reconcileConfirmedOffers() {
        Map<Integer, Long> acceptedCounts = applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.SUCCESSFUL_ACCEPTED)
                .collect(Collectors.groupingBy(
                        Application::getInternshipId,
                        Collectors.counting()));

        // For each student, check if they have exactly one SUCCESSFUL_ACCEPTED with all
        // others withdrawn
        // This indicates they've actually accepted (not just been offered)
        Map<Integer, Long> confirmedCounts = applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.SUCCESSFUL_ACCEPTED)
                .filter(application -> {
                    List<Application> studentApps = getApplicationsForStudent(application.getStudentId());
                    long activeCount = studentApps.stream()
                            .filter(app -> app.getStatus() == ApplicationStatus.PENDING
                                    || app.getStatus() == ApplicationStatus.SUCCESSFUL_ACCEPTED
                                    || app.getStatus() == ApplicationStatus.SUCCESSFUL_REJECTED)
                            .count();
                    return activeCount == 1; // Only this application is active
                })
                .collect(Collectors.groupingBy(Application::getInternshipId, Collectors.counting()));

        confirmedCounts.forEach((internshipId, count) -> findInternshipById(internshipId).ifPresent(internship -> {
            internship.setConfirmedOffers(count.intValue());
            if (internship.getConfirmedOffers() >= internship.getSlots()) {
                internship.setStatus(InternshipStatus.FILLED);
            }
        }));
    }

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
