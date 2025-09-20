package edu.ccrm.domain;

import edu.ccrm.domain.enrollment.Enrollment;
import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.person.Student;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a student's academic transcript.
 * This class demonstrates polymorphism via the generateTranscript method and various transcript types.
 */
public class Transcript {
    protected final Student student;

    public Transcript(Student student) {
        this.student = student;
    }

    public double calculateOverallGPA() {
        List<Enrollment> gradedEnrollments = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getGrade().countsTowardGPA())
                .collect(Collectors.toList());

        if (gradedEnrollments.isEmpty()) {
            return 0.0;
        }

        double totalQualityPoints = gradedEnrollments.stream()
                .mapToDouble(Enrollment::getQualityPoints)
                .sum();

        int totalCredits = gradedEnrollments.stream()
                .mapToInt(Enrollment::getGPACredits)
                .sum();
        
        return (totalCredits > 0) ? totalQualityPoints / totalCredits : 0.0;
    }

    public double calculateSemesterGPA(Semester semester) {
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

    public int getTotalCreditsEarned() {
        return student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getGrade().isPassing())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
    }

    public int getTotalCreditsAttempted() {
        return student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .filter(e -> e.getGrade().countsTowardGPA())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
    }

    // Virtual method for polymorphism - can be overridden by subclasses
    public String generateTranscript() {
        return generateFullTranscript();
    }

    protected String generateFullTranscript() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("           OFFICIAL ACADEMIC TRANSCRIPT\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append(student.getProfile()).append("\n");
        sb.append("=".repeat(60)).append("\n");

        // Group enrollments by semester
        Map<Semester, List<Enrollment>> enrollmentsBySemester = student.getEnrollments().stream()
                .filter(e -> !e.isDropped())
                .collect(Collectors.groupingBy(Enrollment::getSemester));

        for (Semester semester : Semester.values()) {
            List<Enrollment> semesterEnrollments = enrollmentsBySemester.get(semester);
            if (semesterEnrollments != null && !semesterEnrollments.isEmpty()) {
                sb.append(generateSemesterSection(semester, semesterEnrollments));
            }
        }

        sb.append(generateSummarySection());
        sb.append("=".repeat(60)).append("\n");
        return sb.toString();
    }

    protected String generateSemesterSection(Semester semester, List<Enrollment> enrollments) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n--- %s SEMESTER ---\n", semester.name()));
        sb.append(String.format("%-12s | %-30s | %-7s | %-8s | %-5s\n", 
                "Course Code", "Course Title", "Credits", "Marks", "Grade"));
        sb.append("-".repeat(70)).append("\n");

        double semesterQualityPoints = 0.0;
        int semesterCredits = 0;

        for (Enrollment e : enrollments) {
            String marksStr = (e.getMarks() >= 0) ? String.format("%.1f", e.getMarks()) : "N/A";
            sb.append(String.format("%-12s | %-30s | %-7d | %-8s | %-5s\n",
                    e.getCourse().getCourseCode().getCode(),
                    truncate(e.getCourse().getTitle(), 30),
                    e.getCourse().getCredits(),
                    marksStr,
                    e.getGrade().name()));

            if (e.getGrade().countsTowardGPA()) {
                semesterQualityPoints += e.getQualityPoints();
                semesterCredits += e.getGPACredits();
            }
        }

        sb.append("-".repeat(70)).append("\n");
        double semesterGPA = (semesterCredits > 0) ? semesterQualityPoints / semesterCredits : 0.0;
        sb.append(String.format("Semester Credits: %d | Semester GPA: %.2f\n", 
                semesterCredits, semesterGPA));
        sb.append("\n");

        return sb.toString();
    }

    protected String generateSummarySection() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- ACADEMIC SUMMARY ---\n");
        sb.append(String.format("Total Credits Attempted: %d\n", getTotalCreditsAttempted()));
        sb.append(String.format("Total Credits Earned: %d\n", getTotalCreditsEarned()));
        sb.append(String.format("Cumulative GPA: %.2f\n", calculateOverallGPA()));
        
        // Academic standing
        double gpa = calculateOverallGPA();
        String standing = getAcademicStanding(gpa);
        sb.append(String.format("Academic Standing: %s\n", standing));
        
        return sb.toString();
    }

    protected String getAcademicStanding(double gpa) {
        if (gpa >= 9.0) return "Excellent";
        if (gpa >= 8.0) return "Very Good";
        if (gpa >= 7.0) return "Good";
        if (gpa >= 6.0) return "Satisfactory";
        if (gpa >= 5.0) return "Pass";
        return "Below Standard";
    }

    protected String truncate(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }

    @Override
    public String toString() {
        return String.format("Transcript[Student=%s, GPA=%.2f, Credits=%d]", 
                student.getFullName(), calculateOverallGPA(), getTotalCreditsEarned());
    }

    // Static factory methods for different transcript types (demonstrates polymorphism)
    public static Transcript createOfficialTranscript(Student student) {
        return new OfficialTranscript(student);
    }

    public static Transcript createUnnofficialTranscript(Student student) {
        return new UnofficialTranscript(student);
    }

    public static Transcript createSemesterTranscript(Student student, Semester semester) {
        return new SemesterTranscript(student, semester);
    }

    // Inner classes for different transcript types (demonstrates polymorphism)
    public static class OfficialTranscript extends Transcript {
        public OfficialTranscript(Student student) {
            super(student);
        }

        @Override
        public String generateTranscript() {
            StringBuilder sb = new StringBuilder();
            sb.append("*** OFFICIAL TRANSCRIPT ***\n");
            sb.append("This is an official academic record.\n");
            sb.append("Date of Issue: ").append(java.time.LocalDate.now()).append("\n\n");
            sb.append(generateFullTranscript());
            sb.append("\n*** END OF OFFICIAL TRANSCRIPT ***\n");
            return sb.toString();
        }
    }

    public static class UnofficialTranscript extends Transcript {
        public UnofficialTranscript(Student student) {
            super(student);
        }

        @Override
        public String generateTranscript() {
            StringBuilder sb = new StringBuilder();
            sb.append("*** UNOFFICIAL TRANSCRIPT ***\n");
            sb.append("This is an unofficial academic record for student use only.\n\n");
            sb.append(generateFullTranscript());
            sb.append("\n*** UNOFFICIAL - NOT FOR OFFICIAL USE ***\n");
            return sb.toString();
        }
    }

    public static class SemesterTranscript extends Transcript {
        private final Semester semester;

        public SemesterTranscript(Student student, Semester semester) {
            super(student);
            this.semester = semester;
        }

        @Override
        public String generateTranscript() {
            StringBuilder sb = new StringBuilder();
            sb.append("=".repeat(50)).append("\n");
            sb.append(String.format("   %s SEMESTER TRANSCRIPT\n", semester.name()));
            sb.append("=".repeat(50)).append("\n");
            sb.append(String.format("Student: %s (ID: %d)\n", student.getFullName(), student.getId()));
            sb.append(String.format("Registration Number: %s\n", student.getRegNo()));
            sb.append("=".repeat(50)).append("\n");

            List<Enrollment> semesterEnrollments = student.getEnrollments().stream()
                    .filter(e -> !e.isDropped())
                    .filter(e -> e.getSemester() == semester)
                    .collect(Collectors.toList());

            if (semesterEnrollments.isEmpty()) {
                sb.append("No enrollments found for this semester.\n");
            } else {
                sb.append(generateSemesterSection(semester, semesterEnrollments));
            }

            sb.append("=".repeat(50)).append("\n");
            return sb.toString();
        }
    }
}