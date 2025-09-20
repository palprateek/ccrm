package edu.ccrm.cli;

/**
 * Utility class for displaying console menus.
 */
public class Menu {

    public static void displayMainMenu() {
        System.out.println("\n===== CCRM Main Menu =====");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollments & Grades");
        System.out.println("4. File Utilities");
        System.out.println("5. Run Reports");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    public static void displayStudentMenu() {
        System.out.println("\n--- Student Management ---");
        System.out.println("1. Add New Student");
        System.out.println("2. List All Students");
        System.out.println("3. List Students (Custom Sort)");
        System.out.println("4. Update Student Details");
        System.out.println("5. Print Student Profile & Transcript");
        System.out.println("6. Deactivate Student");
        System.out.println("7. Reactivate Student");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void displayCourseMenu() {
        System.out.println("\n--- Course Management ---");
        System.out.println("1. Add New Course");
        System.out.println("2. List All Courses");
        System.out.println("3. Update Course Details");
        System.out.println("4. Assign Instructor to Course");
        System.out.println("5. Deactivate Course");
        System.out.println("6. Reactivate Course");
        System.out.println("7. Search & Filter Courses");
        System.out.println("8. Advanced Course Search");
        System.out.println("9. Course Statistics");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }
    
    public static void displayEnrollmentMenu() {
        System.out.println("\n--- Enrollments & Grades ---");
        System.out.println("1. Enroll Student in a Course");
        System.out.println("2. Unenroll Student from Course");
        System.out.println("3. Record Student Grade");
        System.out.println("4. Record Student Marks");
        System.out.println("5. View Student Enrollments");
        System.out.println("6. Calculate Semester GPA");
        System.out.println("7. Generate Official Transcript");
        System.out.println("8. Generate Unofficial Transcript");
        System.out.println("9. Generate Semester Transcript");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }
    
    public static void displayFileMenu() {
        System.out.println("\n--- File Operations (NIO.2 + Streams) ---");
        System.out.println("1. Import Students from CSV");
        System.out.println("2. Import Courses from CSV");
        System.out.println("3. Export All Data to Files");
        System.out.println("4. Export Specific Data Type");
        System.out.println("5. Create Backup (Timestamped)");
        System.out.println("6. List Available Backups");
        System.out.println("7. Restore from Backup");
        System.out.println("8. Delete Backup");
        System.out.println("9. Recursive Directory Analysis");
        System.out.println("10. List Files by Depth");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }
    
    public static void displayReportsMenu() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. View Top N Students by GPA");
        System.out.println("2. View GPA Distribution");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void printPlatformSummary() {
        System.out.println("\n--- Java Platform Summary ---");
        System.out.println("Java SE (Standard Edition): For desktop and server applications.");
        System.out.println("Java ME (Micro Edition): For resource-constrained devices like mobile phones.");
        System.out.println("Java EE (Enterprise Edition): Extends SE with features for large-scale, enterprise-level applications.");
        System.out.println("--------------------------------\n");
    }
}