package edu.ccrm.domain.enrollment;

/**
 * Enum for letter grades with corresponding grade points.
 * Demonstrates an enum with a constructor and fields.
 */
public enum Grade {
    S(10.0, "Excellent", 90.0, 100.0),
    A(9.0, "Very Good", 80.0, 89.9),
    B(8.0, "Good", 70.0, 79.9),
    C(7.0, "Satisfactory", 60.0, 69.9),
    D(6.0, "Pass", 50.0, 59.9),
    F(0.0, "Fail", 0.0, 49.9),
    NA(-1.0, "Not Awarded", -1.0, -1.0); // Not Awarded

    private final double gradePoint;
    private final String description;
    private final double minPercentage;
    private final double maxPercentage;

    Grade(double gradePoint, String description, double minPercentage, double maxPercentage) {
        this.gradePoint = gradePoint;
        this.description = description;
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public String getDescription() {
        return description;
    }

    public double getMinPercentage() {
        return minPercentage;
    }

    public double getMaxPercentage() {
        return maxPercentage;
    }

    /**
     * Converts numerical marks to letter grade
     */
    public static Grade fromMarks(double marks) {
        if (marks >= 90.0) return S;
        if (marks >= 80.0) return A;
        if (marks >= 70.0) return B;
        if (marks >= 60.0) return C;
        if (marks >= 50.0) return D;
        if (marks >= 0.0) return F;
        return NA;
    }

    /**
     * Check if grade is passing
     */
    public boolean isPassing() {
        return this != F && this != NA;
    }

    /**
     * Check if grade contributes to GPA
     */
    public boolean countsTowardGPA() {
        return this != NA;
    }

    @Override
    public String toString() {
        return String.format("%s (%.1f)", name(), gradePoint);
    }
}