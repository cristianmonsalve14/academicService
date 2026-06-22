package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.StudentDTO;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;

    @InjectMocks
    private StudentController studentController;

    @Test
    void createStudent_requiresAdminAndDelegatesToService() {
        StudentDTO dto = new StudentDTO();
        dto.setRut("12.345.678-5");
        dto.setFirstName("Ana");
        dto.setLastName("Pérez");
        dto.setMotherLastName("Gómez");
        dto.setEmail("ana@mail.cl");
        dto.setStudentStatus("ACTIVO");

        Student created = new Student();
        created.setId(15L);
        created.setRut("12.345.678-5");
        created.setStudentStatusId((short) 1);

        when(catalogs.requireId("student_statuses", "ACTIVO")).thenReturn((short) 1);
        when(studentService.createStudent(any(Student.class))).thenReturn(created);
        when(catalogs.code("student_statuses", (short) 1)).thenReturn("ACTIVO");

        StudentDTO response = studentController.createStudent(dto);

        assertNotNull(response);
        assertEquals(15L, response.getId());
        assertEquals("ACTIVO", response.getStudentStatus());
        verify(access).requireAdmin();

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentService).createStudent(captor.capture());
        assertEquals("12.345.678-5", captor.getValue().getRut());
    }

    @Test
    void getAllStudents_whenStudentRoleReturnsCurrentStudentOnly() {
        Student student = new Student();
        student.setId(22L);
        student.setStudentStatusId((short) 1);

        when(access.isAdmin()).thenReturn(false);
        when(access.isStudent()).thenReturn(true);
        when(access.currentStudent()).thenReturn(Optional.of(student));
        when(catalogs.code("student_statuses", (short) 1)).thenReturn("ACTIVO");

        List<StudentDTO> response = studentController.getAllStudents();

        assertEquals(1, response.size());
        assertEquals(22L, response.getFirst().getId());
        assertEquals("ACTIVO", response.getFirst().getStudentStatus());
    }

    @Test
    void getStudent_callsAccessCheckAndMapsEntity() {
        Student student = new Student();
        student.setId(30L);
        student.setStudentStatusId((short) 1);

        when(studentService.getStudentById(30L)).thenReturn(Optional.of(student));
        when(catalogs.code("student_statuses", (short) 1)).thenReturn("ACTIVO");

        StudentDTO response = studentController.getStudent(30L);

        assertNotNull(response);
        assertEquals(30L, response.getId());
        verify(access).ensureCanReadStudent(30L);
    }
}
