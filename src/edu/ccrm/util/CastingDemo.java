package edu.ccrm.util;

import edu.ccrm.domain.person.Person;
import edu.ccrm.domain.person.Student;
import edu.ccrm.domain.person.Instructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates upcasting, downcasting, and instanceof checks.
 * This class shows when and how to use casting in Java object-oriented programming.
 */
public class CastingDemo {
    
    /**
     * Demonstrates casting concepts with Person, Student, and Instructor hierarchy.
     */
    public static void demonstrateCasting() {
        System.out.println("=== Casting Demonstration ===\n");
        
        // Create instances of Student and Instructor
        Student student = new Student(1, "R2025001", "John Doe", "john@example.com", LocalDate.now());
        Instructor instructor = new Instructor(2, "Dr. Jane Smith", "jane.smith@university.edu", "Computer Science");
        
        System.out.println("1. Original Objects:");
        System.out.println("Student: " + student.getProfile());
        System.out.println("Instructor: " + instructor.getProfile());
        
        // Upcasting (Implicit) - going from specific to general
        System.out.println("\n2. Upcasting (Implicit - Automatic):");
        Person person1 = student;      // Student -> Person (automatic upcasting)
        Person person2 = instructor;   // Instructor -> Person (automatic upcasting)
        
        System.out.println("person1 (Student as Person): " + person1.getClass().getSimpleName());
        System.out.println("person2 (Instructor as Person): " + person2.getClass().getSimpleName());
        
        // Polymorphism in action - virtual method invocation
        System.out.println("\n3. Polymorphism - Virtual Method Invocation:");
        System.out.println("person1.getProfile() calls Student's getProfile():");
        System.out.println(person1.getProfile());
        System.out.println("\nperson2.getProfile() calls Instructor's getProfile():");
        System.out.println(person2.getProfile());
        
        // instanceof checks - determining actual object types
        System.out.println("\n4. instanceof Checks:");
        System.out.println("person1 instanceof Student: " + (person1 instanceof Student));
        System.out.println("person1 instanceof Instructor: " + (person1 instanceof Instructor));
        System.out.println("person1 instanceof Person: " + (person1 instanceof Person));
        System.out.println("person2 instanceof Student: " + (person2 instanceof Student));
        System.out.println("person2 instanceof Instructor: " + (person2 instanceof Instructor));
        System.out.println("person2 instanceof Person: " + (person2 instanceof Person));
        
        // Downcasting (Explicit) - going from general to specific
        System.out.println("\n5. Downcasting (Explicit - Manual):");
        
        // Safe downcasting with instanceof check
        if (person1 instanceof Student) {
            Student downcastStudent = (Student) person1;  // Explicit downcast
            System.out.println("Successfully downcast person1 to Student:");
            System.out.println("  Registration Number: " + downcastStudent.getRegNo());
            System.out.println("  Status: " + downcastStudent.getStatus());
            System.out.println("  Enrolled Courses: " + downcastStudent.getEnrolledCourses().size());
        }
        
        if (person2 instanceof Instructor) {
            Instructor downcastInstructor = (Instructor) person2;  // Explicit downcast
            System.out.println("Successfully downcast person2 to Instructor:");
            System.out.println("  Department: " + downcastInstructor.getDepartment());
            System.out.println("  Assigned Courses: " + downcastInstructor.getAssignedCourses().size());
        }
        
        // Demonstrating the need for downcasting
        System.out.println("\n6. Why Downcasting is Necessary:");
        System.out.println("When we have a Person reference, we can only access Person methods:");
        System.out.println("person1.getFullName(): " + person1.getFullName());
        System.out.println("person1.getEmail(): " + person1.getEmail());
        // System.out.println(person1.getRegNo()); // This would cause compilation error!
        System.out.println("To access Student-specific methods like getRegNo(), we need to downcast.");
        
        // Unsafe downcasting example (what NOT to do)
        System.out.println("\n7. Unsafe Downcasting (Don't do this!):");
        try {
            // This will compile but throw ClassCastException at runtime
            // because person2 is actually an Instructor, not a Student
            // Student wrongCast = (Student) person2;  // Uncomment to see exception
            System.out.println("Commented out unsafe cast to prevent ClassCastException");
        } catch (ClassCastException e) {
            System.err.println("ClassCastException: " + e.getMessage());
        }
        
        // Modern pattern matching with instanceof (Java 16+)
        System.out.println("\n8. Modern Pattern Matching (Java 16+):");
        processPersonModern(person1);
        processPersonModern(person2);
        
        // Practical example: Processing a mixed list
        System.out.println("\n9. Practical Example - Processing Mixed List:");
        List<Person> people = new ArrayList<>();
        people.add(student);
        people.add(instructor);
        people.add(new Student(3, "R2025002", "Alice Johnson", "alice@example.com", LocalDate.now()));
        people.add(new Instructor(4, "Prof. Bob Wilson", "bob.wilson@university.edu", "Mathematics"));
        
        processPersonList(people);
        
        System.out.println("\n=== End of Casting Demonstration ===");
    }
    
