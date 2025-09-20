package edu.ccrm.domain.person;

/**
 * Represents an Instructor, inheriting from Person.
 */
public class Instructor extends Person {
    private String department;

    public Instructor(int id, String fullName, String email, String department) {
        super(id, fullName, email);
        this.department = department;
    }

    @Override
    public String getProfile() {
        return String.format("Instructor Profile:\nID: %d\nName: %s\nEmail: %s\nDepartment: %s",
                id, fullName, email, department);
    }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return String.format("Instructor[ID=%d, Name='%s', Dept='%s']", id, fullName, department);
    }
}