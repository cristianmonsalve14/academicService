package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.repository.CourseRepository;
import cl.duoc.libroDigital.academicService.service.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        Optional<Course> existingCourse = courseRepository.findById(id);
        if (existingCourse.isPresent()) {
            Course updatedCourse = existingCourse.get();
            if (course.getName() != null) {
                updatedCourse.setName(course.getName());
            }
            if (course.getGrade() != null) {
                updatedCourse.setGrade(course.getGrade());
            }
            if (course.getAcademicYear() != null) {
                updatedCourse.setAcademicYear(course.getAcademicYear());
            }
            if (course.getShift() != null) {
                updatedCourse.setShift(course.getShift());
            }
            if (course.getHeadTeacherId() != null) {
                updatedCourse.setHeadTeacherId(course.getHeadTeacherId());
            }
            if (course.getMaxCapacity() != null) {
                updatedCourse.setMaxCapacity(course.getMaxCapacity());
            }
            if (course.getLevel() != null) {
                updatedCourse.setLevel(course.getLevel());
            }
            if (course.getClassroom() != null) {
                updatedCourse.setClassroom(course.getClassroom());
            }
            if (course.getCourseStatus() != null) {
                updatedCourse.setCourseStatus(course.getCourseStatus());
            }
            return courseRepository.save(updatedCourse);
        }
        return null;
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}