    /**
     * Modern approach using pattern matching with instanceof (Java 16+).
     * If your Java version doesn't support this, the traditional approach works too.
     */
    private static void processPersonModern(Person person) {
        System.out.println("Processing: " + person.getFullName());
        
        // Pattern matching automatically creates and casts the variable
        if (person instanceof Student student) {
            // 'student' variable is automatically created and cast
            System.out.println("  -> Student Registration: " + student.getRegNo());
            System.out.println("  -> Status: " + student.getStatus());
        } else if (person instanceof Instructor instructor) {
            // 'instructor' variable is automatically created and cast
            System.out.println("  -> Instructor Department: " + instructor.getDepartment());
        }
    }
    
    /**
     * Traditional approach for older Java versions.
     */
    private static void processPersonTraditional(Person person) {
        System.out.println("Processing: " + person.getFullName());
        
        if (person instanceof Student) {
            Student student = (Student) person;  // Manual cast
            System.out.println("  -> Student Registration: " + student.getRegNo());
            System.out.println("  -> Status: " + student.getStatus());
        } else if (person instanceof Instructor) {
            Instructor instructor = (Instructor) person;  // Manual cast
            System.out.println("  -> Instructor Department: " + instructor.getDepartment());
        }
    }
    
    /**
     * Demonstrates processing a list with mixed types using casting.
     */
    private static void processPersonList(List<Person> people) {
        int studentCount = 0;
        int instructorCount = 0;
        int activeStudents = 0;
        
        System.out.println("Processing list of " + people.size() + " people:");
        
        for (Person person : people) {
            System.out.println("\n  " + person.getClass().getSimpleName() + ": " + person.getFullName());
            
            // Use instanceof and casting to access specific functionality
            if (person instanceof Student) {
                studentCount++;
                Student student = (Student) person;
                
                if (student.getStatus() == Student.Status.ACTIVE) {
                    activeStudents++;
                }
                
                System.out.println("    Registration: " + student.getRegNo());
                System.out.println("    Status: " + student.getStatus());
                System.out.println("    Enrolled Courses: " + student.getEnrolledCourses().size());
                
            } else if (person instanceof Instructor) {
                instructorCount++;
                Instructor instructor = (Instructor) person;
                
                System.out.println("    Department: " + instructor.getDepartment());
                System.out.println("    Assigned Courses: " + instructor.getAssignedCourses().size());
            }
        }
        
        // Summary
        System.out.println("\nSummary:");
        System.out.println("  Total People: " + people.size());
        System.out.println("  Students: " + studentCount + " (Active: " + activeStudents + ")");
        System.out.println("  Instructors: " + instructorCount);
    }
    
    /**
     * Demonstrates when casting is useful in real-world scenarios.
     */
    public static void demonstrateRealWorldUsage() {
        System.out.println("\n=== Real-World Casting Usage ===");
        
        // Scenario: Method that accepts Person but needs specific behavior
        Student student = new Student(1, "R2025001", "John Doe", "john@example.com", LocalDate.now());
        Instructor instructor = new Instructor(2, "Dr. Smith", "smith@example.com", "CS");
        
        // Method that processes different types of people differently
        processForEmailNotification(student);
        processForEmailNotification(instructor);
        
        // Scenario: UI components that handle different object types
        displayPersonInUI(student);
        displayPersonInUI(instructor);
    }
    
    /**
     * Example method that needs to handle Person objects differently based on their actual type.
     */
    private static void processForEmailNotification(Person person) {
        System.out.println("\nPreparing email notification for: " + person.getFullName());
        
        if (person instanceof Student) {
            Student student = (Student) person;
            System.out.println("  Email Type: Student Notification");
            System.out.println("  Registration: " + student.getRegNo());
            System.out.println("  Current Status: " + student.getStatus());
            System.out.println("  Template: student_notification.html");
            
        } else if (person instanceof Instructor) {
            Instructor instructor = (Instructor) person;
            System.out.println("  Email Type: Faculty Notification");
            System.out.println("  Department: " + instructor.getDepartment());
            System.out.println("  Template: faculty_notification.html");
        }
    }
    
    /**
     * Example UI method that displays person information differently based on type.
     */
    private static void displayPersonInUI(Person person) {
        System.out.println("\nDisplaying in UI: " + person.getFullName());
        
        if (person instanceof Student) {
            Student student = (Student) person;
            System.out.println("  UI Component: StudentCard");
            System.out.println("  Show Registration Number: " + student.getRegNo());
            System.out.println("  Show Status Badge: " + student.getStatus());
            System.out.println("  Show Course Count: " + student.getEnrolledCourses().size());
            
        } else if (person instanceof Instructor) {
            Instructor instructor = (Instructor) person;
            System.out.println("  UI Component: InstructorCard");
            System.out.println("  Show Department: " + instructor.getDepartment());
            System.out.println("  Show Course Load: " + instructor.getAssignedCourses().size());
        }
    }
    
    /**
     * Main method to run all demonstrations.
     */
    public static void main(String[] args) {
        demonstrateCasting();
        demonstrateRealWorldUsage();
    }
}