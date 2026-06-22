package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.CourseDTO;
import cl.duoc.libroDigital.academicService.model.Course;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private CourseService courseService;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;

    @InjectMocks
    private CourseController courseController;

    @Test
    void createCourse_requiresAdminAndMapsCatalogFields() {
        CourseDTO dto = new CourseDTO();
        dto.setName("3 Medio A");
        dto.setGrade("3 MEDIO");
        dto.setAcademicYear("2026-03-01");
        dto.setShift("COMPLETA");
        dto.setCourseStatus("ACTIVO");
        dto.setHeadTeacherId(20L);

        Course created = new Course();
        created.setId(44L);
        created.setName("3 Medio A");
        created.setLevelId((short) 9);
        created.setAcademicYearId((short) 2);
        created.setShiftId((short) 7);
        created.setCourseStatusId((short) 1);

        when(catalogs.educationLevelIdFromLabel("3 MEDIO")).thenReturn((short) 9);
        when(catalogs.academicYearIdFromDate(LocalDate.of(2026, 3, 1))).thenReturn((short) 2);
        when(catalogs.requireId("shifts", "COMPLETA")).thenReturn((short) 7);
        when(catalogs.requireId("course_statuses", "ACTIVO")).thenReturn((short) 1);
        when(courseService.createCourse(any(Course.class))).thenReturn(created);

        when(catalogs.label("education_levels", (short) 9)).thenReturn("3 MEDIO");
        when(catalogs.academicYearStartDate((short) 2)).thenReturn(LocalDate.of(2026, 3, 1));
        when(catalogs.code("shifts", (short) 7)).thenReturn("COMPLETA");
        when(catalogs.educationStage((short) 9)).thenReturn("MEDIA");
        when(catalogs.code("course_statuses", (short) 1)).thenReturn("ACTIVO");

        CourseDTO response = courseController.createCourse(dto);

        assertNotNull(response);
        assertEquals(44L, response.getId());
        assertEquals("COMPLETA", response.getShift());
        verify(access).requireAdmin();

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseService).createCourse(captor.capture());
        assertEquals((short) 9, captor.getValue().getLevelId());
        assertEquals((short) 2, captor.getValue().getAcademicYearId());
    }

    @Test
    void getAllCourses_guardianSeesOnlyAllowedCourses() {
        Course denied = new Course();
        denied.setId(1L);
        denied.setLevelId((short) 9);
        denied.setAcademicYearId((short) 2);
        denied.setShiftId((short) 1);
        denied.setCourseStatusId((short) 1);

        Course allowed = new Course();
        allowed.setId(2L);
        allowed.setLevelId((short) 9);
        allowed.setAcademicYearId((short) 2);
        allowed.setShiftId((short) 1);
        allowed.setCourseStatusId((short) 1);

        when(access.isAdmin()).thenReturn(false);
        when(access.isStudent()).thenReturn(false);
        when(access.isGuardian()).thenReturn(true);
        when(access.requireGuardianId()).thenReturn(100L);
        when(access.guardianCourseIds(100L)).thenReturn(Set.of(2L));
        when(courseService.getAllCourses()).thenReturn(List.of(denied, allowed));

        when(catalogs.label("education_levels", (short) 9)).thenReturn("3 MEDIO");
        when(catalogs.academicYearStartDate((short) 2)).thenReturn(LocalDate.of(2026, 3, 1));
        when(catalogs.code("shifts", (short) 1)).thenReturn("MATUTINO");
        when(catalogs.educationStage((short) 9)).thenReturn("MEDIA");
        when(catalogs.code("course_statuses", (short) 1)).thenReturn("ACTIVO");

        List<CourseDTO> response = courseController.getAllCourses();

        assertEquals(1, response.size());
        assertEquals(2L, response.getFirst().getId());
    }

    @Test
    void getCourse_callsAccessCheckBeforeReturningDto() {
        Course course = new Course();
        course.setId(80L);
        course.setLevelId((short) 9);
        course.setAcademicYearId((short) 2);
        course.setShiftId((short) 1);
        course.setCourseStatusId((short) 1);

        when(courseService.getCourseById(80L)).thenReturn(Optional.of(course));
        when(catalogs.label("education_levels", (short) 9)).thenReturn("3 MEDIO");
        when(catalogs.academicYearStartDate((short) 2)).thenReturn(LocalDate.of(2026, 3, 1));
        when(catalogs.code("shifts", (short) 1)).thenReturn("MATUTINO");
        when(catalogs.educationStage((short) 9)).thenReturn("MEDIA");
        when(catalogs.code("course_statuses", (short) 1)).thenReturn("ACTIVO");

        CourseDTO response = courseController.getCourse(80L);

        assertNotNull(response);
        assertEquals(80L, response.getId());
        verify(access).ensureCanReadCourse(80L);
    }
}
