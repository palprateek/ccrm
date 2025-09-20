package edu.ccrm.service;

import edu.ccrm.domain.course.Course;
import edu.ccrm.domain.person.Student;
import edu.ccrm.util.IdGenerator;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store for the application.
 * Acts as a single source of truth for domain objects.
 */
public class DataStore {
    private final Map<Integer, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();

    public Student addStudent(String fullName, String email, LocalDate registrationDate) {
        int id = IdGenerator.getNextStudentId();
        String regNo = "R" + (2025000 + id);
        Student student = new Student(id, regNo, fullName, email, registrationDate);
        students.put(id, student);
        return student;
    }

    public Student addStudent(String fullName, String email, LocalDate registrationDate, LocalDate birthDate) {
        int id = IdGenerator.getNextStudentId();
        String regNo = "R" + (2025000 + id);
        Student student = new Student(id, regNo, fullName, email, registrationDate, birthDate);
        students.put(id, student);
        return student;
    }

    public void addCourse(Course course) {
        courses.put(course.getCourseCode().getCode(), course);
    }

    public void updateCourse(Course course) {
        courses.put(course.getCourseCode().getCode(), course);
    }

    public Optional<Student> findStudentById(int id) {
        return Optional.ofNullable(students.get(id));
    }
    
    public Optional<Course> findCourseByCode(String code) {
        return Optional.ofNullable(courses.get(code));
    }

    public Map<Integer, Student> getStudents() {
        return students;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }
}