package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.course.Course;
import edu.ccrm.domain.enrollment.Enrollment;
import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.enrollment.Grade;
import edu.ccrm.domain.person.Student;
import edu.ccrm.domain.person.Instructor;
import edu.ccrm.service.DataStore;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for importing and exporting data using NIO.2 and Streams.
 */
public class ImportExportService {
    private final DataStore dataStore;
    private final Path dataDir;

    public ImportExportService(DataStore dataStore) {
        this.dataStore = dataStore;
        this.dataDir = Paths.get(AppConfig.getInstance().getDataDirectory());
    }

    public void importStudents(String filename) {
        Path path = dataDir.resolve(filename);
        
        // Check if file exists before attempting to read
        if (Files.notExists(path)) {
            System.err.println("File not found: " + path);
            return;
        }
        
        try {
            // Ensure data directory exists
            if (Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            try (Stream<String> lines = Files.lines(path)) {
                long count = lines.skip(1) // Skip header row
                    .filter(line -> !line.trim().isEmpty()) // Filter empty lines
                    .map(line -> parseCsvLine(line)) // Use proper CSV parsing
                    .mapToLong(parts -> {
                        try {
                            if (parts.length < 4) {
                                System.err.println("Invalid student record (insufficient columns): " + String.join(",", parts));
                                return 0;
                            }
                            
                            // Basic fields with validation
                            String fullName = parts[1].trim();
                            String email = parts[2].trim();
                            LocalDate registrationDate = LocalDate.parse(parts[3].trim());
                            
                            if (fullName.isEmpty() || email.isEmpty()) {
                                System.err.println("Invalid student record (missing name or email): " + String.join(",", parts));
                                return 0;
                            }
                            
                            // Create student with birth date if available (6th column)
                            Student student;
                            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                                LocalDate birthDate = LocalDate.parse(parts[6].trim());
                                student = dataStore.addStudent(fullName, email, registrationDate, birthDate);
                            } else {
                                student = dataStore.addStudent(fullName, email, registrationDate);
                            }
                            
                            // Set status if provided (5th column)
                            if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                                try {
                                    Student.Status status = Student.Status.valueOf(parts[4].trim().toUpperCase());
                                    student.setStatus(status);
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Invalid status '" + parts[4].trim() + "' for student " + parts[1].trim() + ". Using default ACTIVE status.");
                                }
                            }
                            
                            // Set graduation date if provided (7th column)
                            if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                                LocalDate graduationDate = LocalDate.parse(parts[7].trim());
                                student.setGraduationDate(graduationDate);
                            }
                            
                            // Set last login date if provided (8th column)
                            if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                                LocalDateTime lastLoginDate = LocalDateTime.parse(parts[8].trim());
                                student.setLastLoginDate(lastLoginDate);
                            }
                            
                            // Set enrolled courses with grades if provided (9th column)
                            if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                                student.setCourseGradesFromString(parts[9].trim());
                                // Create enrollment objects for courses with grades
                                createEnrollmentsFromGrades(student);
                            }
                            
                            return 1; // Successfully processed
                        } catch (Exception e) {
                            System.err.println("Error processing student record: " + String.join(",", parts) + " - " + e.getMessage());
                            return 0;
                        }
                    })
                    .sum();
                    
                System.out.println("Students imported successfully from " + filename + " (" + count + " records processed)");
            }
        } catch (IOException e) {
            System.err.println("Error importing students from " + filename + ": " + e.getMessage());
        }
    }

    public void importCourses(String filename) {
        Path path = dataDir.resolve(filename);
        
        // Check if file exists before attempting to read
        if (Files.notExists(path)) {
            System.err.println("File not found: " + path);
            return;
        }
        
        try {
            // Ensure data directory exists
            if (Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            try (Stream<String> lines = Files.lines(path)) {
                long count = lines.skip(1)
                    .filter(line -> !line.trim().isEmpty()) // Filter empty lines
                    .map(line -> line.split(","))
                    .mapToLong(parts -> {
                        try {
                            if (parts.length < 2) {
                                System.err.println("Invalid course record (insufficient columns): " + String.join(",", parts));
                                return 0;
                            }
                            
                            // Basic course information with validation
                            String code = parts[0].trim();
                            String title = parts[1].trim();
                            
                            if (code.isEmpty() || title.isEmpty()) {
                                System.err.println("Invalid course record (missing code or title): " + String.join(",", parts));
                                return 0;
                            }
                            
                            int credits = parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 3;
                            String department = parts.length > 3 ? parts[3].trim() : "General";
                            
                            Course.Builder builder = new Course.Builder(code, title)
                                    .credits(credits)
                                    .department(department);
                            
                            // Set semester if provided (5th column)
                            if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                                try {
                                    Semester semester = Semester.valueOf(parts[4].trim().toUpperCase());
                                    builder.semester(semester);
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Invalid semester '" + parts[4].trim() + "' for course " + code + ". Using default FALL.");
                                }
                            }
                            
                            // Set instructor if provided (6th and 7th columns)
                            if (parts.length > 6 && !parts[5].trim().isEmpty() && !parts[6].trim().isEmpty()) {
                                Instructor instructor = new Instructor(0, parts[5].trim(), parts[6].trim(), department);
                                builder.instructor(instructor);
                            }
                            
                            // Set active status if provided (8th column)
                            if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                                boolean active = Boolean.parseBoolean(parts[7].trim());
                                builder.active(active);
                            }
                            
                            Course course = builder.build();
                            dataStore.addCourse(course);
                            return 1; // Successfully processed
                        } catch (Exception e) {
                            System.err.println("Error processing course record: " + String.join(",", parts) + " - " + e.getMessage());
                            return 0;
                        }
                    })
                    .sum();
                    
                System.out.println("Courses imported successfully from " + filename + " (" + count + " records processed)");
            }
        } catch (IOException e) {
            System.err.println("Error importing courses from " + filename + ": " + e.getMessage());
        }
    }

    public void exportAllData() {
        try {
            // Create data directory if it doesn't exist
            if (Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // Export all data types
            exportStudents("exported_students.csv");
            exportCourses("exported_courses.csv");
            exportEnrollments("exported_enrollments.csv");
            
            // Verify exports
            long studentCount = Files.lines(dataDir.resolve("exported_students.csv")).count() - 1; // minus header
            long courseCount = Files.lines(dataDir.resolve("exported_courses.csv")).count() - 1;
            long enrollmentCount = Files.lines(dataDir.resolve("exported_enrollments.csv")).count() - 1;
            
            System.out.println("All data exported successfully:");
            System.out.println("  - Students: " + studentCount + " records");
            System.out.println("  - Courses: " + courseCount + " records");
            System.out.println("  - Enrollments: " + enrollmentCount + " records");
            System.out.println("  - Location: " + dataDir.toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error during data export: " + e.getMessage());
        }
    }
    
    public void exportData(String dataType, String filename) {
        try {
            if (Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            switch (dataType.toLowerCase()) {
                case "students":
                    exportStudents(filename);
                    break;
                case "courses":
                    exportCourses(filename);
                    break;
                case "enrollments":
                    exportEnrollments(filename);
                    break;
                default:
                    System.err.println("Unknown data type: " + dataType + ". Valid types: students, courses, enrollments");
                    return;
            }
            
            long recordCount = Files.lines(dataDir.resolve(filename)).count() - 1; // minus header
            System.out.println("Exported " + recordCount + " " + dataType + " records to " + filename);
            
        } catch (IOException e) {
            System.err.println("Error exporting " + dataType + ": " + e.getMessage());
        }
    }

    private void exportStudents(String filename) throws IOException {
        List<String> lines = dataStore.getStudents().values().stream()
                .map(s -> String.join(",",
                        String.valueOf(s.getId()),
                        s.getFullName(),
                        s.getEmail(),
                        s.getRegistrationDate().toString(),
                        s.getStatus().name(),
                        s.getRegNo(),
                        s.getBirthDate() != null ? s.getBirthDate().toString() : "",
                        s.getGraduationDate() != null ? s.getGraduationDate().toString() : "",
                        s.getLastLoginDate() != null ? s.getLastLoginDate().toString() : "",
                        s.getCourseGradesAsString(),
                        String.format("%.2f", s.getGPA())))
                .collect(Collectors.toList());
        lines.add(0, "ID,FullName,Email,RegistrationDate,Status,RegNo,BirthDate,GraduationDate,LastLoginDate,EnrolledCourses,GPA");
        Files.write(dataDir.resolve(filename), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    private void exportCourses(String filename) throws IOException {
        List<String> lines = dataStore.getCourses().values().stream()
                .map(c -> String.join(",",
                        c.getCourseCode().getCode(),
                        c.getTitle(),
                        String.valueOf(c.getCredits()),
                        c.getDepartment(),
                        c.getSemester().name(),
                        c.getInstructor() != null ? c.getInstructor().getFullName() : "",
                        c.getInstructor() != null ? c.getInstructor().getEmail() : "",
                        String.valueOf(c.isActive())))
                .collect(Collectors.toList());
        lines.add(0, "Code,Title,Credits,Department,Semester,InstructorName,InstructorEmail,Active");
        Files.write(dataDir.resolve(filename), lines);
    }

    private void exportEnrollments(String filename) throws IOException {
        List<String> lines = dataStore.getStudents().values().stream()
                .flatMap(student -> student.getEnrollments().stream()
                        .map(enrollment -> String.join(",",
                                String.valueOf(student.getId()),
                                student.getRegNo(),
                                enrollment.getCourse().getCourseCode().getCode(),
                                enrollment.getCourse().getTitle(),
                                enrollment.getSemester().name(),
                                enrollment.getEnrollmentDate().toString(),
                                enrollment.getMarks() >= 0 ? String.valueOf(enrollment.getMarks()) : "",
                                enrollment.getGrade().name(),
                                String.valueOf(enrollment.getGrade().getGradePoint()),
                                enrollment.isDropped() ? "DROPPED" : "ENROLLED")))
                .collect(Collectors.toList());
        lines.add(0, "StudentID,StudentRegNo,CourseCode,CourseTitle,Semester,EnrollmentDate,Marks,Grade,GradePoints,Status");
        Files.write(dataDir.resolve(filename), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Parse a CSV line handling quoted fields properly
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(ch);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString());
        
        return fields.toArray(new String[0]);
    }

    /**
     * Create enrollment objects for a student based on their course grades
     */
    private void createEnrollmentsFromGrades(Student student) {
        Map<String, Grade> courseGrades = student.getCourseGrades();
        
        for (Map.Entry<String, Grade> entry : courseGrades.entrySet()) {
            String courseCode = entry.getKey();
            Grade grade = entry.getValue();
            
            // Find the course in the data store
            Course course = dataStore.getCourses().values().stream()
                .filter(c -> c.getCourseCode().getCode().equals(courseCode))
                .findFirst()
                .orElse(null);
                
            if (course != null) {
                // Create an enrollment for this course
                Enrollment enrollment = new Enrollment(course, course.getSemester());
                enrollment.setGrade(grade);
                
                // Calculate marks from grade (approximate)
                double marks = calculateMarksFromGrade(grade);
                enrollment.setMarks(marks);
                
                // Add enrollment to student
                student.addEnrollment(enrollment);
            } else {
                System.err.println("Course not found: " + courseCode + " for student " + student.getFullName());
            }
        }
    }
    
    /**
     * Calculate approximate marks from grade
     */
    private double calculateMarksFromGrade(Grade grade) {
        switch (grade) {
            case S: return 95.0;
            case A: return 85.0;
            case B: return 75.0;
            case C: return 65.0;
            case D: return 55.0;
            case F: return 35.0;
            default: return -1.0;
        }
    }
}