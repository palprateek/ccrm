package edu.ccrm.cli;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import edu.ccrm.domain.course.Course;
import edu.ccrm.domain.enrollment.Grade;
import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.enrollment.Enrollment;
import edu.ccrm.domain.person.Student;
import edu.ccrm.domain.person.Instructor;
import edu.ccrm.domain.Transcript;
import edu.ccrm.exception.CourseNotFoundException;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.EnrollmentNotFoundException;
import edu.ccrm.exception.InvalidUnenrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.exception.PrerequisiteNotMetException;
import edu.ccrm.exception.StudentNotFoundException;
import edu.ccrm.io.BackupService;
import edu.ccrm.io.ImportExportService;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.DataStore;
import edu.ccrm.service.EnrollmentService;
import edu.ccrm.service.ReportingService;
import edu.ccrm.service.StudentService;
import edu.ccrm.util.Validator;
import edu.ccrm.util.RecursiveFileUtils; 

/**
 * Main Command-Line Interface (CLI) controller for the CCRM application.
 * Demonstrates various loop and decision constructs.
 */
public class CCRM_CLI {
    private final Scanner scanner;
    private final DataStore dataStore;
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ImportExportService importExportService;
    private final BackupService backupService;
    private final ReportingService reportingService;

    public CCRM_CLI() {
        this.scanner = new Scanner(System.in);
        this.dataStore = new DataStore();
        this.studentService = new StudentService(dataStore);
        this.courseService = new CourseService(dataStore);
        this.enrollmentService = new EnrollmentService(dataStore);
        this.importExportService = new ImportExportService(dataStore);
        this.backupService = new BackupService();
        this.reportingService = new ReportingService(dataStore);
        
        // Load initial data from CSV files if they exist
        loadInitialData();
    }
    
    /**
     * Loads initial data from CSV files in the data directory.
     * This method is called during application startup to populate the system
     * with existing data from students.csv and courses.csv files.
     */
    private void loadInitialData() {
        System.out.println("Loading initial data...");
        
        try {
            // Import courses from CSV FIRST
            importExportService.importCourses("courses.csv");
            
            // Import students from CSV AFTER courses are loaded
            importExportService.importStudents("students.csv");
            
            // Display summary of loaded data
            int studentCount = studentService.getAllStudents().size();
            int courseCount = courseService.getAllCourses().size();
            
            if (studentCount > 0 || courseCount > 0) {
                System.out.println("Initial data loaded successfully:");
                System.out.println("- Students: " + studentCount);
                System.out.println("- Courses: " + courseCount);
            } else {
                System.out.println("No initial data found. Starting with empty system.");
            }
            
        } catch (Exception e) {
            System.err.println("Warning: Could not load initial data: " + e.getMessage());
            System.out.println("Starting with empty system. You can add data manually or import CSV files later.");
        }
        
        System.out.println(); // Add a blank line for better readability
    }

