package edu.ccrm.domain.course;

/**
 * An immutable value class for CourseCode.
 */
public final class CourseCode {
    private final String code;

    public CourseCode(String code) {
        if (code == null || !code.matches("[A-Z]{2,4}\\d{3,4}")) {
            throw new IllegalArgumentException("Invalid course code format.");
        }
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CourseCode that = (CourseCode) obj;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}