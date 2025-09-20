package edu.ccrm.domain.enrollment;

import edu.ccrm.domain.course.Course;
import java.time.LocalDateTime;

/**
 * Represents the enrollment of a student in a course.
 */
public class Enrollment {
    private final Course course;
    private Grade grade;
    private double marks; // Numerical marks out of 100
    private final LocalDateTime enrollmentDate;
    private final Semester semester;
    private boolean dropped; // For unenrollment tracking

    public Enrollment(Course course) {
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
        this.grade = Grade.NA; // Default grade
        this.marks = -1.0; // -1 indicates no marks assigned
        this.semester = course.getSemester();
        this.dropped = false;
    }

    public Enrollment(Course course, Semester semester) {
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
        this.grade = Grade.NA;
        this.marks = -1.0;
        this.semester = semester;
        this.dropped = false;
    }

    // Getters
    public Course getCourse() { return course; }
    public Grade getGrade() { return grade; }
    public double getMarks() { return marks; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public Semester getSemester() { return semester; }
    public boolean isDropped() { return dropped; }

    // Setters
    public void setGrade(Grade grade) { 
        this.grade = grade; 
    }

    public void setMarks(double marks) {
        if (marks >= 0 && marks <= 100) {
            this.marks = marks;
            this.grade = Grade.fromMarks(marks);
        } else if (marks == -1) {
            this.marks = marks;
            this.grade = Grade.NA;
        } else {
            throw new IllegalArgumentException("Marks must be between 0 and 100, or -1 for not assigned");
        }
    }

    public void drop() {
        this.dropped = true;
    }

    public void undrop() {
        this.dropped = false;
    }

    /**
     * Calculate quality points for this enrollment (grade points * credits)
     */
    public double getQualityPoints() {
        if (dropped || !grade.countsTowardGPA()) {
            return 0.0;
        }
        return grade.getGradePoint() * course.getCredits();
    }

    /**
     * Get credits that count toward GPA
     */
    public int getGPACredits() {
        if (dropped || !grade.countsTowardGPA()) {
            return 0;
        }
        return course.getCredits();
    }

    @Override
    public String toString() {
        String status = dropped ? " (DROPPED)" : "";
        String marksInfo = (marks >= 0) ? String.format(" [%.1f%%]", marks) : "";
        return String.format("Enrollment[Course=%s, Grade=%s%s, Semester=%s%s]", 
                course.getCourseCode().getCode(), grade, marksInfo, semester, status);
    }
}