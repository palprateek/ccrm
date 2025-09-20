package edu.ccrm.service;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.course.Course;
import edu.ccrm.domain.enrollment.Enrollment;
import edu.ccrm.domain.enrollment.Grade;
import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.person.Student;
import edu.ccrm.exception.CourseNotFoundException;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.EnrollmentNotFoundException;
import edu.ccrm.exception.InvalidUnenrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.exception.PrerequisiteNotMetException;
import edu.ccrm.exception.StudentNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final DataStore dataStore;
    private final int maxCredits;
    private final int minCredits;
    private final long enrollmentDeadlineHours; // Hours after which enrollment modifications are restricted

    public EnrollmentService(DataStore dataStore) {
        this.dataStore = dataStore;
        AppConfig config = AppConfig.getInstance();
        this.maxCredits = config.getIntProperty("max.credits.per.semester", 18);
        this.minCredits = config.getIntProperty("min.credits.per.semester", 12);
        this.enrollmentDeadlineHours = config.getLongProperty("enrollment.deadline.hours", 168L); // Default 1 week
    }

    public void enrollStudent(int studentId, String courseCode) 
        throws StudentNotFoundException, CourseNotFoundException, MaxCreditLimitExceededException, 
               DuplicateEnrollmentException, PrerequisiteNotMetException {
        enrollStudent(studentId, courseCode, null);
    }

    public void enrollStudent(int studentId, String courseCode, Semester semester) 
        throws StudentNotFoundException, CourseNotFoundException, MaxCreditLimitExceededException, 
               DuplicateEnrollmentException, PrerequisiteNotMetException {
        
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));
        Course course = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("Course not found."));

        // Validate student is active
        if (student.getStatus() != Student.Status.ACTIVE) {
            throw new IllegalStateException("Cannot enroll inactive or graduated student.");
        }

        // Validate course is active
        if (!course.isActive()) {
            throw new IllegalStateException("Cannot enroll in inactive course.");
        }

        // Use provided semester or course's default semester
        Semester enrollmentSemester = (semester != null) ? semester : course.getSemester();

        // Business Rule: Check for duplicate enrollment
        boolean alreadyEnrolled = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .anyMatch(e -> e.getCourse().getCourseCode().getCode().equals(courseCode) && 
                              e.getSemester() == enrollmentSemester);
        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException("Student is already enrolled in this course for this semester.");
        }

        // Business Rule: Check credit limit for the semester
        int currentCredits = getCurrentSemesterCredits(student, enrollmentSemester);
        if (currentCredits + course.getCredits() > maxCredits) {
            throw new MaxCreditLimitExceededException(
                String.format("Enrollment exceeds max credit limit. Current: %d, Adding: %d, Max: %d", 
                            currentCredits, course.getCredits(), maxCredits));
        }

        // Business Rule: Check prerequisites (simplified - can be enhanced)
        if (!checkPrerequisites(student, course)) {
            throw new PrerequisiteNotMetException("Prerequisites not met for course: " + courseCode);
        }

        // Create and add enrollment
        Enrollment enrollment = new Enrollment(course, enrollmentSemester);
        student.addEnrollment(enrollment);
    }

    public void unenrollStudent(int studentId, String courseCode) 
        throws StudentNotFoundException, EnrollmentNotFoundException, InvalidUnenrollmentException {
        
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));

        Enrollment enrollment = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getCourse().getCourseCode().getCode().equals(courseCode))
                .findFirst()
                .orElseThrow(() -> new EnrollmentNotFoundException("Student is not enrolled in this course."));

        // Business Rule: Check if it's past the drop deadline
        long hoursEnrolled = ChronoUnit.HOURS.between(enrollment.getEnrollmentDate(), LocalDateTime.now());
        if (hoursEnrolled > enrollmentDeadlineHours) {
            throw new InvalidUnenrollmentException("Cannot drop course after enrollment deadline.");
        }

        // Business Rule: Check minimum credit requirement
        int creditsAfterDrop = getCurrentSemesterCredits(student, enrollment.getSemester()) - enrollment.getCourse().getCredits();
        if (creditsAfterDrop < minCredits) {
            throw new InvalidUnenrollmentException(
                String.format("Cannot drop course. Would result in %d credits, below minimum of %d", 
                            creditsAfterDrop, minCredits));
        }

        // Business Rule: Cannot drop if grade has been assigned (except NA)
        if (enrollment.getGrade() != Grade.NA) {
            throw new InvalidUnenrollmentException("Cannot drop course after grade has been assigned.");
        }

        enrollment.drop();
    }

    public void assignGrade(int studentId, String courseCode, Grade grade) 
        throws StudentNotFoundException, EnrollmentNotFoundException {
        
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));
        
        Enrollment enrollment = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getCourse().getCourseCode().getCode().equals(courseCode))
                .findFirst()
                .orElseThrow(() -> new EnrollmentNotFoundException("Student is not enrolled in this course."));

        enrollment.setGrade(grade);
    }

    public void assignMarks(int studentId, String courseCode, double marks) 
        throws StudentNotFoundException, EnrollmentNotFoundException {
        
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));
        
        Enrollment enrollment = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getCourse().getCourseCode().getCode().equals(courseCode))
                .findFirst()
                .orElseThrow(() -> new EnrollmentNotFoundException("Student is not enrolled in this course."));

        enrollment.setMarks(marks); // This automatically sets the grade based on marks
    }

    // Helper methods
    private int getCurrentSemesterCredits(Student student, Semester semester) {
        return student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getSemester() == semester)
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
    }

    private boolean checkPrerequisites(Student student, Course course) {
        // Simplified prerequisite check - can be enhanced with actual prerequisite system
        // For now, just check if student has attempted any course in the same department for advanced courses
        String courseCode = course.getCourseCode().getCode();
        
        // Check if it's an advanced course (assuming course codes ending with numbers > 200 are advanced)
        try {
            String numPart = courseCode.replaceAll("[^0-9]", "");
            if (!numPart.isEmpty()) {
                int courseLevel = Integer.parseInt(numPart);
                if (courseLevel >= 300) {
                    // For 300+ level courses, student should have completed at least one course in same department
                    String dept = course.getDepartment();
                    return student.getEnrollments().stream()
                            .filter(e -> !e.isDropped())
                            .filter(e -> e.getGrade().isPassing())
                            .anyMatch(e -> e.getCourse().getDepartment().equals(dept));
                }
            }
        } catch (NumberFormatException e) {
            // If we can't parse course level, assume prerequisites are met
        }
        
        return true; // Prerequisites met for basic courses
    }

    // Query methods
    public List<Enrollment> getStudentEnrollments(int studentId, Semester semester) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));
        
        return student.getEnrollments().stream()
                .filter(e -> e.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getActiveEnrollments(int studentId) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));
        
        return student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .collect(Collectors.toList());
    }

    public double calculateSemesterGPA(int studentId, Semester semester) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found."));
        
        List<Enrollment> semesterEnrollments = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getSemester() == semester)
                .filter(e -> e.getGrade().countsTowardGPA())
                .collect(Collectors.toList());

        if (semesterEnrollments.isEmpty()) {
            return 0.0;
        }

        double totalQualityPoints = semesterEnrollments.stream()
                .mapToDouble(Enrollment::getQualityPoints)
                .sum();

        int totalCredits = semesterEnrollments.stream()
                .mapToInt(Enrollment::getGPACredits)
                .sum();

        return (totalCredits > 0) ? totalQualityPoints / totalCredits : 0.0;
    }
}