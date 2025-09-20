package edu.ccrm.exception;

/**
 * Custom checked exception for when a student is not found.
 */
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}