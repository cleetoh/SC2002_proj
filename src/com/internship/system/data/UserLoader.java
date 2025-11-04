package com.internship.system.data;

import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.util.CsvUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserLoader {
    private static final String DEFAULT_PASSWORD = "password";

    public List<Student> loadStudentsFromFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<Student> students = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) {
                List<String> tokens = CsvUtils.parseLine(lines.get(i));
                if (tokens.size() < 4) {
                    continue;
                }
                String studentId = tokens.get(0);
                String name = tokens.get(1);
                String major = tokens.get(2);
                int year = parseInt(tokens.get(3), 1);
                students.add(new Student(studentId, name, DEFAULT_PASSWORD, year, major));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load students from " + filePath, e);
        }
        return students;
    }

    public List<CareerCenterStaff> loadStaffFromFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<CareerCenterStaff> staff = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) {
                List<String> tokens = CsvUtils.parseLine(lines.get(i));
                if (tokens.size() < 4) {
                    continue;
                }
                String staffId = tokens.get(0);
                String name = tokens.get(1);
                String department = tokens.get(3);
                staff.add(new CareerCenterStaff(staffId, name, DEFAULT_PASSWORD, department));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load staff from " + filePath, e);
        }
        return staff;
    }

    public List<CompanyRepresentative> loadCompanyRepsFromFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<CompanyRepresentative> reps = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) {
                List<String> tokens = CsvUtils.parseLine(lines.get(i));
                if (tokens.size() < 6) {
                    continue;
                }
                String repId = tokens.get(0);
                String name = tokens.get(1);
                String companyName = tokens.get(2);
                String department = tokens.get(3);
                String position = tokens.get(4);
                boolean approved = tokens.size() > 6 && tokens.get(6).trim().equalsIgnoreCase("approved");
                reps.add(new CompanyRepresentative(repId, name, DEFAULT_PASSWORD, companyName, department, position,
                        approved));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load company representatives from " + filePath, e);
        }
        return reps;
    }

    public void saveStudentsToFile(String filePath, List<Student> students) {
        List<String> lines = new ArrayList<>();
        lines.add("StudentID,Name,Major,Year,Email");
        for (Student student : students) {
            List<String> values = new ArrayList<>();
            values.add(student.getUserId());
            values.add(student.getName());
            values.add(student.getMajor());
            values.add(String.valueOf(student.getYearOfStudy()));
            values.add("");
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }

    public void saveStaffToFile(String filePath, List<CareerCenterStaff> staff) {
        List<String> lines = new ArrayList<>();
        lines.add("StaffID,Name,Role,Department,Email");
        for (CareerCenterStaff member : staff) {
            List<String> values = new ArrayList<>();
            values.add(member.getUserId());
            values.add(member.getName());
            values.add("Career Center Staff");
            values.add(member.getStaffDepartment());
            values.add("");
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }

    public void saveCompanyRepsToFile(String filePath, List<CompanyRepresentative> reps) {
        List<String> lines = new ArrayList<>();
        lines.add("CompanyRepID,Name,CompanyName,Department,Position,Email,Status");
        for (CompanyRepresentative rep : reps) {
            List<String> values = new ArrayList<>();
            values.add(rep.getUserId());
            values.add(rep.getName());
            values.add(rep.getCompanyName());
            values.add(rep.getDepartment());
            values.add(rep.getPosition());
            values.add(rep.getUserId());
            values.add(rep.isApproved() ? "Approved" : "Pending");
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private void writeLines(String filePath, List<String> lines) {
        try {
            Files.write(Path.of(filePath), lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to " + filePath, e);
        }
    }
}
