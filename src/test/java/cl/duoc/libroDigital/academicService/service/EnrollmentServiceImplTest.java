package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.repository.EnrollmentRepository;
import cl.duoc.libroDigital.academicService.service.impl.EnrollmentServiceImpl;
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
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CatalogLookupService catalogs;

    @Mock
    private EnrollmentNumberService enrollmentNumberService;

    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    void createEnrollment_validatesSavesAndAssignsNumber() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(10L);
        enrollment.setCourseId(3L);
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        Enrollment saved = enrollmentService.createEnrollment(enrollment);

        assertSame(enrollment, saved);
        verify(validator).validateEnrollmentForSave(enrollment, null);
        verify(enrollmentRepository).save(enrollment);
        verify(enrollmentNumberService).assignIfNeeded(enrollment);
    }

    @Test
    void getEnrollmentById_returnsEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(5L);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Optional<Enrollment> result = enrollmentService.getEnrollmentById(1L);

        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getStudentId());
    }

    @Test
    void updateEnrollment_notFoundThrows() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> enrollmentService.updateEnrollment(99L, new Enrollment()));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void deleteEnrollment_deletesById() {
        enrollmentService.deleteEnrollment(6L);
        verify(enrollmentRepository).deleteById(6L);
    }
}
