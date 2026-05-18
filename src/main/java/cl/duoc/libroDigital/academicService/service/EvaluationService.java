package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationService {

    // Crear
    Evaluation createEvaluation(Evaluation evaluation);

    // Listar
    List<Evaluation> getAllEvaluations();

    // Buscar por ID
    Optional<Evaluation> getEvaluationById(Long id);

    // Actualizar
    Evaluation updateEvaluation(Long id, Evaluation evaluation);

    // Eliminar
    void deleteEvaluation(Long id);

    // ===== MÉTODOS PRO =====

    List<Evaluation> getEvaluationsByCourse(Long courseId);

    List<Evaluation> getEvaluationsBySubject(Long subjectId);

    List<Evaluation> getEvaluationsByStatus(String evaluationStatus);
}
