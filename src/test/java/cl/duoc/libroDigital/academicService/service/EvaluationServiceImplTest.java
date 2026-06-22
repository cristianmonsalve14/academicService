package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.service.impl.EvaluationServiceImpl;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceImplTest {

    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private CatalogLookupService catalogs;
    @Mock
    private AcademicEntityValidator validator;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    @Test
    void createEvaluation_validatesAndSaves() {
        Evaluation evaluation = new Evaluation();
        evaluation.setName("Prueba parcial");
        evaluation.setDate(LocalDate.of(2026, 6, 20));
        when(evaluationRepository.save(evaluation)).thenReturn(evaluation);

        Evaluation saved = evaluationService.createEvaluation(evaluation);

        assertSame(evaluation, saved);
        verify(validator).validateEvaluationForSave(evaluation, null);
        verify(evaluationRepository).save(evaluation);
    }

    @Test
    void updateEvaluation_appliesChangesValidatesAndSaves() {
        Evaluation existing = new Evaluation();
        existing.setId(10L);
        existing.setName("Prueba 1");
        existing.setDescription("Original");

        Evaluation update = new Evaluation();
        update.setName("Prueba 1 - corregida");
        update.setDescription("Actualizada");

        when(evaluationRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(evaluationRepository.save(existing)).thenReturn(existing);

        Evaluation result = evaluationService.updateEvaluation(10L, update);

        assertEquals("Prueba 1 - corregida", result.getName());
        assertEquals("Actualizada", result.getDescription());
        verify(validator).validateEvaluationForSave(existing, 10L);
        verify(evaluationRepository).save(existing);
    }

    @Test
    void updateEvaluation_notFoundThrows() {
        when(evaluationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> evaluationService.updateEvaluation(99L, new Evaluation()));
    }

    @Test
    void getEvaluationsByStatus_usesCatalogLookup() {
        Evaluation evaluation = new Evaluation();
        when(catalogs.requireId("evaluation_statuses", "ACTIVA")).thenReturn((short) 2);
        when(evaluationRepository.findByEvaluationStatusId((short) 2)).thenReturn(List.of(evaluation));

        List<Evaluation> result = evaluationService.getEvaluationsByStatus("ACTIVA");

        assertEquals(1, result.size());
        verify(catalogs).requireId("evaluation_statuses", "ACTIVA");
    }
}
