package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.SubjectDTO;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.SubjectService;
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
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;

    @InjectMocks
    private SubjectController subjectController;

    @Test
    void createSubject_requiresAdminAndMapsSubjectType() {
        SubjectDTO dto = new SubjectDTO();
        dto.setSubjectName("Matemática");
        dto.setSubjectType("TRONCAL");
        dto.setTeacherId(7L);
        dto.setCourseId(3L);

        Subject created = new Subject();
        created.setId(10L);
        created.setSubjectTypeId((short) 2);

        when(catalogs.requireId("subject_types", "TRONCAL")).thenReturn((short) 2);
        when(catalogs.code("subject_types", (short) 2)).thenReturn("TRONCAL");
        when(subjectService.createSubject(any(Subject.class))).thenReturn(created);

        SubjectDTO response = subjectController.createSubject(dto);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("TRONCAL", response.getSubjectType());
        verify(access).requireAdmin();

        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectService).createSubject(captor.capture());
        assertEquals((short) 2, captor.getValue().getSubjectTypeId());
    }

    @Test
    void getAllSubjects_nonAdminReadsOnlyTeacherSubjects() {
        Subject subject = new Subject();
        subject.setId(11L);
        subject.setTeacherId(50L);
        subject.setSubjectTypeId((short) 1);

        when(access.isAdmin()).thenReturn(false);
        when(access.requireTeacherId()).thenReturn(50L);
        when(subjectService.getSubjectsByTeacher(50L)).thenReturn(List.of(subject));
        when(catalogs.code("subject_types", (short) 1)).thenReturn("ELECTIVO");

        List<SubjectDTO> response = subjectController.getAllSubjects();

        assertEquals(1, response.size());
        assertEquals(11L, response.getFirst().getId());
    }

    @Test
    void getSubject_checksAccessAndMapsDto() {
        Subject subject = new Subject();
        subject.setId(4L);
        subject.setSubjectTypeId((short) 1);

        when(subjectService.getSubjectById(4L)).thenReturn(Optional.of(subject));
        when(catalogs.code("subject_types", (short) 1)).thenReturn("ELECTIVO");

        SubjectDTO response = subjectController.getSubject(4L);

        assertNotNull(response);
        assertEquals(4L, response.getId());
        verify(access).ensureCanReadSubject(4L);
    }
}
