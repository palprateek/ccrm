package edu.ccrm.exception;

/**
 * Custom checked exception for when course prerequisites are not met.
 */
public class PrerequisiteNotMetException extends Exception {
    public PrerequisiteNotMetException(String message) {
        super(message);
    }
}