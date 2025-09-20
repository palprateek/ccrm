package edu.ccrm.exception;

/**
 * Custom checked exception for when an enrollment is not found.
 */
public class EnrollmentNotFoundException extends Exception {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}