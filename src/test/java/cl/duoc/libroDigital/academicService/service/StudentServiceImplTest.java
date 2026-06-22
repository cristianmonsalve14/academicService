package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.service.impl.StudentServiceImpl;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CatalogLookupService catalogs;

    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void createStudent_clearsEnrollmentNumberValidatesAndSaves() {
        Student student = new Student();
        student.setEnrollmentNumber("2026-99");
        student.setFirstName("Ana");
        student.setLastName("Pérez");
        when(studentRepository.save(student)).thenReturn(student);

        Student saved = studentService.createStudent(student);

        assertNull(saved.getEnrollmentNumber());
        verify(validator).validateStudentForSave(student, null);
        verify(studentRepository).save(student);
    }

    @Test
    void getStudentById_returnsStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Luis");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Optional<Student> result = studentService.getStudentById(1L);

        assertTrue(result.isPresent());
        assertEquals("Luis", result.get().getFirstName());
    }

    @Test
    void updateStudent_appliesChangesAndSaves() {
        Student existing = new Student();
        existing.setId(2L);
        existing.setFirstName("Carlos");

        Student update = new Student();
        update.setFirstName("Carlos Updated");
        update.setEmail("carlos@example.com");

        when(studentRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        Student result = studentService.updateStudent(2L, update);

        assertEquals("Carlos Updated", result.getFirstName());
        assertEquals("carlos@example.com", result.getEmail());
        verify(validator).validateStudentForSave(existing, 2L);
        verify(studentRepository).save(existing);
    }

    @Test
    void updateStudent_notFoundThrows() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> studentService.updateStudent(99L, new Student()));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void deleteStudent_deletesById() {
        studentService.deleteStudent(5L);
        verify(studentRepository).deleteById(5L);
    }

    @Test
    void getStudentsByStatus_usesCatalogLookup() {
        when(catalogs.requireId("student_statuses", "ACTIVO")).thenReturn((short) 3);
        when(studentRepository.findByStudentStatusId((short) 3)).thenReturn(List.of(new Student()));

        List<Student> result = studentService.getStudentsByStatus("ACTIVO");

        assertEquals(1, result.size());
        verify(catalogs).requireId("student_statuses", "ACTIVO");
    }
}
