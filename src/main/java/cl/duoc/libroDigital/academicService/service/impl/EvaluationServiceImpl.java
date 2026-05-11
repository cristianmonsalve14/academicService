package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.service.EvaluationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Override
    public Evaluation createEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    @Override
    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    @Override
    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Override
    public Evaluation updateEvaluation(Long id, Evaluation evaluation) {
        Optional<Evaluation> existingEvaluation = evaluationRepository.findById(id);
        if (existingEvaluation.isPresent()) {
            Evaluation updatedEvaluation = existingEvaluation.get();
            if (evaluation.getName() != null) {
                updatedEvaluation.setName(evaluation.getName());
            }
            if (evaluation.getDate() != null) {
                updatedEvaluation.setDate(evaluation.getDate());
            }
            if (evaluation.getSubjectId() != null) {
                updatedEvaluation.setSubjectId(evaluation.getSubjectId());
            }
            return evaluationRepository.save(updatedEvaluation);
        }
        return null;
    }

    @Override
    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }
}