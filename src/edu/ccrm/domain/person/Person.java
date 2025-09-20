package edu.ccrm.domain.person;

/**
 * Abstract base class representing a person.
 * Demonstrates Abstraction and Inheritance.
 */
public abstract class Person {
    protected int id; // 'protected' allows access by subclasses
    protected String fullName;
    protected String email;

    public Person(int id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    // Abstract method to be implemented by subclasses
    public abstract String getProfile();

    // Getters and Setters (Encapsulation)
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}