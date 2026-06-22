package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.TeacherDTO;
import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.TeacherService;
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
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;

    @InjectMocks
    private TeacherController teacherController;

    @Test
    void createTeacher_requiresAdminAndMapsCatalogIds() {
        TeacherDTO dto = new TeacherDTO();
        dto.setRut("11.111.111-1");
        dto.setFirstName("Juan");
        dto.setLastName("Gómez");
        dto.setContractType("TITULAR");
        dto.setTeacherStatus("ACTIVO");

        Teacher created = new Teacher();
        created.setId(20L);
        created.setContractTypeId((short) 3);
        created.setTeacherStatusId((short) 1);

        when(catalogs.requireId("contract_types", "TITULAR")).thenReturn((short) 3);
        when(catalogs.requireId("teacher_statuses", "ACTIVO")).thenReturn((short) 1);
        when(catalogs.code("contract_types", (short) 3)).thenReturn("TITULAR");
        when(catalogs.code("teacher_statuses", (short) 1)).thenReturn("ACTIVO");
        when(teacherService.createTeacher(any(Teacher.class))).thenReturn(created);

        TeacherDTO response = teacherController.createTeacher(dto);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals("TITULAR", response.getContractType());
        assertEquals("ACTIVO", response.getTeacherStatus());
        verify(access).requireAdmin();

        ArgumentCaptor<Teacher> captor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherService).createTeacher(captor.capture());
        assertEquals((short) 3, captor.getValue().getContractTypeId());
        assertEquals((short) 1, captor.getValue().getTeacherStatusId());
    }

    @Test
    void getAllTeachers_nonAdminReturnsCurrentTeacherOnly() {
        Teacher current = new Teacher();
        current.setId(88L);
        current.setContractTypeId((short) 2);
        current.setTeacherStatusId((short) 1);

        when(access.isAdmin()).thenReturn(false);
        when(access.currentTeacher()).thenReturn(Optional.of(current));
        when(catalogs.code("contract_types", (short) 2)).thenReturn("CONTRATA");
        when(catalogs.code("teacher_statuses", (short) 1)).thenReturn("ACTIVO");

        List<TeacherDTO> response = teacherController.getAllTeachers();

        assertEquals(1, response.size());
        assertEquals(88L, response.getFirst().getId());
    }

    @Test
    void getTeacherById_checksReadAccessBeforeReturningDto() {
        Teacher teacher = new Teacher();
        teacher.setId(35L);
        teacher.setContractTypeId((short) 2);
        teacher.setTeacherStatusId((short) 1);

        when(teacherService.getTeacherById(35L)).thenReturn(Optional.of(teacher));
        when(catalogs.code("contract_types", (short) 2)).thenReturn("CONTRATA");
        when(catalogs.code("teacher_statuses", (short) 1)).thenReturn("ACTIVO");

        TeacherDTO response = teacherController.getTeacherById(35L);

        assertNotNull(response);
        assertEquals(35L, response.getId());
        verify(access).ensureCanReadTeacher(35L);
    }
}
