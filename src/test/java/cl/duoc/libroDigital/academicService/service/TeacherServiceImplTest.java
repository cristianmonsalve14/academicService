package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import cl.duoc.libroDigital.academicService.service.impl.TeacherServiceImpl;
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
class TeacherServiceImplTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private CatalogLookupService catalogs;

    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    void createTeacher_validatesAndSaves() {
        Teacher teacher = new Teacher();
        teacher.setRut("22.222.222-2");
        teacher.setFirstName("Juan");
        teacher.setLastName("Soto");
        when(teacherRepository.save(teacher)).thenReturn(teacher);

        Teacher saved = teacherService.createTeacher(teacher);

        assertSame(teacher, saved);
        verify(validator).validateTeacherForSave(teacher, null);
        verify(teacherRepository).save(teacher);
    }

    @Test
    void getTeacherById_returnsTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Elena");
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Optional<Teacher> result = teacherService.getTeacherById(1L);

        assertTrue(result.isPresent());
        assertEquals("Elena", result.get().getFirstName());
    }

    @Test
    void updateTeacher_appliesChangesAndSaves() {
        Teacher existing = new Teacher();
        existing.setId(4L);
        existing.setSpecialization("Matemáticas");

        Teacher update = new Teacher();
        update.setSpecialization("Física");
        update.setEmployeeNumber("DOC-001");

        when(teacherRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(teacherRepository.save(existing)).thenReturn(existing);

        Teacher result = teacherService.updateTeacher(4L, update);

        assertEquals("Física", result.getSpecialization());
        assertEquals("DOC-001", result.getEmployeeNumber());
        verify(validator).validateTeacherForSave(existing, 4L);
        verify(teacherRepository).save(existing);
    }

    @Test
    void updateTeacher_notFoundThrows() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> teacherService.updateTeacher(99L, new Teacher()));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void deleteTeacher_deletesById() {
        teacherService.deleteTeacher(8L);
        verify(teacherRepository).deleteById(8L);
    }

    @Test
    void getTeachersByStatus_usesCatalogLookup() {
        when(catalogs.requireId("teacher_statuses", "ACTIVO")).thenReturn((short) 2);
        when(teacherRepository.findByTeacherStatusId((short) 2)).thenReturn(List.of(new Teacher()));

        List<Teacher> result = teacherService.getTeachersByStatus("ACTIVO");

        assertEquals(1, result.size());
        verify(catalogs).requireId("teacher_statuses", "ACTIVO");
    }
}
