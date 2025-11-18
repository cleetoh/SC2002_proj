package com.internship.system.data;

import com.internship.system.model.user.CareerCenterStaff;
import com.internship.system.model.user.CompanyRepresentative;
import com.internship.system.model.user.Student;
import com.internship.system.util.CsvUtils;
import com.internship.system.util.PasswordValidator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles loading and saving of user data (students, staff, company representatives) from/to CSV files.
 */
public class UserLoader {
    /** Default password constant (deprecated, use PasswordValidator.getDefaultPassword() instead). */
    private static final String DEFAULT_PASSWORD = "password";

    /**
     * Loads students from a CSV file.
     *
     * @param filePath path to the CSV file
     * @return list of loaded students, or empty list if file doesn't exist
     */
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
                String password = (tokens.size() > 5 && !tokens.get(5).isBlank()) 
                    ? tokens.get(5) 
                    : PasswordValidator.getDefaultPassword();
    
                students.add(new Student(studentId, name, password, year, major));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }
    


    /**
     * Loads company representatives from a CSV file.
     *
     * @param filePath path to the CSV file
     * @return list of loaded company representatives, or empty list if file doesn't exist
     */
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
                String status = tokens.size() > 6 ? tokens.get(6).toLowerCase() : "pending";
                boolean approved = status.contains("approved");
                String password = (tokens.size() > 7 && !tokens.get(7).isBlank()) 
                    ? tokens.get(7) 
                    : PasswordValidator.getDefaultPassword();

                reps.add(new CompanyRepresentative(repId, name, password, companyName, department, position, approved));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reps;
    }


    /**
     * Loads career center staff from a CSV file.
     *
     * @param filePath path to the CSV file
     * @return list of loaded staff members, or empty list if file doesn't exist
     */
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
                String role = tokens.get(2);
                String department = tokens.get(3);
                String password = (tokens.size() > 5 && !tokens.get(5).isBlank()) 
                    ? tokens.get(5) 
                    : PasswordValidator.getDefaultPassword();
    
                staff.add(new CareerCenterStaff(staffId, name, password, department));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }
    
    

    /**
     * Saves students to a CSV file.
     *
     * @param filePath path to the CSV file
     * @param students list of students to save
     * @throws RuntimeException if file writing fails
     */
    public void saveStudentsToFile(String filePath, List<Student> students) {
        List<String> lines = new ArrayList<>();
        lines.add("StudentID,Name,Major,YearOfStudy,Email,Password"); 
        for (Student student : students) {
            List<String> values = new ArrayList<>();
            values.add(student.getUserId());
            values.add(student.getName());
            values.add(student.getMajor());
            values.add(String.valueOf(student.getYearOfStudy()));
            values.add("");  
            values.add(student.getPassword());  
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }

    /**
     * Saves career center staff to a CSV file.
     *
     * @param filePath path to the CSV file
     * @param staff list of staff members to save
     * @throws RuntimeException if file writing fails
     */
    public void saveStaffToFile(String filePath, List<CareerCenterStaff> staff) {
        List<String> lines = new ArrayList<>();
        lines.add("StaffID,Name,Role,Department,Email,Password");
        for (CareerCenterStaff member : staff) {
            List<String> values = new ArrayList<>();
            values.add(member.getUserId());
            values.add(member.getName());
            values.add("Career Center Staff");
            values.add(member.getStaffDepartment());
            values.add("");  
            values.add(member.getPassword()); 
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }
    

    /**
     * Saves company representatives to a CSV file.
     *
     * @param filePath path to the CSV file
     * @param reps list of company representatives to save
     * @throws RuntimeException if file writing fails
     */
    public void saveCompanyRepsToFile(String filePath, List<CompanyRepresentative> reps) {
        List<String> lines = new ArrayList<>();
        lines.add("CompanyRepID,Name,CompanyName,Department,Position,Email,Status,Password");  
        for (CompanyRepresentative rep : reps) {
            List<String> values = new ArrayList<>();
            values.add(rep.getUserId());
            values.add(rep.getName());
            values.add(rep.getCompanyName());
            values.add(rep.getDepartment());
            values.add(rep.getPosition());
            values.add(rep.getUserId());  
            values.add(rep.isApproved() ? "Approved" : "Pending");
            values.add(rep.getPassword()); 
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }
    

    /**
     * Parses an integer from a string.
     *
     * @param value the string value
     * @param fallback the fallback value if parsing fails
     * @return the parsed integer, or fallback if parsing fails
     */
    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    /**
     * Writes lines to a file.
     *
     * @param filePath path to the file
     * @param lines list of lines to write
     * @throws RuntimeException if file writing fails
     */
    private void writeLines(String filePath, List<String> lines) {
        try {
            Files.write(Path.of(filePath), lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to " + filePath, e);
        }
    }
}
