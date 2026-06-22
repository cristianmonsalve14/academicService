package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.repository.CourseRepository;
import cl.duoc.libroDigital.academicService.service.impl.CourseServiceImpl;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void createCourse_savesValidatedCourse() {
        Course course = new Course();
        course.setName("3° Medio A");
        course.setAcademicYearId((short) 2);
        when(courseRepository.save(course)).thenReturn(course);

        Course saved = courseService.createCourse(course);

        assertSame(course, saved);
        verify(validator).validateCourseForSave(course);
        verify(courseRepository).save(course);
    }

    @Test
    void getCourseById_returnsCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setName("2° Medio B");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.getCourseById(1L);

        assertTrue(result.isPresent());
        assertEquals("2° Medio B", result.get().getName());
    }

    @Test
    void updateCourse_notFoundThrows() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> courseService.updateCourse(99L, new Course()));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void deleteCourse_deletesById() {
        courseService.deleteCourse(5L);
        verify(courseRepository).deleteById(5L);
    }
}
