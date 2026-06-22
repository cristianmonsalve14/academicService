package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Enrollment;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentNumberServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CatalogLookupService catalogs;

    @InjectMocks
    private EnrollmentNumberService enrollmentNumberService;

    @Test
    void assignIfNeeded_doesNothingWhenStudentIdIsNull() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(null);

        enrollmentNumberService.assignIfNeeded(enrollment);

        verify(studentRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(studentRepository, never()).save(org.mockito.ArgumentMatchers.any(Student.class));
    }

    @Test
    void assignIfNeeded_doesNothingWhenStudentNotFound() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(77L);

        when(studentRepository.findById(77L)).thenReturn(Optional.empty());

        enrollmentNumberService.assignIfNeeded(enrollment);

        verify(studentRepository, never()).save(org.mockito.ArgumentMatchers.any(Student.class));
    }

    @Test
    void assignIfNeeded_keepsCurrentNumberWhenYearMatches() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(88L);
        enrollment.setAcademicYearId((short) 2);

        Student student = new Student();
        student.setId(88L);
        student.setEnrollmentNumber("2026-4");

        when(studentRepository.findById(88L)).thenReturn(Optional.of(student));
        when(catalogs.academicYearValue((short) 2)).thenReturn(2026);

        enrollmentNumberService.assignIfNeeded(enrollment);

        assertEquals("2026-4", student.getEnrollmentNumber());
        verify(studentRepository, never()).save(student);
    }

    @Test
    void assignIfNeeded_generatesAndSavesNextNumber() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(99L);
        enrollment.setAcademicYearId((short) 2);

        Student target = new Student();
        target.setId(99L);

        Student s1 = new Student();
        s1.setEnrollmentNumber("2026-1");
        Student s2 = new Student();
        s2.setEnrollmentNumber("2026-3");
        Student s3 = new Student();
        s3.setEnrollmentNumber("texto-invalido");
        Student s4 = new Student();
        s4.setEnrollmentNumber("2025-8");

        when(studentRepository.findById(99L)).thenReturn(Optional.of(target));
        when(catalogs.academicYearValue((short) 2)).thenReturn(2026);
        when(studentRepository.findAll()).thenReturn(List.of(s1, s2, s3, s4));

        enrollmentNumberService.assignIfNeeded(enrollment);

        assertEquals("2026-4", target.getEnrollmentNumber());
        verify(studentRepository).save(target);
    }

    @Test
    void resolveAcademicYear_usesCatalogThenDateThenCurrentYear() {
        Enrollment withCatalogYear = new Enrollment();
        withCatalogYear.setAcademicYearId((short) 2);
        when(catalogs.academicYearValue((short) 2)).thenReturn(2026);
        assertEquals(2026, enrollmentNumberService.resolveAcademicYear(withCatalogYear));

        Enrollment withDateOnly = new Enrollment();
        withDateOnly.setAcademicYearId((short) 3);
        withDateOnly.setEnrollmentDate(LocalDate.of(2025, 3, 10));
        when(catalogs.academicYearValue((short) 3)).thenReturn(null);
        assertEquals(2025, enrollmentNumberService.resolveAcademicYear(withDateOnly));

        Enrollment fallback = new Enrollment();
        fallback.setAcademicYearId((short) 4);
        when(catalogs.academicYearValue((short) 4)).thenReturn(null);
        assertEquals(LocalDate.now().getYear(), enrollmentNumberService.resolveAcademicYear(fallback));
    }

    @Test
    void generateNextForYear_ignoresOtherYearsAndInvalidFormats() {
        Student a = new Student();
        a.setEnrollmentNumber("2024-7");
        Student b = new Student();
        b.setEnrollmentNumber("2026-2");
        Student c = new Student();
        c.setEnrollmentNumber(" 2026-10 ");
        Student d = new Student();
        d.setEnrollmentNumber("sin-formato");
        Student e = new Student();
        e.setEnrollmentNumber(null);

        when(studentRepository.findAll()).thenReturn(List.of(a, b, c, d, e));

        String next = enrollmentNumberService.generateNextForYear(2026);

        assertEquals("2026-11", next);
    }
}
