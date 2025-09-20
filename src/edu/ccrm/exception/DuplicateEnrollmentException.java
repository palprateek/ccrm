package edu.ccrm.exception;

/**
 * Custom unchecked exception for duplicate enrollment attempts.
 */
public class DuplicateEnrollmentException extends RuntimeException {
    public DuplicateEnrollmentException(String message) {
        super(message);
    }
}