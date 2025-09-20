package edu.ccrm.exception;

/**
 * Custom checked exception for when attempting to unenroll from a course inappropriately.
 */
public class InvalidUnenrollmentException extends Exception {
    public InvalidUnenrollmentException(String message) {
        super(message);
    }
}