    public void run() {
        boolean exit = false;
        // The main 'while' loop for the application menu
        while (!exit) {
            Menu.displayMainMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Enhanced 'switch' statement for menu navigation
                switch (choice) {
                    case 1 -> manageStudents();
                    case 2 -> manageCourses();
                    case 3 -> manageEnrollmentsAndGrades();
                    case 4 -> manageFileOperations();
                    case 5 -> runReports();
                    case 0 -> {
                        exit = true; // Set flag to exit the while loop
                        Menu.printPlatformSummary();
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
        scanner.close();
    }

    private void manageStudents() {
        boolean back = false;
        // 'do-while' loop for the student management submenu
        do {
            Menu.displayStudentMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    listAllStudents();
                    break;
                case 3:
                    listStudentsWithCustomSort();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    printStudentProfileAndTranscript();
                    break;
                case 6:
                    deactivateStudent();
                    break;
                case 7:
                    reactivateStudent();
                    break;
                case 0:
                    back = true;
                    break; // break from switch
                default:
                    System.out.println("Invalid choice.");
            }
        } while (!back);
    }
    
    private void manageCourses() {
        boolean back = false;
        // Demonstrates a labeled jump statement.
        COURSE_MENU_LOOP:
        while (!back) {
            Menu.displayCourseMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: addCourse(); break;
                case 2: listAllCourses(); break;
                case 3: updateCourse(); break;
                case 4: assignInstructorToCourse(); break;
                case 5: deactivateCourse(); break;
                case 6: reactivateCourse(); break;
                case 7: searchAndFilterCourses(); break;
                case 8: advancedCourseSearch(); break;
                case 9: showCourseStatistics(); break;
                case 0: back = true;
                        break COURSE_MENU_LOOP; // Labeled break
                default:
                    System.out.println("Invalid choice. Continuing to menu...");
                    continue; // continue the COURSE_MENU_LOOP
            }
        }
    }

    private void manageEnrollmentsAndGrades() {
        boolean back = false;
        do {
            Menu.displayEnrollmentMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    enrollStudentInCourse();
                    break;
                case 2:
                    unenrollStudentFromCourse();
                    break;
                case 3:
                    recordGrade();
                    break;
                case 4:
                    recordMarks();
                    break;
                case 5:
                    viewStudentEnrollments();
                    break;
                case 6:
                    calculateSemesterGPA();
                    break;
                case 7:
                    generateOfficialTranscript();
                    break;
                case 8:
                    generateUnofficialTranscript();
                    break;
                case 9:
                    generateSemesterTranscript();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (!back);
    }
    
    private void manageFileOperations() {
        Menu.displayFileMenu();
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch(choice) {
            case 1 -> importStudentsFromFile();
            case 2 -> importCoursesFromFile();
            case 3 -> importExportService.exportAllData();
            case 4 -> exportSpecificDataType();
            case 5 -> createBackup();
            case 6 -> backupService.listBackups();
            case 7 -> restoreFromBackup();
            case 8 -> deleteBackup();
            case 9 -> performDirectoryAnalysis();
            case 10 -> listFilesByDepth();
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }
    
    private void importStudentsFromFile() {
        System.out.print("Enter CSV filename for students (or press Enter for default 'students.csv'): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "students.csv";
        }
        importExportService.importStudents(filename);
    }
    
    private void importCoursesFromFile() {
        System.out.print("Enter CSV filename for courses (or press Enter for default 'courses.csv'): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "courses.csv";
        }
        importExportService.importCourses(filename);
    }
    
    private void exportSpecificDataType() {
        System.out.println("Select data type to export:");
        System.out.println("1. Students only");
        System.out.println("2. Courses only");
        System.out.println("3. Enrollments only");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine().trim();
        
        switch (choice) {
            case 1 -> importExportService.exportData("students", filename);
            case 2 -> importExportService.exportData("courses", filename);
            case 3 -> importExportService.exportData("enrollments", filename);
            default -> System.out.println("Invalid choice.");
        }
    }
    
    private void createBackup() {
        System.out.println("Creating timestamped backup...");
        String backupPath = backupService.backupData();
        if (backupPath != null) {
            // Additional analysis
            long size = RecursiveFileUtils.calculateDirectorySize(backupPath);
            System.out.println("Backup analysis complete.");
            RecursiveFileUtils.printDirectoryStats(backupPath);
        }
    }
    
    private void restoreFromBackup() {
        System.out.println("Available backups:");
        backupService.listBackups();
        System.out.print("Enter backup timestamp (yyyyMMdd_HHmmss): ");
        String timestamp = scanner.nextLine().trim();
        
        if (timestamp.isEmpty()) {
            System.out.println("No timestamp provided.");
            return;
        }
        
        System.out.print("Are you sure you want to restore from backup " + timestamp + "? This will overwrite current data. (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if ("y".equals(confirm) || "yes".equals(confirm)) {
            boolean success = backupService.restoreBackup(timestamp);
            if (success) {
                System.out.println("Data restored successfully. You may need to refresh the application.");
            }
        } else {
            System.out.println("Restore cancelled.");
        }
    }
    
    private void deleteBackup() {
        System.out.println("Available backups:");
        backupService.listBackups();
        System.out.print("Enter backup timestamp to delete (yyyyMMdd_HHmmss): ");
        String timestamp = scanner.nextLine().trim();
        
        if (timestamp.isEmpty()) {
            System.out.println("No timestamp provided.");
            return;
        }
        
        System.out.print("Are you sure you want to delete backup " + timestamp + "? (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if ("y".equals(confirm) || "yes".equals(confirm)) {
            backupService.deleteBackup(timestamp);
        } else {
            System.out.println("Delete cancelled.");
        }
    }
    
    private void performDirectoryAnalysis() {
        System.out.println("Select directory to analyze:");
        System.out.println("1. Data directory");
        System.out.println("2. Backup directory");
        System.out.println("3. Custom directory");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        String dirPath;
        switch (choice) {
            case 1 -> dirPath = "data";
            case 2 -> dirPath = "backup";
            case 3 -> {
                System.out.print("Enter directory path: ");
                dirPath = scanner.nextLine().trim();
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        
        System.out.println("Performing recursive directory analysis...");
        RecursiveFileUtils.printDirectoryStats(dirPath);
    }
    
    private void listFilesByDepth() {
        System.out.print("Enter directory path (or press Enter for current directory): ");
        String dirPath = scanner.nextLine().trim();
        if (dirPath.isEmpty()) {
            dirPath = ".";
        }
        
        System.out.print("Enter maximum depth to traverse (0 for current directory only): ");
        int maxDepth = scanner.nextInt();
        scanner.nextLine();
        
        System.out.println("Listing files by depth...");
        RecursiveFileUtils.listFilesByDepth(dirPath, maxDepth);
    }
    
    private void runReports() {
        Menu.displayReportsMenu();
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch(choice) {
            case 1 -> reportingService.printTopStudentsByGpa(3);
            case 2 -> reportingService.printGpaDistribution();
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void addStudent() {
        try {
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            if (!Validator.isValidEmail(email)) {
                System.out.println("Invalid email format.");
                return;
            }
            Student newStudent = studentService.addStudent(name, email, LocalDate.now());
            System.out.println("Student added successfully: " + newStudent);
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    private void listAllStudents() {
        System.out.println("\n--- All Students ---");
        
        List<Student> allStudents = studentService.getAllStudents();
        
        if (allStudents.isEmpty()) {
            System.out.println("No students found in the system.");
            System.out.println("Use 'Add New Student' option to register students.");
        } else {
            // Using an enhanced 'for' loop
            for (Student s : allStudents) {
                System.out.println(s);
            }
            System.out.println("\nTotal students: " + allStudents.size());
        }
        
        System.out.println("--------------------\n");
    }

    /**
     * Demonstrates anonymous inner class usage for custom student sorting.
     * This method shows students sorted by custom criteria using an anonymous Comparator.
     */
    private void listStudentsWithCustomSort() {
        System.out.println("\n--- Students with Custom Sorting ---");
        
        List<Student> students = studentService.getAllStudents();
        
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        // Anonymous inner class implementing Comparator interface
        // This demonstrates anonymous inner classes - a class without a name
        java.util.Comparator<Student> customComparator = new java.util.Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                // Custom sorting logic:
                // 1. First by status (ACTIVE students first)
                // 2. Then by number of enrolled courses (descending)
                // 3. Finally by name (ascending)
                
                // Compare by status first
                if (s1.getStatus() != s2.getStatus()) {
                    // ACTIVE status should come first
                    if (s1.getStatus() == Student.Status.ACTIVE) return -1;
                    if (s2.getStatus() == Student.Status.ACTIVE) return 1;
                    // GRADUATED before INACTIVE
                    if (s1.getStatus() == Student.Status.GRADUATED) return -1;
                    if (s2.getStatus() == Student.Status.GRADUATED) return 1;
                }
                
                // If same status, compare by number of enrolled courses (descending)
                int courseCountDiff = Integer.compare(s2.getEnrolledCourses().size(), s1.getEnrolledCourses().size());
                if (courseCountDiff != 0) {
                    return courseCountDiff;
                }
                
                // If same course count, sort by name (ascending)
                return s1.getFullName().compareToIgnoreCase(s2.getFullName());
            }
        };
        
        // Sort using the anonymous inner class
        students.stream()
                .sorted(customComparator)
                .forEach(student -> {
                    System.out.printf("%-15s | %-20s | Status: %-10s | Courses: %d%n",
                            student.getRegNo(),
                            student.getFullName(),
                            student.getStatus(),
                            student.getEnrolledCourses().size());
                });
        
        System.out.println("(Sorted by: Status > Course Count > Name)");
        System.out.println("--------------------\n");
        
        // Alternative: Using lambda expression (more modern approach for comparison)
        System.out.println("--- Same data sorted with Lambda (for comparison) ---");
        students.stream()
                .sorted((s1, s2) -> {
                    // Same logic as above but using lambda expression
                    if (s1.getStatus() != s2.getStatus()) {
                        if (s1.getStatus() == Student.Status.ACTIVE) return -1;
                        if (s2.getStatus() == Student.Status.ACTIVE) return 1;
                        if (s1.getStatus() == Student.Status.GRADUATED) return -1;
                        if (s2.getStatus() == Student.Status.GRADUATED) return 1;
                    }
                    int courseCountDiff = Integer.compare(s2.getEnrolledCourses().size(), s1.getEnrolledCourses().size());
                    if (courseCountDiff != 0) return courseCountDiff;
                    return s1.getFullName().compareToIgnoreCase(s2.getFullName());
                })
                .limit(5) // Show only top 5 for comparison
                .forEach(student -> System.out.println("  " + student.getFullName() + " (" + student.getStatus() + ")"));
        
        System.out.println("--------------------\n");
    }

    private void updateStudent() {
        System.out.print("Enter Student ID to update: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();
        try {
            System.out.print("Enter new Full Name: ");
            String newName = scanner.nextLine();
            System.out.print("Enter new Email: ");
            String newEmail = scanner.nextLine();
            studentService.updateStudent(studentId, newName, newEmail);
            System.out.println("Student updated successfully.");
        } catch (StudentNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void printStudentProfileAndTranscript() {
        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();
        try {
            System.out.println(studentService.getStudentProfile(studentId));
            System.out.println(studentService.getStudentTranscript(studentId).generateTranscript());
        } catch (StudentNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void addCourse() {
         try {
            System.out.print("Enter Course Code (e.g., CS101): ");
            String code = scanner.nextLine();
            System.out.print("Enter Course Title: ");
            String title = scanner.nextLine();
            System.out.print("Enter Credits: ");
            int credits = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Department: ");
            String dept = scanner.nextLine();
            System.out.print("Enter Semester (SPRING, FALL, SUMMER): ");
            Semester semester = Semester.valueOf(scanner.nextLine().toUpperCase());
            
            // Using the Builder pattern
            Course newCourse = new Course.Builder(code, title)
                .credits(credits)
                .department(dept)
                .semester(semester)
                .build();
            
            courseService.addCourse(newCourse);
            System.out.println("Course added successfully: " + newCourse);
        } catch (IllegalArgumentException e) {
             System.err.println("Invalid input. Please check semester value or other fields.");
        }
    }
    
    private void listAllCourses() {
        System.out.println("\n--- All Courses ---");
        
        List<Course> allCourses = courseService.getAllCourses();
        
        if (allCourses.isEmpty()) {
            System.out.println("No courses found in the system.");
            System.out.println("Use 'Add New Course' option to create courses.");
        } else {
            allCourses.forEach(System.out::println);
            System.out.println("\nTotal courses: " + allCourses.size());
        }
        
        System.out.println("-------------------\n");
    }
    
    private void searchAndFilterCourses() {
        System.out.print("Enter search keyword (or press Enter to skip): ");
        String keyword = scanner.nextLine();
        System.out.print("Filter by Department (or press Enter to skip): ");
        String dept = scanner.nextLine();
        System.out.print("Filter by Semester (SPRING/FALL/SUMMER, or press Enter to skip): ");
        String semStr = scanner.nextLine();
        
        Optional<Semester> semester = semStr.isEmpty() ? Optional.empty() : Optional.of(Semester.valueOf(semStr.toUpperCase()));

        List<Course> results = courseService.searchCourses(keyword, Optional.ofNullable(dept.isEmpty() ? null : dept), semester);

        System.out.println("\n--- Search Results ---");
        if(results.isEmpty()){
            System.out.println("No courses found matching criteria.");
        } else {
            results.forEach(System.out::println);
        }
        System.out.println("----------------------\n");
    }

    private void updateCourse() {
        System.out.print("Enter Course Code to update: ");
        String courseCode = scanner.nextLine();
        try {
            System.out.print("Enter new Course Title: ");
            String newTitle = scanner.nextLine();
            System.out.print("Enter new Credits: ");
            int newCredits = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter new Department: ");
            String newDepartment = scanner.nextLine();
            System.out.print("Enter new Semester (SPRING, FALL, SUMMER): ");
            Semester newSemester = Semester.valueOf(scanner.nextLine().toUpperCase());
            
            courseService.updateCourse(courseCode, newTitle, newCredits, newDepartment, newSemester);
            System.out.println("Course updated successfully.");
        } catch (CourseNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid semester value entered.");
        }
    }

    private void assignInstructorToCourse() {
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();
        System.out.print("Enter Instructor Name: ");
        String instructorName = scanner.nextLine();
        System.out.print("Enter Instructor Email: ");
        String instructorEmail = scanner.nextLine();
        
        try {
            // Create a simple instructor (you might want to enhance this with proper instructor management)
            Instructor instructor = new Instructor(0, instructorName, instructorEmail, "");
            courseService.assignInstructor(courseCode, instructor);
            System.out.println("Instructor assigned successfully.");
        } catch (CourseNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void deactivateCourse() {
        System.out.print("Enter Course Code to deactivate: ");
        String courseCode = scanner.nextLine();
        try {
            courseService.deactivateCourse(courseCode);
            System.out.println("Course deactivated successfully.");
        } catch (CourseNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void reactivateCourse() {
        System.out.print("Enter Course Code to reactivate: ");
        String courseCode = scanner.nextLine();
        try {
            courseService.reactivateCourse(courseCode);
            System.out.println("Course reactivated successfully.");
        } catch (CourseNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void advancedCourseSearch() {
        System.out.println("\n--- Advanced Course Search ---");
        System.out.print("Enter keyword (or press Enter to skip): ");
        String keyword = scanner.nextLine();
        System.out.print("Filter by Department (or press Enter to skip): ");
        String department = scanner.nextLine();
        System.out.print("Filter by Semester (SPRING/FALL/SUMMER, or press Enter to skip): ");
        String semesterStr = scanner.nextLine();
        System.out.print("Filter by Instructor name (or press Enter to skip): ");
        String instructorName = scanner.nextLine();
        System.out.print("Show only active courses? (y/n, or press Enter to skip): ");
        String activeStr = scanner.nextLine();
        System.out.print("Minimum credits (or press Enter to skip): ");
        String minCreditsStr = scanner.nextLine();
        System.out.print("Maximum credits (or press Enter to skip): ");
        String maxCreditsStr = scanner.nextLine();

        Optional<String> keywordOpt = keyword.isEmpty() ? Optional.empty() : Optional.of(keyword);
        Optional<String> departmentOpt = department.isEmpty() ? Optional.empty() : Optional.of(department);
        Optional<Semester> semesterOpt = semesterStr.isEmpty() ? Optional.empty() : 
                                        Optional.of(Semester.valueOf(semesterStr.toUpperCase()));
        Optional<String> instructorOpt = instructorName.isEmpty() ? Optional.empty() : Optional.of(instructorName);
        Optional<Boolean> activeOpt = activeStr.isEmpty() ? Optional.empty() : 
                                     Optional.of(activeStr.toLowerCase().startsWith("y"));
        Optional<Integer> minCreditsOpt = minCreditsStr.isEmpty() ? Optional.empty() : 
                                         Optional.of(Integer.parseInt(minCreditsStr));
        Optional<Integer> maxCreditsOpt = maxCreditsStr.isEmpty() ? Optional.empty() : 
                                         Optional.of(Integer.parseInt(maxCreditsStr));

        try {
            List<Course> results = courseService.advancedSearch(keywordOpt, departmentOpt, semesterOpt, 
                                                              instructorOpt, activeOpt, minCreditsOpt, maxCreditsOpt);
            
            System.out.println("\n--- Advanced Search Results ---");
            if (results.isEmpty()) {
                System.out.println("No courses found matching criteria.");
            } else {
                results.forEach(System.out::println);
            }
            System.out.println("-------------------------------\n");
        } catch (Exception e) {
            System.err.println("Error in search: " + e.getMessage());
        }
    }

    private void showCourseStatistics() {
        System.out.println("\n--- Course Statistics ---");
        System.out.println("Total Active Courses: " + courseService.getTotalActiveCourses());
        System.out.println("Average Credits: " + String.format("%.2f", courseService.getAverageCredits()));
        
        System.out.println("\nDepartments:");
        courseService.getAllDepartments().forEach(dept -> System.out.println("  - " + dept));
        
        System.out.println("\nCourses without Instructor:");
        List<Course> noInstructor = courseService.getCoursesWithoutInstructor();
        if (noInstructor.isEmpty()) {
            System.out.println("  All courses have instructors assigned.");
        } else {
            noInstructor.forEach(course -> System.out.println("  - " + course.getCourseCode().getCode() + ": " + course.getTitle()));
        }
        System.out.println("-------------------------\n");
    }
    
    private void enrollStudentInCourse() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Enter Semester (SPRING, FALL, SUMMER) or press Enter for course default: ");
            String semesterStr = scanner.nextLine();
            
            if (semesterStr.isEmpty()) {
                enrollmentService.enrollStudent(studentId, courseCode);
            } else {
                Semester semester = Semester.valueOf(semesterStr.toUpperCase());
                enrollmentService.enrollStudent(studentId, courseCode, semester);
            }
            System.out.println("Enrollment successful!");
        } catch (StudentNotFoundException | CourseNotFoundException | MaxCreditLimitExceededException | 
                 DuplicateEnrollmentException | PrerequisiteNotMetException e) {
            System.err.println("Enrollment failed: " + e.getMessage());
        } catch (InputMismatchException e) {
             System.err.println("Invalid input. Please enter a number for Student ID.");
             scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid semester. Please use SPRING, FALL, or SUMMER.");
        }
    }
    
    private void recordGrade() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Enter Grade (S, A, B, C, D, F): ");
            Grade grade = Grade.valueOf(scanner.nextLine().toUpperCase());
            enrollmentService.assignGrade(studentId, courseCode, grade);
            System.out.println("Grade recorded successfully.");
        } catch (StudentNotFoundException | EnrollmentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch(IllegalArgumentException e) {
            System.err.println("Invalid grade entered.");
        }
    }

    private void unenrollStudentFromCourse() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine();
            enrollmentService.unenrollStudent(studentId, courseCode);
            System.out.println("Student unenrolled successfully.");
        } catch (StudentNotFoundException | EnrollmentNotFoundException | InvalidUnenrollmentException e) {
            System.err.println("Unenrollment failed: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Student ID.");
            scanner.nextLine();
        }
    }

    private void recordMarks() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Enter Marks (0-100): ");
            double marks = scanner.nextDouble();
            scanner.nextLine();
            enrollmentService.assignMarks(studentId, courseCode, marks);
            System.out.println("Marks recorded successfully. Grade automatically assigned.");
        } catch (StudentNotFoundException | EnrollmentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid marks. Please enter a value between 0 and 100.");
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for marks.");
            scanner.nextLine();
        }
    }

    private void viewStudentEnrollments() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Semester (SPRING, FALL, SUMMER) or press Enter for all active: ");
            String semesterStr = scanner.nextLine();

            List<Enrollment> enrollments;
            if (semesterStr.isEmpty()) {
                enrollments = enrollmentService.getActiveEnrollments(studentId);
                System.out.println("\n--- All Active Enrollments ---");
            } else {
                Semester semester = Semester.valueOf(semesterStr.toUpperCase());
                enrollments = enrollmentService.getStudentEnrollments(studentId, semester);
                System.out.println(String.format("\n--- %s Semester Enrollments ---", semester.name()));
            }

            if (enrollments.isEmpty()) {
                System.out.println("No enrollments found.");
            } else {
                enrollments.forEach(System.out::println);
            }
            System.out.println("--------------------------------\n");
        } catch (StudentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid semester. Please use SPRING, FALL, or SUMMER.");
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Student ID.");
            scanner.nextLine();
        }
    }

    private void calculateSemesterGPA() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Semester (SPRING, FALL, SUMMER): ");
            Semester semester = Semester.valueOf(scanner.nextLine().toUpperCase());
            
            double gpa = enrollmentService.calculateSemesterGPA(studentId, semester);
            System.out.println(String.format("Semester GPA for %s: %.2f", semester.name(), gpa));
        } catch (StudentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid semester. Please use SPRING, FALL, or SUMMER.");
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Student ID.");
            scanner.nextLine();
        }
    }

    private void generateOfficialTranscript() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            
            Student student = dataStore.findStudentById(studentId)
                    .orElseThrow(() -> new StudentNotFoundException("Student not found."));
            
            Transcript officialTranscript = Transcript.createOfficialTranscript(student);
            System.out.println(officialTranscript.generateTranscript());
        } catch (StudentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Student ID.");
            scanner.nextLine();
        }
    }

    private void generateUnofficialTranscript() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            
            Student student = dataStore.findStudentById(studentId)
                    .orElseThrow(() -> new StudentNotFoundException("Student not found."));
            
            Transcript unofficialTranscript = Transcript.createUnnofficialTranscript(student);
            System.out.println(unofficialTranscript.generateTranscript());
        } catch (StudentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Student ID.");
            scanner.nextLine();
        }
    }

    private void generateSemesterTranscript() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Semester (SPRING, FALL, SUMMER): ");
            Semester semester = Semester.valueOf(scanner.nextLine().toUpperCase());
            
            Student student = dataStore.findStudentById(studentId)
                    .orElseThrow(() -> new StudentNotFoundException("Student not found."));
            
            Transcript semesterTranscript = Transcript.createSemesterTranscript(student, semester);
            System.out.println(semesterTranscript.generateTranscript());
        } catch (StudentNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid semester. Please use SPRING, FALL, or SUMMER.");
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Student ID.");
            scanner.nextLine();
        }
    }

    private void deactivateStudent() {
        System.out.print("Enter Student ID to deactivate: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();
        try {
            studentService.deactivateStudent(studentId);
            System.out.println("Student deactivated successfully.");
        } catch (StudentNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void reactivateStudent() {
        System.out.print("Enter Student ID to reactivate: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();
        try {
            studentService.reactivateStudent(studentId);
            System.out.println("Student reactivated successfully.");
        } catch (StudentNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}