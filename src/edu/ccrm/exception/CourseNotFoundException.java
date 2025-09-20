package edu.ccrm.exception;

/**
 * Custom checked exception for when a course is not found.
 */
public class CourseNotFoundException extends Exception {
    public CourseNotFoundException(String message) {
        super(message);
    }
}