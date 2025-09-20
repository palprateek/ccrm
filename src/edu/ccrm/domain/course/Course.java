package edu.ccrm.domain.course;

import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.person.Instructor;

/**
 * Represents a Course. This class uses the Builder pattern.
 */
public class Course {
    private final CourseCode courseCode; // Immutable value object
    private final String title;
    private final int credits;
    private final String department;
    private final Semester semester;
    private Instructor instructor; // Can be assigned later
    private boolean active; // For course deactivation

    private Course(Builder builder) {
        this.courseCode = new CourseCode(builder.code);
        this.title = builder.title;
        this.credits = builder.credits;
        this.department = builder.department;
        this.semester = builder.semester;
        this.instructor = builder.instructor;
        this.active = builder.active;
    }
    
    // Getters
    public CourseCode getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public Semester getSemester() { return semester; }
    public Instructor getInstructor() { return instructor; }
    public boolean isActive() { return active; }
    
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        String instructorName = (instructor != null) ? instructor.getFullName() : "Not Assigned";
        String status = active ? "Active" : "Inactive";
        return String.format("Course[%s: %s, Credits: %d, Dept: %s, Semester: %s, Instructor: %s, Status: %s]",
                courseCode.getCode(), title, credits, department, semester, instructorName, status);
    }

    // Static nested Builder class
    public static class Builder {
        private final String code;
        private final String title;
        private int credits = 3; // Default value
        private String department = "General";
        private Semester semester = Semester.FALL;
        private Instructor instructor = null;
        private boolean active = true; // Default to active

        public Builder(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public Builder credits(int credits) {
            this.credits = credits;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }
        
        public Builder instructor(Instructor instructor) {
            this.instructor = instructor;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Course build() {
            // Assertions for invariants - enable with -ea JVM flag
            // These assertions check for programming errors and help catch bugs during development
            
            // Course code validation
            assert code != null : "Course code cannot be null";
            assert !code.trim().isEmpty() : "Course code cannot be empty or whitespace";
            assert code.length() >= 5 && code.length() <= 10 : "Course code must be between 5-10 characters, got: " + code.length();
            assert code.matches("^[A-Z]{2,4}\\d{3,4}$") : "Course code must follow pattern (2-4 letters + 3-4 digits), got: " + code;
            
            // Title validation
            assert title != null : "Course title cannot be null";
            assert !title.trim().isEmpty() : "Course title cannot be empty or whitespace";
            assert title.length() >= 3 && title.length() <= 100 : "Course title must be between 3-100 characters, got: " + title.length();
            
            // Credits validation
            assert credits > 0 : "Credits must be positive, got: " + credits;
            assert credits <= 9 : "Credits cannot exceed 9, got: " + credits;
            assert credits <= 6 || department.equals("Advanced Studies") : "Courses with more than 6 credits require Advanced Studies department, got: " + credits + " credits in " + department;
            
            // Department validation
            assert department != null : "Department cannot be null";
            assert !department.trim().isEmpty() : "Department cannot be empty or whitespace";
            assert department.length() >= 2 && department.length() <= 50 : "Department name must be between 2-50 characters, got: " + department.length();
            
            // Semester validation
            assert semester != null : "Semester cannot be null";
            
            // Business logic assertions
            assert !code.startsWith("TEST") || department.equals("Testing") : "TEST courses must be in Testing department";
            assert !code.contains("LAB") || credits <= 2 : "Lab courses should not exceed 2 credits, got: " + credits;
            
            // Optional instructor validation (if provided)
            if (instructor != null) {
                assert instructor.getFullName() != null && !instructor.getFullName().trim().isEmpty() : "Instructor must have a valid name";
                assert instructor.getDepartment() != null : "Instructor must have a department assigned";
            }
            
            return new Course(this);
        }
    }
}