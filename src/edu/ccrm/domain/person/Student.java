package edu.ccrm.domain.person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import edu.ccrm.domain.enrollment.Enrollment;
import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.enrollment.Grade;
import edu.ccrm.domain.course.Course;
import edu.ccrm.domain.Searchable;
import edu.ccrm.domain.Filterable;

/**
 * Represents a Student, inheriting from Person.
 * Implements Searchable and Filterable interfaces to demonstrate:
 * - Interface implementation
 * - Diamond problem resolution with default methods
 * - Generic interfaces
 */
public class Student extends Person implements Searchable<String>, Filterable<Student.Status> {
    private final String regNo;
    private Status status;
    private final LocalDate registrationDate;
    private final List<Enrollment> enrollments;
    private final Set<Course> enrolledCourses;
    private final Map<String, Grade> courseGrades; // Map of course code to grade
    private LocalDate birthDate;
    private LocalDateTime lastLoginDate;
    private LocalDate graduationDate;
    private LocalDateTime statusChangeDate;

    // Nested Enum for Student Status
    public enum Status { ACTIVE, INACTIVE, GRADUATED }

    public Student(int id, String regNo, String fullName, String email, LocalDate registrationDate) {
        super(id, fullName, email); // Calls superclass constructor
        
        // Assertions for invariants - enable with -ea JVM flag
        assert id > 0 : "Student ID must be positive, got: " + id;
        assert regNo != null && !regNo.trim().isEmpty() : "Registration number cannot be null or empty";
        assert regNo.matches("^R\\d{7}$") : "Registration number must follow pattern R followed by 7 digits, got: " + regNo;
        assert registrationDate != null : "Registration date cannot be null";
        assert !registrationDate.isAfter(LocalDate.now()) : "Registration date cannot be in the future, got: " + registrationDate;
        assert registrationDate.isAfter(LocalDate.of(2000, 1, 1)) : "Registration date seems too old, got: " + registrationDate;
        
        this.regNo = regNo;
        this.registrationDate = registrationDate;
        this.status = Status.ACTIVE;
        this.enrollments = new ArrayList<>();
        this.enrolledCourses = new HashSet<>();
        this.courseGrades = new HashMap<>();
        this.statusChangeDate = LocalDateTime.now();
    }

    // Constructor with additional date fields
    public Student(int id, String regNo, String fullName, String email, LocalDate registrationDate, LocalDate birthDate) {
        this(id, regNo, fullName, email, registrationDate);
        this.birthDate = birthDate;
    }

    @Override
    public String getProfile() {
        String birthInfo = (birthDate != null) ? "\nBirth Date: " + birthDate : "";
        String graduationInfo = (graduationDate != null) ? "\nGraduation Date: " + graduationDate : "";
        String lastLoginInfo = (lastLoginDate != null) ? "\nLast Login: " + lastLoginDate : "";
        String gpaInfo = (courseGrades.isEmpty()) ? "" : "\nGPA: " + String.format("%.2f", getGPA());
        String gradesInfo = (courseGrades.isEmpty()) ? "" : "\nCourse Grades: " + getCourseGradesAsString();
        
        return String.format("Student Profile:\nID: %d\nReg No: %s\nName: %s\nEmail: %s\nStatus: %s\nRegistered On: %s%s%s%s%s%s\nEnrolled Courses: %d",
                id, regNo, fullName, email, status, registrationDate, birthInfo, graduationInfo, lastLoginInfo, gpaInfo, gradesInfo, enrolledCourses.size());
    }

    public void addEnrollment(Enrollment enrollment) {
        // Assertions to check business rules
        assert enrollment != null : "Enrollment cannot be null";
        assert !enrolledCourses.contains(enrollment.getCourse()) : "Student is already enrolled in course: " + enrollment.getCourse().getCourseCode().getCode();
        assert enrollments.size() < 10 : "Student cannot enroll in more than 10 courses";
        
        this.enrollments.add(enrollment);
        this.enrolledCourses.add(enrollment.getCourse());
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        this.enrolledCourses.remove(enrollment.getCourse());
    }

    /**
     * Get current active enrollments (not dropped)
     */
    public List<Enrollment> getActiveEnrollments() {
        return enrollments.stream()
                .filter(e -> !e.isDropped())
                .collect(Collectors.toList());
    }

    /**
     * Get enrollments for a specific semester
     */
    public List<Enrollment> getEnrollments(Semester semester) {
        return enrollments.stream()
                .filter(e -> e.getSemester() == semester)
                .collect(Collectors.toList());
    }

    /**
     * Add a course grade for the student
     */
    public void addCourseGrade(String courseCode, Grade grade) {
        assert courseCode != null && !courseCode.trim().isEmpty() : "Course code cannot be null or empty";
        assert grade != null : "Grade cannot be null";
        
        this.courseGrades.put(courseCode, grade);
    }

    /**
     * Remove a course grade
     */
    public void removeCourseGrade(String courseCode) {
        this.courseGrades.remove(courseCode);
    }

    /**
     * Get grade for a specific course
     */
    public Grade getCourseGrade(String courseCode) {
        return this.courseGrades.get(courseCode);
    }

