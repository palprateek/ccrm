package edu.ccrm.exception;

/**
 * Custom checked exception for exceeding credit limits.
 */
public class MaxCreditLimitExceededException extends Exception {
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
}