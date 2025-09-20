# CCRM Usage Guide

This guide provides comprehensive instructions, sample data files, and command flows for using the Campus Course & Records Manager (CCRM) application.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 21 or later
- Command line or terminal access

### Compilation and Execution
```bash
# Compile the project
javac -d out --source-path src src/edu/ccrm/Main.java

# Run the application
java -cp out edu.ccrm.Main

# Run with assertions enabled (recommended for development)
java -ea -cp out edu.ccrm.Main
```

## Sample Data Files

The application uses CSV files for data import. Sample files are provided in the `data/` folder.

### `students.csv` Format
```csv
ID,FullName,Email,RegistrationDate,Status,RegNo,BirthDate,GraduationDate,LastLoginDate,EnrolledCourses,GPA
1,John Smith,john.smith@example.com,2024-01-15,ACTIVE,R2025001,1998-05-12,,2024-09-18T10:30:00,"CS101:A;MATH201:B;ENG101:S",8.67
2,Emily Johnson,emily.johnson@example.com,2024-01-15,ACTIVE,R2025002,1999-03-22,,2024-09-17T14:22:00,"CS101:S;MATH201:A;PHYS101:A",9.33
3,Michael Brown,michael.brown@example.com,2023-08-20,ACTIVE,R2025003,1997-11-08,,2024-09-16T09:15:00,"CS101:B;ENG101:A;HIST101:B",8.33
```

### `courses.csv` Format
```csv
Code,Title,Credits,Department,Semester,InstructorName,InstructorEmail,Active
CS101,Introduction to Computer Science,3,Computer Science,FALL,Dr. James Wilson,james.wilson@university.edu,true
MATH201,Calculus I,4,Mathematics,FALL,Prof. Michael Davis,m.davis@university.edu,true
ENG101,English Composition,3,English,FALL,Dr. Emily Brown,e.brown@university.edu,true
```

## Application Workflow

### Main Menu Navigation
The application presents a hierarchical menu system:

```
===== CCRM Main Menu =====
1. Manage Students
2. Manage Courses
3. Manage Enrollments & Grades
4. File Utilities
5. Run Reports
0. Exit
```

## Common Usage Scenarios

### Scenario 1: Setting Up a New Academic Term

1. **Import Course Data**
   - Select `4. File Utilities` → `2. Import Courses from CSV`
   - Use the provided `data/courses.csv` file
   - Verify courses are loaded with `2. Manage Courses` → `2. List All Courses`

2. **Import Student Data**
   - Select `4. File Utilities` → `1. Import Students from CSV`
   - Use the provided `data/students.csv` file
   - Verify students are loaded with `1. Manage Students` → `2. List All Students`

### Scenario 2: Student Enrollment Process

1. **Enroll a Student in Courses**
   - Select `3. Manage Enrollments & Grades` → `1. Enroll Student in a Course`
   - Enter student ID and course code
   - System validates prerequisites and credit limits

2. **Record Grades**
   - Select `3. Manage Enrollments & Grades` → `3. Record Student Grade`
   - Choose grade type: Letter Grade (A, B, C, D, F) or Pass/Fail (S, U)
   - System automatically calculates GPA

### Scenario 3: Academic Records Management

1. **Generate Transcripts**
   - **Official Transcript**: `3. Manage Enrollments & Grades` → `7. Generate Official Transcript`
   - **Unofficial Transcript**: `3. Manage Enrollments & Grades` → `8. Generate Unofficial Transcript`
   - **Semester Transcript**: `3. Manage Enrollments & Grades` → `9. Generate Semester Transcript`

2. **View Student Profile**
   - Select `1. Manage Students` → `5. Print Student Profile & Transcript`
   - Displays comprehensive student information and academic history

### Scenario 4: Course Management

1. **Add New Course**
   - Select `2. Manage Courses` → `1. Add New Course`
   - Enter course details (code, title, credits, department, semester)
   - Assign instructor information

2. **Search and Filter Courses**
   - Select `2. Manage Courses` → `7. Search & Filter Courses`
   - Filter by department, semester, or active status
   - Use advanced search with multiple criteria

### Scenario 5: Data Management and Backup

1. **Create System Backup**
   - Select `4. File Utilities` → `5. Create Backup (Timestamped)`
   - System creates timestamped backup of all data

2. **Export Data**
   - Select `4. File Utilities` → `3. Export All Data to Files`
   - Exports students, courses, and enrollments to separate CSV files

3. **Restore from Backup**
   - Select `4. File Utilities` → `7. Restore from Backup`
   - Choose from available backup files

## Key Features and Validation Rules

### Student Management
- **Registration Numbers**: Must follow pattern R + 7 digits (e.g., R2025001)
- **Status**: ACTIVE or INACTIVE
- **Credit Limit**: Maximum 20 credits per semester
- **GPA Calculation**: Automatic based on enrolled courses and grades

### Course Management
- **Course Codes**: Format DeptCode + Number (e.g., CS101, MATH201)
- **Credits**: Must be between 1-9 credits
- **Prerequisites**: System validates before enrollment
- **Instructor Assignment**: Each course can have one assigned instructor

### Grade System
- **Letter Grades**: A (9-10), B (8-8.99), C (7-7.99), D (6-6.99), F (0-5.99)
- **Pass/Fail**: S (Satisfactory), U (Unsatisfactory)
- **Grade Points**: Used for GPA calculation

### Enrollment Rules
- Students cannot enroll in the same course twice
- Prerequisites must be completed before enrollment
- Credit limit validation prevents over-enrollment
- Semester-based enrollment tracking

## Reports and Analytics

### Academic Performance Reports
1. **Top Students by GPA**
   - Select `5. Run Reports` → `1. View Top N Students by GPA`
   - Specify number of top students to display

2. **GPA Distribution**
   - Select `5. Run Reports` → `2. View GPA Distribution`
   - Shows distribution across different GPA ranges

3. **Course Statistics**
   - Select `2. Manage Courses` → `9. Course Statistics`
   - Displays enrollment statistics and grade distributions

## Advanced Features

### File System Operations
- **Recursive Directory Analysis**: Analyzes project directory structure
- **File Depth Listing**: Lists files by directory depth level
- **NIO.2 Integration**: Modern file handling with Path and Files APIs

### Data Validation
- **Email Format Validation**: Ensures valid email addresses
- **Date Validation**: Proper date format handling
- **Business Rule Enforcement**: Prevents invalid operations

### Exception Handling
The application handles various scenarios gracefully:
- `StudentNotFoundException`: When student ID doesn't exist
- `CourseNotFoundException`: When course code is invalid
- `DuplicateEnrollmentException`: Prevents duplicate enrollments
- `MaxCreditLimitExceededException`: Enforces credit limits
- `PrerequisiteNotMetException`: Validates course prerequisites

## Tips for Effective Usage

1. **Always backup before major operations**
2. **Use assertions during development** (`java -ea`)
3. **Import sample data first** to explore functionality
4. **Check course prerequisites** before enrollment
5. **Monitor credit limits** during enrollment
6. **Generate regular reports** for academic oversight
7. **Use search and filter features** for large datasets

## Troubleshooting

### Common Issues
- **File Not Found**: Ensure CSV files are in the correct directory
- **Invalid Course Code**: Check course code format (DeptCode + Number)
- **Enrollment Failure**: Verify prerequisites and credit limits
- **Import Errors**: Check CSV file format and headers

### Error Messages
The application provides descriptive error messages for all common scenarios. If you encounter unexpected behavior, enable assertions for detailed debugging information.