package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Course createCourse(Course course);

    List<Course> getAllCourses();

    Optional<Course> getCourseById(Long id);
}