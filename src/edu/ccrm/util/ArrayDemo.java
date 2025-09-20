package edu.ccrm.util;

import java.util.Arrays;

/**
 * Demonstrates arrays and Arrays utility class usage.
 * Shows sorting, searching, comparing, and other utility operations.
 */
public class ArrayDemo {
    
    /**
     * Demonstrates array operations and Arrays utility methods.
     */
    public static void demonstrateArrays() {
        System.out.println("=== Array and Arrays Utility Demonstration ===\n");
        
        // 1. Array declaration and initialization
        System.out.println("1. Array Declaration and Initialization:");
        
        // Different ways to declare and initialize arrays
        String[] courseCodes = {"CS101", "MATH201", "ENG102", "PHY301", "CS205", "BIO150"};
        int[] studentIds = {1001, 1005, 1003, 1008, 1002, 1010, 1007};
        double[] gpas = {8.5, 7.2, 9.1, 6.8, 8.9, 7.5, 8.2};
        
        // Array with explicit size
        String[] departments = new String[5];
        departments[0] = "Computer Science";
        departments[1] = "Mathematics";
        departments[2] = "Physics";
        departments[3] = "Biology";
        departments[4] = "English";
        
        System.out.println("Original course codes: " + Arrays.toString(courseCodes));
        System.out.println("Original student IDs: " + Arrays.toString(studentIds));
        System.out.println("Original GPAs: " + Arrays.toString(gpas));
        System.out.println("Departments: " + Arrays.toString(departments));
        
        // 2. Arrays.sort() - sorting arrays
        System.out.println("\n2. Sorting Arrays:");
        
        // Sort course codes alphabetically
        String[] sortedCourses = Arrays.copyOf(courseCodes, courseCodes.length);
        Arrays.sort(sortedCourses);
        System.out.println("Sorted course codes: " + Arrays.toString(sortedCourses));
        
        // Sort student IDs numerically
        int[] sortedIds = Arrays.copyOf(studentIds, studentIds.length);
        Arrays.sort(sortedIds);
        System.out.println("Sorted student IDs: " + Arrays.toString(sortedIds));
        
        // Sort GPAs in ascending order
        double[] sortedGPAs = Arrays.copyOf(gpas, gpas.length);
        Arrays.sort(sortedGPAs);
        System.out.println("Sorted GPAs: " + Arrays.toString(sortedGPAs));
        
        // Sort a portion of array (from index 1 to 4)
        String[] partialSort = Arrays.copyOf(courseCodes, courseCodes.length);
        Arrays.sort(partialSort, 1, 4); // Sort elements from index 1 to 3
        System.out.println("Partial sort (index 1-3): " + Arrays.toString(partialSort));
        
        // 3. Arrays.binarySearch() - searching in sorted arrays
        System.out.println("\n3. Binary Search (requires sorted array):");
        
        // Search for course codes
        int csIndex = Arrays.binarySearch(sortedCourses, "CS101");
        int mathIndex = Arrays.binarySearch(sortedCourses, "MATH201");
        int notFoundIndex = Arrays.binarySearch(sortedCourses, "ART100");
        
        System.out.println("CS101 found at index: " + csIndex);
        System.out.println("MATH201 found at index: " + mathIndex);
        System.out.println("ART100 search result: " + notFoundIndex + " (negative = not found)");
        
        // Search for student IDs
        int studentSearch = Arrays.binarySearch(sortedIds, 1005);
        System.out.println("Student ID 1005 found at index: " + studentSearch);
        
        // 4. Arrays.fill() - filling arrays with values
        System.out.println("\n4. Filling Arrays:");
        
        // Fill entire array
        int[] defaultGrades = new int[6];
        Arrays.fill(defaultGrades, 85);
        System.out.println("Default grades (filled with 85): " + Arrays.toString(defaultGrades));
        
        // Fill portion of array
        Arrays.fill(defaultGrades, 2, 5, 90); // Fill indexes 2-4 with 90
        System.out.println("Partial fill (index 2-4 with 90): " + Arrays.toString(defaultGrades));
        
        // 5. Arrays.equals() and Arrays.deepEquals() - comparing arrays
        System.out.println("\n5. Comparing Arrays:");
        
        int[] grades1 = {85, 90, 88, 92, 87};
        int[] grades2 = {85, 90, 88, 92, 87};
        int[] grades3 = {85, 90, 88, 92, 86};
        
        System.out.println("grades1: " + Arrays.toString(grades1));
        System.out.println("grades2: " + Arrays.toString(grades2));
        System.out.println("grades3: " + Arrays.toString(grades3));
        System.out.println("grades1 equals grades2: " + Arrays.equals(grades1, grades2));
        System.out.println("grades1 equals grades3: " + Arrays.equals(grades1, grades3));
        
        // For multi-dimensional arrays, use deepEquals
        String[][] courseSchedule1 = {{"CS101", "09:00"}, {"MATH201", "11:00"}};
        String[][] courseSchedule2 = {{"CS101", "09:00"}, {"MATH201", "11:00"}};
        System.out.println("2D arrays equal: " + Arrays.deepEquals(courseSchedule1, courseSchedule2));
        
        // 6. Arrays.copyOf() and Arrays.copyOfRange() - copying arrays
        System.out.println("\n6. Copying Arrays:");
        
        // Copy entire array
        String[] coursesCopy = Arrays.copyOf(courseCodes, courseCodes.length);
        System.out.println("Full copy: " + Arrays.toString(coursesCopy));
        
        // Copy with different length (truncate or pad with nulls)
        String[] shorterCopy = Arrays.copyOf(courseCodes, 3);
        String[] longerCopy = Arrays.copyOf(courseCodes, 8);
        System.out.println("Shorter copy (3 elements): " + Arrays.toString(shorterCopy));
        System.out.println("Longer copy (8 elements): " + Arrays.toString(longerCopy));
        
        // Copy range of elements
        String[] rangeCopy = Arrays.copyOfRange(courseCodes, 2, 5); // Elements from index 2 to 4
        System.out.println("Range copy (index 2-4): " + Arrays.toString(rangeCopy));
        
        // 7. Arrays.hashCode() and Arrays.deepHashCode() - hash codes
        System.out.println("\n7. Array Hash Codes:");
        
        System.out.println("Hash code of grades1: " + Arrays.hashCode(grades1));
        System.out.println("Hash code of grades2: " + Arrays.hashCode(grades2));
        System.out.println("Hash code of 2D array: " + Arrays.deepHashCode(courseSchedule1));
        
        // 8. Multi-dimensional arrays
        System.out.println("\n8. Multi-dimensional Arrays:");
        
        // 2D array for grade matrix (students x subjects)
        int[][] gradeMatrix = {
            {85, 90, 88}, // Student 1: Math, Science, English
            {92, 87, 94}, // Student 2: Math, Science, English
            {78, 85, 82}  // Student 3: Math, Science, English
        };
        
        System.out.println("Grade matrix:");
        for (int i = 0; i < gradeMatrix.length; i++) {
            System.out.println("Student " + (i + 1) + ": " + Arrays.toString(gradeMatrix[i]));
        }
        
        // Using Arrays.deepToString() for multi-dimensional arrays
        System.out.println("Grade matrix (deepToString): " + Arrays.deepToString(gradeMatrix));
        
        // 9. Enhanced for loop with arrays
        System.out.println("\n9. Enhanced For Loop:");
        
        System.out.print("Course codes using enhanced for: ");
        for (String course : courseCodes) {
            System.out.print(course + " ");
        }
        System.out.println();
        
        System.out.print("GPAs above 8.0: ");
        for (double gpa : gpas) {
            if (gpa > 8.0) {
                System.out.print(gpa + " ");
            }
        }
        System.out.println();
        
        // 10. Practical examples for CCRM system
        System.out.println("\n10. CCRM System Examples:");
        
        // Find students with high GPA
        System.out.println("Students with GPA > 8.0:");
        for (int i = 0; i < studentIds.length; i++) {
            if (gpas[i] > 8.0) {
                System.out.println("Student ID: " + studentIds[i] + ", GPA: " + gpas[i]);
            }
        }
        
        // Sort students by GPA (parallel arrays)
        Integer[] indices = new Integer[studentIds.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        
        // Sort indices based on GPA values
        Arrays.sort(indices, (i, j) -> Double.compare(gpas[j], gpas[i])); // Descending order
        
        System.out.println("\nStudents sorted by GPA (highest first):");
        for (int idx : indices) {
            System.out.println("Student ID: " + studentIds[idx] + ", GPA: " + gpas[idx]);
        }
        
        System.out.println("\n=== End of Array Demonstration ===");
    }
    
    /**
     * Demonstrates linear search vs binary search performance concept.
     */
    public static void demonstrateSearchPerformance() {
        System.out.println("\n=== Search Performance Demonstration ===");
        
        // Create a large array of registration numbers
        String[] regNumbers = new String[1000];
        for (int i = 0; i < regNumbers.length; i++) {
            regNumbers[i] = "R" + String.format("%04d", 2025000 + i);
        }
        
        String targetReg = "R2025500";
        
        // Linear search
        long startTime = System.nanoTime();
        int linearResult = -1;
        for (int i = 0; i < regNumbers.length; i++) {
            if (regNumbers[i].equals(targetReg)) {
                linearResult = i;
                break;
            }
        }
        long linearTime = System.nanoTime() - startTime;
        
        // Binary search (array is already sorted)
        startTime = System.nanoTime();
        int binaryResult = Arrays.binarySearch(regNumbers, targetReg);
        long binaryTime = System.nanoTime() - startTime;
        
        System.out.println("Target: " + targetReg);
        System.out.println("Linear search result: " + linearResult + " (time: " + linearTime + " ns)");
        System.out.println("Binary search result: " + binaryResult + " (time: " + binaryTime + " ns)");
        System.out.println("Binary search is " + (linearTime / Math.max(binaryTime, 1)) + "x faster");
    }
    
    /**
     * Main method to run all demonstrations.
     */
    public static void main(String[] args) {
        demonstrateArrays();
        demonstrateSearchPerformance();
    }
}