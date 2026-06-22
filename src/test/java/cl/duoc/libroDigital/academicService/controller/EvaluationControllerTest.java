package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.EvaluationDTO;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.EvaluationService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationControllerTest {

    @Mock
    private EvaluationService evaluationService;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicAccessService access;

    @InjectMocks
    private EvaluationController evaluationController;

    @Test
    void createEvaluation_checksAccessAndDelegatesToService() {
        EvaluationDTO dto = new EvaluationDTO();
        dto.setSubjectId(10L);
        dto.setName("Prueba 1");
        dto.setDate(LocalDate.of(2026, 6, 10));
        dto.setEvaluationType("PRUEBA");
        dto.setEvaluationStatus("ACTIVA");

        Evaluation created = new Evaluation();
        created.setId(70L);
        created.setSubjectId(10L);
        created.setName("Prueba 1");
        created.setDate(LocalDate.of(2026, 6, 10));
        created.setEvaluationTypeId((short) 3);
        created.setEvaluationStatusId((short) 1);

        Subject subject = new Subject();
        subject.setId(10L);
        subject.setCourseId(40L);

        when(catalogs.requireId("evaluation_types", "PRUEBA")).thenReturn((short) 3);
        when(catalogs.requireId("evaluation_statuses", "ACTIVA")).thenReturn((short) 1);
        when(evaluationService.createEvaluation(any(Evaluation.class))).thenReturn(created);
        when(subjectRepository.findById(10L)).thenReturn(Optional.of(subject));
        when(catalogs.code("evaluation_types", (short) 3)).thenReturn("PRUEBA");
        when(catalogs.code("evaluation_statuses", (short) 1)).thenReturn("ACTIVA");

        EvaluationDTO response = evaluationController.createEvaluation(dto);

        assertNotNull(response);
        assertEquals(70L, response.getId());
        assertEquals(40L, response.getCourseId());
        verify(access).ensureCanManageEvaluationSubject(10L);

        ArgumentCaptor<Evaluation> captor = ArgumentCaptor.forClass(Evaluation.class);
        verify(evaluationService).createEvaluation(captor.capture());
        assertEquals((short) 3, captor.getValue().getEvaluationTypeId());
        assertEquals((short) 1, captor.getValue().getEvaluationStatusId());
    }

    @Test
    void getAllEvaluations_teacherSeesOnlyOwnSubjects() {
        Evaluation allowed = new Evaluation();
        allowed.setId(1L);
        allowed.setSubjectId(10L);
        allowed.setEvaluationTypeId((short) 1);
        allowed.setEvaluationStatusId((short) 1);

        Evaluation denied = new Evaluation();
        denied.setId(2L);
        denied.setSubjectId(20L);
        denied.setEvaluationTypeId((short) 1);
        denied.setEvaluationStatusId((short) 1);

        when(access.isAdmin()).thenReturn(false);
        when(access.requireTeacherId()).thenReturn(500L);
        when(access.teacherSubjectIds(500L)).thenReturn(List.of(10L));
        when(evaluationService.getAllEvaluations()).thenReturn(List.of(allowed, denied));
        when(catalogs.code("evaluation_types", (short) 1)).thenReturn("PRUEBA");
        when(catalogs.code("evaluation_statuses", (short) 1)).thenReturn("ACTIVA");

        List<EvaluationDTO> response = evaluationController.getAllEvaluations();

        assertEquals(1, response.size());
        assertEquals(1L, response.getFirst().getId());
    }

    @Test
    void getEvaluation_callsReadAccessForSubject() {
        Evaluation evaluation = new Evaluation();
        evaluation.setId(9L);
        evaluation.setSubjectId(10L);
        evaluation.setEvaluationTypeId((short) 1);
        evaluation.setEvaluationStatusId((short) 1);

        when(evaluationService.getEvaluationById(9L)).thenReturn(Optional.of(evaluation));
        when(catalogs.code("evaluation_types", (short) 1)).thenReturn("PRUEBA");
        when(catalogs.code("evaluation_statuses", (short) 1)).thenReturn("ACTIVA");

        EvaluationDTO response = evaluationController.getEvaluation(9L);

        assertNotNull(response);
        assertEquals(9L, response.getId());
        verify(access).ensureCanReadSubject(10L);
    }
}
