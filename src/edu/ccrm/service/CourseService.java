package edu.ccrm.service;

import edu.ccrm.domain.course.Course;
import edu.ccrm.domain.enrollment.Semester;
import edu.ccrm.domain.person.Instructor;
import edu.ccrm.exception.CourseNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CourseService {
    private final DataStore dataStore;

    public CourseService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void addCourse(Course course) {
        dataStore.addCourse(course);
    }

    public List<Course> getAllCourses() {
        return List.copyOf(dataStore.getCourses().values());
    }

    public List<Course> getActiveCourses() {
        return dataStore.getCourses().values().stream()
                .filter(Course::isActive)
                .collect(Collectors.toList());
    }

    public Optional<Course> findCourseByCode(String courseCode) {
        return dataStore.findCourseByCode(courseCode);
    }

    public void updateCourse(String courseCode, String newTitle, int newCredits, String newDepartment, 
                           Semester newSemester) throws CourseNotFoundException {
        Course course = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("Course with code " + courseCode + " not found."));
        
        // Since Course fields are mostly final, we need to create a new course and replace it
        Course updatedCourse = new Course.Builder(courseCode, newTitle)
                .credits(newCredits)
                .department(newDepartment)
                .semester(newSemester)
                .instructor(course.getInstructor())
                .active(course.isActive())
                .build();
        
        dataStore.updateCourse(updatedCourse);
    }

    public void assignInstructor(String courseCode, Instructor instructor) throws CourseNotFoundException {
        Course course = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("Course with code " + courseCode + " not found."));
        course.setInstructor(instructor);
    }

    public void deactivateCourse(String courseCode) throws CourseNotFoundException {
        Course course = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("Course with code " + courseCode + " not found."));
        course.setActive(false);
    }

    public void reactivateCourse(String courseCode) throws CourseNotFoundException {
        Course course = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("Course with code " + courseCode + " not found."));
        course.setActive(true);
    }

    public List<Course> searchCourses(String keyword, Optional<String> department, Optional<Semester> semester) {
        Predicate<Course> keywordMatch = c -> keyword.isEmpty() || 
                                              c.getTitle().toLowerCase().contains(keyword.toLowerCase()) || 
                                              c.getCourseCode().getCode().equalsIgnoreCase(keyword);
        
        Predicate<Course> deptMatch = c -> department.isEmpty() || c.getDepartment().equalsIgnoreCase(department.get());
        
        Predicate<Course> semesterMatch = c -> semester.isEmpty() || c.getSemester() == semester.get();

        return dataStore.getCourses().values().stream()
                .filter(keywordMatch.and(deptMatch).and(semesterMatch))
                .collect(Collectors.toList());
    }

    // Enhanced search and filter methods using Stream API
    public List<Course> filterByInstructor(String instructorName) {
        return dataStore.getCourses().values().stream()
                .filter(course -> course.getInstructor() != null)
                .filter(course -> course.getInstructor().getFullName().toLowerCase()
                        .contains(instructorName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Course> filterByDepartment(String department) {
        return dataStore.getCourses().values().stream()
                .filter(course -> course.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Course> filterBySemester(Semester semester) {
        return dataStore.getCourses().values().stream()
                .filter(course -> course.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public List<Course> filterByCredits(int minCredits, int maxCredits) {
        return dataStore.getCourses().values().stream()
                .filter(course -> course.getCredits() >= minCredits && course.getCredits() <= maxCredits)
                .collect(Collectors.toList());
    }

    public List<Course> getCoursesWithoutInstructor() {
        return dataStore.getCourses().values().stream()
                .filter(course -> course.getInstructor() == null)
                .collect(Collectors.toList());
    }

    public List<Course> getActiveCoursesWithInstructor() {
        return dataStore.getCourses().values().stream()
                .filter(Course::isActive)
                .filter(course -> course.getInstructor() != null)
                .collect(Collectors.toList());
    }

    // Advanced combined filtering
    public List<Course> advancedSearch(Optional<String> keyword, Optional<String> department, 
                                     Optional<Semester> semester, Optional<String> instructorName, 
                                     Optional<Boolean> activeOnly, Optional<Integer> minCredits, 
                                     Optional<Integer> maxCredits) {
        return dataStore.getCourses().values().stream()
                .filter(course -> keyword.isEmpty() || 
                        course.getTitle().toLowerCase().contains(keyword.get().toLowerCase()) ||
                        course.getCourseCode().getCode().toLowerCase().contains(keyword.get().toLowerCase()))
                .filter(course -> department.isEmpty() || 
                        course.getDepartment().equalsIgnoreCase(department.get()))
                .filter(course -> semester.isEmpty() || 
                        course.getSemester() == semester.get())
                .filter(course -> instructorName.isEmpty() || 
                        (course.getInstructor() != null && 
                         course.getInstructor().getFullName().toLowerCase().contains(instructorName.get().toLowerCase())))
                .filter(course -> activeOnly.isEmpty() || 
                        course.isActive() == activeOnly.get())
                .filter(course -> minCredits.isEmpty() || 
                        course.getCredits() >= minCredits.get())
                .filter(course -> maxCredits.isEmpty() || 
                        course.getCredits() <= maxCredits.get())
                .collect(Collectors.toList());
    }

    // Statistics and reporting using Stream API
    public long getTotalActiveCourses() {
        return dataStore.getCourses().values().stream()
                .filter(Course::isActive)
                .count();
    }

    public List<String> getAllDepartments() {
        return dataStore.getCourses().values().stream()
                .map(Course::getDepartment)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public double getAverageCredits() {
        return dataStore.getCourses().values().stream()
                .filter(Course::isActive)
                .mapToInt(Course::getCredits)
                .average()
                .orElse(0.0);
    }
}