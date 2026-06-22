package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.repository.CourseRepository;
import cl.duoc.libroDigital.academicService.service.CourseService;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final AcademicEntityValidator validator;

    public CourseServiceImpl(CourseRepository courseRepository, AcademicEntityValidator validator) {
        this.courseRepository = courseRepository;
        this.validator = validator;
    }

    @Override
    public Course createCourse(Course course) {
        validator.validateCourseForSave(course);
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
        return courseRepository.findById(id).map(updatedCourse -> {
            if (course.getName() != null) updatedCourse.setName(course.getName());
            if (course.getLevelId() != null) updatedCourse.setLevelId(course.getLevelId());
            if (course.getAcademicYearId() != null) updatedCourse.setAcademicYearId(course.getAcademicYearId());
            if (course.getShiftId() != null) updatedCourse.setShiftId(course.getShiftId());
            if (course.getHeadTeacherId() != null) updatedCourse.setHeadTeacherId(course.getHeadTeacherId());
            if (course.getMaxCapacity() != null) updatedCourse.setMaxCapacity(course.getMaxCapacity());
            if (course.getClassroom() != null) updatedCourse.setClassroom(course.getClassroom());
            if (course.getCourseStatusId() != null) updatedCourse.setCourseStatusId(course.getCourseStatusId());

            validator.validateCourseForSave(updatedCourse);
            return courseRepository.save(updatedCourse);
        }).orElseThrow(() -> new NotFoundException("Curso no encontrado con id " + id));
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
