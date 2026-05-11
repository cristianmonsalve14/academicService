package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationService {

    Evaluation createEvaluation(Evaluation evaluation);

    List<Evaluation> getAllEvaluations();

    Optional<Evaluation> getEvaluationById(Long id);

    Evaluation updateEvaluation(Long id, Evaluation evaluation);

    void deleteEvaluation(Long id);
}