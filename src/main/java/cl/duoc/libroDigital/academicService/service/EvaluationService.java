package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Evaluation;

import java.util.List;

public interface EvaluationService {

    Evaluation createEvaluation(Evaluation evaluation);

    List<Evaluation> getAllEvaluations();
}