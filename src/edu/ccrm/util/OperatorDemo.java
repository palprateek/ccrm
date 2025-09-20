package edu.ccrm.util;

/**
 * Demonstrates Java operators and operator precedence.
 * This class shows primitive variables, arithmetic, relational, logical, and bitwise operators.
 */
public class OperatorDemo {
    
    /**
     * Demonstrates all types of operators with precedence examples.
     */
    public static void demonstrateOperators() {
        System.out.println("=== Operator Demonstration ===\n");
        
        // Primitive variables
        int a = 10, b = 3, c = 5;
        double gpa = 8.5;
        boolean isActive = true;
        boolean isHonorStudent = false;
        
        // Arithmetic operators with precedence demonstration
        System.out.println("1. Arithmetic Operators:");
        System.out.println("a = " + a + ", b = " + b + ", c = " + c);
        
        // Operator precedence: *, /, % have higher precedence than +, -
        int precedenceExample = a + b * c - a / b; // = 10 + (3 * 5) - (10 / 3) = 10 + 15 - 3 = 22
        System.out.println("a + b * c - a / b = " + precedenceExample + " (multiplication and division first)");
        
        System.out.println("Addition: " + (a + b) + " = " + (a + b));
        System.out.println("Subtraction: " + a + " - " + b + " = " + (a - b));
        System.out.println("Multiplication: " + b + " * " + c + " = " + (b * c));
        System.out.println("Division: " + a + " / " + b + " = " + (a / b) + " (integer division)");
        System.out.println("Modulo: " + a + " % " + b + " = " + (a % b));
        System.out.println("Floating division: " + a + " / " + b + ".0 = " + (a / (double)b));
        
        // Unary operators
        System.out.println("\nUnary operators:");
        int x = 5;
        System.out.println("x = " + x);
        System.out.println("++x = " + (++x) + " (pre-increment)");
        System.out.println("x++ = " + (x++) + " (post-increment), x is now " + x);
        System.out.println("--x = " + (--x) + " (pre-decrement)");
        
        // Relational operators
        System.out.println("\n2. Relational Operators:");
        System.out.println("gpa = " + gpa);
        System.out.println("gpa > 8.0: " + (gpa > 8.0));
        System.out.println("gpa >= 8.5: " + (gpa >= 8.5));
        System.out.println("gpa < 9.0: " + (gpa < 9.0));
        System.out.println("gpa <= 8.5: " + (gpa <= 8.5));
        System.out.println("gpa == 8.5: " + (gpa == 8.5));
        System.out.println("gpa != 8.0: " + (gpa != 8.0));
        
        // Logical operators with short-circuit evaluation
        System.out.println("\n3. Logical Operators:");
        System.out.println("isActive = " + isActive + ", isHonorStudent = " + isHonorStudent);
        System.out.println("isActive && isHonorStudent: " + (isActive && isHonorStudent));
        System.out.println("isActive || isHonorStudent: " + (isActive || isHonorStudent));
        System.out.println("!isActive: " + (!isActive));
        
        // Complex logical expression with precedence
        boolean eligibleForScholarship = gpa >= 8.0 && isActive || isHonorStudent; // && has higher precedence than ||
        System.out.println("Eligible for scholarship (gpa >= 8.0 && isActive || isHonorStudent): " + eligibleForScholarship);
        
        // Bitwise operators - useful for flags and permissions
        System.out.println("\n4. Bitwise Operators:");
        int permissions = 7; // Binary: 111 (read=4, write=2, execute=1)
        int readFlag = 4;    // Binary: 100
        int writeFlag = 2;   // Binary: 010
        int executeFlag = 1; // Binary: 001
        
        System.out.println("permissions = " + permissions + " (binary: " + Integer.toBinaryString(permissions) + ")");
        System.out.println("readFlag = " + readFlag + " (binary: " + Integer.toBinaryString(readFlag) + ")");
        
        // Bitwise AND for checking flags
        boolean canRead = (permissions & readFlag) != 0;
        boolean canWrite = (permissions & writeFlag) != 0;
        boolean canExecute = (permissions & executeFlag) != 0;
        
        System.out.println("Can read: " + canRead + " (permissions & readFlag = " + (permissions & readFlag) + ")");
        System.out.println("Can write: " + canWrite + " (permissions & writeFlag = " + (permissions & writeFlag) + ")");
        System.out.println("Can execute: " + canExecute + " (permissions & executeFlag = " + (permissions & executeFlag) + ")");
        
        // Bitwise OR for setting flags
        int newPermissions = readFlag | executeFlag; // Set read and execute flags
        System.out.println("Setting read and execute: " + newPermissions + " (binary: " + Integer.toBinaryString(newPermissions) + ")");
        
        // Bitwise XOR
        int xorResult = permissions ^ writeFlag; // Toggle write flag
        System.out.println("Toggle write flag: " + xorResult + " (binary: " + Integer.toBinaryString(xorResult) + ")");
        
        // Bitwise complement
        int complement = ~permissions;
        System.out.println("Bitwise complement of permissions: " + complement);
        
        // Shift operators
        System.out.println("\n5. Shift Operators:");
        int shiftValue = 8; // Binary: 1000
        System.out.println("shiftValue = " + shiftValue + " (binary: " + Integer.toBinaryString(shiftValue) + ")");
        System.out.println("Left shift by 2: " + (shiftValue << 2) + " (binary: " + Integer.toBinaryString(shiftValue << 2) + ")");
        System.out.println("Right shift by 2: " + (shiftValue >> 2) + " (binary: " + Integer.toBinaryString(shiftValue >> 2) + ")");
        
        // Assignment operators
        System.out.println("\n6. Assignment Operators:");
        int assignmentDemo = 10;
        System.out.println("assignmentDemo = " + assignmentDemo);
        assignmentDemo += 5; // equivalent to: assignmentDemo = assignmentDemo + 5
        System.out.println("After += 5: " + assignmentDemo);
        assignmentDemo -= 3;
        System.out.println("After -= 3: " + assignmentDemo);
        assignmentDemo *= 2;
        System.out.println("After *= 2: " + assignmentDemo);
        assignmentDemo /= 4;
        System.out.println("After /= 4: " + assignmentDemo);
        assignmentDemo %= 3;
        System.out.println("After %= 3: " + assignmentDemo);
        
        // Ternary operator (conditional operator)
        System.out.println("\n7. Ternary Operator:");
        String gradeLevel = gpa >= 8.0 ? "Excellent" : gpa >= 7.0 ? "Good" : "Average";
        System.out.println("Grade level based on GPA " + gpa + ": " + gradeLevel);
        
        // Demonstration of operator precedence order
        System.out.println("\n8. Operator Precedence Demonstration:");
        int precedenceTest1 = 2 + 3 * 4; // = 2 + 12 = 14 (not 20)
        int precedenceTest2 = (2 + 3) * 4; // = 5 * 4 = 20
        boolean precedenceTest3 = true || false && false; // = true || (false && false) = true || false = true
        boolean precedenceTest4 = (true || false) && false; // = true && false = false
        
        System.out.println("2 + 3 * 4 = " + precedenceTest1 + " (* has higher precedence than +)");
        System.out.println("(2 + 3) * 4 = " + precedenceTest2 + " (parentheses override precedence)");
        System.out.println("true || false && false = " + precedenceTest3 + " (&& has higher precedence than ||)");
        System.out.println("(true || false) && false = " + precedenceTest4 + " (parentheses override precedence)");
        
        System.out.println("\n=== End of Operator Demonstration ===");
    }
    
    /**
     * Main method to run the demonstration.
     */
    public static void main(String[] args) {
        demonstrateOperators();
    }
}