    /**
     * Get all course grades
     */
    public Map<String, Grade> getCourseGrades() {
        return new HashMap<>(courseGrades); // Defensive copy
    }

    /**
     * Calculate and return the current GPA based on course grades
     */
    public double calculateGPA() {
        if (courseGrades.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = 0.0;
        int totalCourses = 0;

        for (Grade grade : courseGrades.values()) {
            if (grade.countsTowardGPA()) {
                totalGradePoints += grade.getGradePoint();
                totalCourses++;
            }
        }

        return totalCourses > 0 ? Math.round((totalGradePoints / totalCourses) * 100.0) / 100.0 : 0.0;
    }

    /**
     * Set course grades from a formatted string (e.g., "CS101:A;MATH201:B;ENG101:S")
     */
    public void setCourseGradesFromString(String gradesString) {
        if (gradesString == null || gradesString.trim().isEmpty()) {
            return;
        }

        courseGrades.clear();
        String[] courseGradePairs = gradesString.split(";");
        
        for (String pair : courseGradePairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                String courseCode = parts[0].trim();
                String gradeStr = parts[1].trim();
                
                try {
                    Grade grade = Grade.valueOf(gradeStr);
                    courseGrades.put(courseCode, grade);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid grade '" + gradeStr + "' for course " + courseCode);
                }
            }
        }
    }

    /**
     * Get course grades as a formatted string (e.g., "CS101:A;MATH201:B;ENG101:S")
     */
    public String getCourseGradesAsString() {
        if (courseGrades.isEmpty()) {
            return "";
        }

        return courseGrades.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue().name())
                .collect(Collectors.joining(";"));
    }

    // Getters
    public String getRegNo() { return regNo; }
    public Status getStatus() { return status; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public List<Enrollment> getEnrollments() { return new ArrayList<>(enrollments); } // Defensive copy
    public Set<Course> getEnrolledCourses() { return new HashSet<>(enrolledCourses); } // Defensive copy
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public LocalDate getGraduationDate() { return graduationDate; }
    public LocalDateTime getStatusChangeDate() { return statusChangeDate; }
    public double getGPA() { return calculateGPA(); }
    
    // Setter for status
    public void setStatus(Status status) {
        this.status = status;
        this.statusChangeDate = LocalDateTime.now();
    }

    // Setters for mutable date fields
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void setGraduationDate(LocalDate graduationDate) {
        this.graduationDate = graduationDate;
        if (graduationDate != null) {
            this.setStatus(Status.GRADUATED);
        }
    }

    // Utility method to record login
    public void recordLogin() {
        this.lastLoginDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        String birthInfo = (birthDate != null) ? ", Birth: " + birthDate : "";
        String lastLoginInfo = (lastLoginDate != null) ? ", Last Login: " + lastLoginDate.toLocalDate() : "";
        String gpaInfo = (courseGrades.isEmpty()) ? "" : ", GPA: " + String.format("%.2f", getGPA());
        
        return String.format("Student[ID=%d, RegNo=%s, Name='%s', Email='%s', Status=%s, Enrolled Courses=%d%s%s%s]",
                id, regNo, fullName, email, status, enrolledCourses.size(), birthInfo, lastLoginInfo, gpaInfo);
    }

    // Implementation of Searchable interface
    @Override
    public boolean matches(String searchTerm) {
        if (!Searchable.isValidSearchTerm(searchTerm)) {
            return false;
        }
        
        String normalizedTerm = Searchable.normalizeSearchTerm(searchTerm);
        
        // Search in multiple fields using the default method from Searchable
        return containsIgnoreCase(fullName, normalizedTerm) ||
               containsIgnoreCase(email, normalizedTerm) ||
               containsIgnoreCase(regNo, normalizedTerm) ||
               containsIgnoreCase(status.toString(), normalizedTerm);
    }

    // Implementation of Filterable interface
    @Override
    public boolean matchesFilter(Status filter) {
        return this.status == filter;
    }

    /**
     * Resolve the diamond problem: Both Searchable and Filterable have getSearchableType() default methods.
     * We must provide an explicit implementation to resolve the conflict.
     * This method overrides both default implementations.
     */
    @Override
    public String getSearchableType() {
        return "Student"; // Explicit resolution of diamond problem
    }

    /**
     * Additional search method for advanced student searching.
     * Demonstrates using both interfaces in combination.
     */
    public boolean matchesAdvancedCriteria(String searchTerm, Status statusFilter) {
        // Use both Searchable and Filterable capabilities
        boolean matchesSearch = matches(searchTerm);
        boolean matchesFilter = (statusFilter == null) || matchesFilter(statusFilter);
        
        return matchesSearch && matchesFilter;
    }

    /**
     * Method to demonstrate the usage of static methods from interfaces.
     */
    public String getSearchHighlighted(String searchTerm) {
        if (!matches(searchTerm)) {
            return toString();
        }
        
        // Use the default method from Searchable to highlight matches
        return highlightMatches(toString(), searchTerm, "[MATCH]", "[/MATCH]");
    }
}