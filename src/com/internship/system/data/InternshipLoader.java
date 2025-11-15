package com.internship.system.data;

import com.internship.system.model.Application;
import com.internship.system.model.Internship;
import com.internship.system.model.enums.ApplicationStatus;
import com.internship.system.model.enums.InternshipLevel;
import com.internship.system.model.enums.InternshipStatus;
import com.internship.system.util.CsvUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InternshipLoader {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<Internship> loadInternshipsFromFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<Internship> internships = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) {
                List<String> tokens = CsvUtils.parseLine(lines.get(i));
                if (tokens.size() < 12) {
                    continue;
                }
                int internshipId = parseInt(tokens.get(0), 0);
                String title = tokens.get(1);
                String description = tokens.get(2);
                InternshipLevel level = parseLevel(tokens.get(3));
                String preferredMajor = tokens.get(4);
                LocalDate openingDate = parseDate(tokens.get(5));
                LocalDate closingDate = parseDate(tokens.get(6));
                InternshipStatus status = parseStatus(tokens.get(7));
                String companyName = tokens.get(8);
                String representativeId = tokens.get(9);
                int slots = parseInt(tokens.get(10), 0);
                boolean visible = Boolean.parseBoolean(tokens.get(11));
                internships.add(new Internship(
                        internshipId,
                        title,
                        description,
                        level,
                        preferredMajor,
                        openingDate,
                        closingDate,
                        status,
                        companyName,
                        representativeId,
                        slots,
                        visible,
                        0));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load internships from " + filePath, e);
        }
        return internships;
    }

    public List<Application> loadApplicationsFromFile(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<Application> applications = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) {
                List<String> tokens = CsvUtils.parseLine(lines.get(i));
                if (tokens.size() < 4) {
                    continue;
                }
                int applicationId = parseInt(tokens.get(0), 0);
                String studentId = tokens.get(1);
                int internshipId = parseInt(tokens.get(2), 0);
                ApplicationStatus status = parseApplicationStatus(tokens.get(3));
                boolean withdrawalRequested = tokens.size() > 4 && Boolean.parseBoolean(tokens.get(4));
                applications.add(new Application(applicationId, studentId, internshipId, status, withdrawalRequested));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load applications from " + filePath, e);
        }
        return applications;
    }

    public void saveInternshipsToFile(String filePath, List<Internship> internships) {
        List<String> lines = new ArrayList<>();
        lines.add(
                "internshipId,title,description,level,preferredMajor,openingDate,closingDate,status,companyName,representativeInChargeId,slots,isVisible");
        for (Internship internship : internships) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(internship.getInternshipId()));
            values.add(internship.getTitle());
            values.add(internship.getDescription());
            values.add(internship.getLevel().name());
            values.add(internship.getPreferredMajor());
            values.add(formatDate(internship.getOpeningDate()));
            values.add(formatDate(internship.getClosingDate()));
            values.add(internship.getStatus().name());
            values.add(internship.getCompanyName());
            values.add(internship.getRepresentativeInChargeId());
            values.add(String.valueOf(internship.getSlots()));
            values.add(String.valueOf(internship.isVisible()));
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }

    public void saveApplicationsToFile(String filePath, List<Application> applications) {
        List<String> lines = new ArrayList<>();
        lines.add("applicationId,studentId,internshipId,status,withdrawalRequested");
        for (Application application : applications) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(application.getApplicationId()));
            values.add(application.getStudentId());
            values.add(String.valueOf(application.getInternshipId()));
            values.add(application.getStatus().name());
            values.add(String.valueOf(application.isWithdrawalRequested()));
            lines.add(CsvUtils.toLine(values));
        }
        writeLines(filePath, lines);
    }

    private InternshipLevel parseLevel(String raw) {
        try {
            return InternshipLevel.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return InternshipLevel.BASIC;
        }
    }

    private InternshipStatus parseStatus(String raw) {
        try {
            return InternshipStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return InternshipStatus.PENDING;
        }
    }

    private ApplicationStatus parseApplicationStatus(String raw) {
        try {
            return ApplicationStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ApplicationStatus.PENDING;
        }
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalDate.parse(raw.trim(), DATE_FORMATTER);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
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
