package edu.ccrm.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs.
 */
public class IdGenerator {
    private static final AtomicInteger studentIdCounter = new AtomicInteger(0);
    private static final AtomicInteger instructorIdCounter = new AtomicInteger(0);

    public static int getNextStudentId() {
        return studentIdCounter.incrementAndGet();
    }

    public static int getNextInstructorId() {
        return instructorIdCounter.incrementAndGet();
    }
}