package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.EnrollmentDTO;
import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @Test
    void createEnrollment_requiresAdminAndMapsCatalogFields() {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setStudentId(11L);
        dto.setCourseId(22L);
        dto.setAcademicYear(2026);
        dto.setEnrollmentStatus("ACTIVA");
        dto.setEnrollmentDate(LocalDate.of(2026, 3, 10));

        Enrollment created = new Enrollment();
        created.setId(77L);
        created.setStudentId(11L);
        created.setCourseId(22L);
        created.setAcademicYearId((short) 2);
        created.setEnrollmentStatusId((short) 1);
        created.setEnrollmentDate(LocalDate.of(2026, 3, 10));

        when(catalogs.academicYearIdFromYear(2026)).thenReturn((short) 2);
        when(catalogs.requireId("enrollment_statuses", "ACTIVA")).thenReturn((short) 1);
        when(enrollmentService.createEnrollment(any(Enrollment.class))).thenReturn(created);
        when(catalogs.academicYearValue((short) 2)).thenReturn(2026);
        when(catalogs.code("enrollment_statuses", (short) 1)).thenReturn("ACTIVA");

        EnrollmentDTO response = enrollmentController.createEnrollment(dto);

        assertNotNull(response);
        assertEquals(77L, response.getId());
        assertEquals(2026, response.getAcademicYear());
        assertEquals("ACTIVA", response.getEnrollmentStatus());
        verify(access).requireAdmin();

        ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentService).createEnrollment(captor.capture());
        assertEquals((short) 2, captor.getValue().getAcademicYearId());
        assertEquals((short) 1, captor.getValue().getEnrollmentStatusId());
    }

    @Test
    void getAllEnrollments_guardianRoleFiltersByStudents() {
        Enrollment allowed = new Enrollment();
        allowed.setId(1L);
        allowed.setStudentId(11L);
        allowed.setAcademicYearId((short) 2);
        allowed.setEnrollmentStatusId((short) 1);

        Enrollment denied = new Enrollment();
        denied.setId(2L);
        denied.setStudentId(99L);
        denied.setAcademicYearId((short) 2);
        denied.setEnrollmentStatusId((short) 1);

        Student student = new Student();
        student.setId(11L);
        student.setEnrollmentNumber("ENR-11");

        when(access.isAdmin()).thenReturn(false);
        when(access.isStudent()).thenReturn(false);
        when(access.isGuardian()).thenReturn(true);
        when(access.requireGuardianId()).thenReturn(500L);
        when(access.guardianStudentIds(500L)).thenReturn(List.of(11L));
        when(enrollmentService.getAllEnrollments()).thenReturn(List.of(allowed, denied));
        when(studentRepository.findById(11L)).thenReturn(Optional.of(student));
        when(catalogs.academicYearValue((short) 2)).thenReturn(2026);
        when(catalogs.code("enrollment_statuses", (short) 1)).thenReturn("ACTIVA");

        List<EnrollmentDTO> response = enrollmentController.getAllEnrollments();

        assertEquals(1, response.size());
        assertEquals(1L, response.getFirst().getId());
        assertEquals("ENR-11", response.getFirst().getEnrollmentNumber());
    }

    @Test
    void getEnrollment_returnsNullWhenNotFound() {
        when(enrollmentService.getEnrollmentById(404L)).thenReturn(Optional.empty());

        EnrollmentDTO response = enrollmentController.getEnrollment(404L);

        assertNull(response);
    }
}
