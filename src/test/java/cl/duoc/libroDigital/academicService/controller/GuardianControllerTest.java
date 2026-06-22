package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.GuardianDTO;
import cl.duoc.libroDigital.academicService.dto.StudentDTO;
import cl.duoc.libroDigital.academicService.model.Guardian;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.GuardianService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuardianControllerTest {

    @Mock
    private GuardianService guardianService;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private GuardianController guardianController;

    @Test
    void createGuardian_requiresAdminAndMapsRelationship() {
        GuardianDTO dto = new GuardianDTO();
        dto.setRut("12.345.678-9");
        dto.setFirstName("María");
        dto.setLastName("Pérez");
        dto.setRelationship("MADRE");
        dto.setIsPrimary(true);

        Guardian created = new Guardian();
        created.setId(55L);
        created.setRut("12.345.678-9");
        created.setRelationshipId((short) 1);

        when(catalogs.requireId("relationship_types", "MADRE")).thenReturn((short) 1);
        when(catalogs.code("relationship_types", (short) 1)).thenReturn("MADRE");
        when(guardianService.createGuardian(any(Guardian.class))).thenReturn(created);

        ResponseEntity<GuardianDTO> response = guardianController.createGuardian(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(55L, response.getBody().getId());
        assertEquals("MADRE", response.getBody().getRelationship());
        verify(access).requireAdmin();

        ArgumentCaptor<Guardian> captor = ArgumentCaptor.forClass(Guardian.class);
        verify(guardianService).createGuardian(captor.capture());
        assertEquals((short) 1, captor.getValue().getRelationshipId());
    }

    @Test
    void getCurrentGuardian_whenNoGuardianReturnsNotFound() {
        when(access.currentGuardian()).thenReturn(Optional.empty());

        ResponseEntity<GuardianDTO> response = guardianController.getCurrentGuardian();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getMyStudents_usesCurrentGuardianAndMapsStudentStatus() {
        Student student = new Student();
        student.setId(90L);
        student.setEnrollmentNumber("ENR-2026-0001");
        student.setStudentStatusId((short) 1);

        when(access.requireGuardianId()).thenReturn(400L);
        when(studentRepository.findByGuardianId(400L)).thenReturn(List.of(student));
        when(catalogs.code("student_statuses", (short) 1)).thenReturn("ACTIVO");

        ResponseEntity<List<StudentDTO>> response = guardianController.getMyStudents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(90L, response.getBody().getFirst().getId());
        assertEquals("ACTIVO", response.getBody().getFirst().getStudentStatus());
    }
}
