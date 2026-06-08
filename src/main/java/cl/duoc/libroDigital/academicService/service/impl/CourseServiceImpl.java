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
        return courseRepository.findById(id).map(updatedCourse -> {
            if (course.getName() != null) updatedCourse.setName(course.getName());
            if (course.getLevelId() != null) updatedCourse.setLevelId(course.getLevelId());
            if (course.getAcademicYearId() != null) updatedCourse.setAcademicYearId(course.getAcademicYearId());
            if (course.getShiftId() != null) updatedCourse.setShiftId(course.getShiftId());
            if (course.getHeadTeacherId() != null) updatedCourse.setHeadTeacherId(course.getHeadTeacherId());
            if (course.getMaxCapacity() != null) updatedCourse.setMaxCapacity(course.getMaxCapacity());
            if (course.getClassroom() != null) updatedCourse.setClassroom(course.getClassroom());
            if (course.getCourseStatusId() != null) updatedCourse.setCourseStatusId(course.getCourseStatusId());
            return courseRepository.save(updatedCourse);
        }).orElse(null);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
