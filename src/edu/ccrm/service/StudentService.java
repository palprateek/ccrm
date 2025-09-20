package edu.ccrm.service;

import edu.ccrm.domain.Transcript;
import edu.ccrm.domain.person.Student;
import edu.ccrm.exception.StudentNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final DataStore dataStore;

    public StudentService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Student addStudent(String fullName, String email, LocalDate registrationDate) {
        return dataStore.addStudent(fullName, email, registrationDate);
    }

    public Student addStudent(String fullName, String email, LocalDate registrationDate, LocalDate birthDate) {
        return dataStore.addStudent(fullName, email, registrationDate, birthDate);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(dataStore.getStudents().values());
    }

    public void updateStudent(int id, String newName, String newEmail) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        student.setFullName(newName);
        student.setEmail(newEmail);
    }

    public String getStudentProfile(int id) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        return student.getProfile();
    }

    public Transcript getStudentTranscript(int id) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        return new Transcript(student);
    }

    public void deactivateStudent(int id) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        student.setStatus(Student.Status.INACTIVE);
    }

    public void reactivateStudent(int id) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        student.setStatus(Student.Status.ACTIVE);
    }

    public void updateStudentBirthDate(int id, LocalDate birthDate) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        student.setBirthDate(birthDate);
    }

    public void recordStudentLogin(int id) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        student.recordLogin();
    }

    public void graduateStudent(int id, LocalDate graduationDate) throws StudentNotFoundException {
        Student student = dataStore.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found."));
        student.setGraduationDate(graduationDate);
    }
}