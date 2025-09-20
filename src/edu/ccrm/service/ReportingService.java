package edu.ccrm.service;

import edu.ccrm.domain.Transcript;
import edu.ccrm.domain.enrollment.Grade;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportingService {
    private final DataStore dataStore;

    public ReportingService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void printTopStudentsByGpa(int limit) {
        System.out.println("\n--- Top " + limit + " Students by GPA ---");
        dataStore.getStudents().values().stream()
            .map(Transcript::new) // Create a transcript for each student
            .sorted(Comparator.comparingDouble(Transcript::calculateOverallGPA).reversed()) // Sort by GPA descending
            .limit(limit)
            .forEach(t -> System.out.println(t.generateTranscript()));
    }

    public void printGpaDistribution() {
        System.out.println("\n--- GPA Distribution Report ---");
        // Using a complex stream pipeline to aggregate data
        Map<String, Long> distribution = dataStore.getStudents().values().stream()
            .flatMap(s -> s.getEnrollments().stream())
            .filter(e -> e.getGrade() != Grade.NA)
            .collect(Collectors.groupingBy(
                e -> getGpaRange(e.getGrade()),
                Collectors.counting()
            ));

        System.out.println("Grade Point Range | Count");
        System.out.println("------------------|-------");
        distribution.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> System.out.printf("%-17s | %d\n", entry.getKey(), entry.getValue()));
    }
    
    private String getGpaRange(Grade grade) {
        double gp = grade.getGradePoint();
        if (gp >= 9.0) return "9.0 - 10.0 (S/A)";
        if (gp >= 8.0) return "8.0 - 8.9 (B)";
        if (gp >= 7.0) return "7.0 - 7.9 (C)";
        if (gp >= 6.0) return "6.0 - 6.9 (D)";
        return "Below 6.0 (F)";
    }
}