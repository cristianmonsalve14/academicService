package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.EvaluationService;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final CatalogLookupService catalogs;
    private final AcademicEntityValidator validator;

    public EvaluationServiceImpl(
            EvaluationRepository evaluationRepository,
            CatalogLookupService catalogs,
            AcademicEntityValidator validator) {
        this.evaluationRepository = evaluationRepository;
        this.catalogs = catalogs;
        this.validator = validator;
    }

    @Override
    public Evaluation createEvaluation(Evaluation evaluation) {
        validator.validateEvaluationForSave(evaluation, null);
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
        return evaluationRepository.findById(id).map(existing -> {
            if (evaluation.getName() != null) existing.setName(evaluation.getName());
            if (evaluation.getDate() != null) existing.setDate(evaluation.getDate());
            if (evaluation.getSubjectId() != null) existing.setSubjectId(evaluation.getSubjectId());
            if (evaluation.getEvaluationTypeId() != null) existing.setEvaluationTypeId(evaluation.getEvaluationTypeId());
            if (evaluation.getEvaluationStatusId() != null) existing.setEvaluationStatusId(evaluation.getEvaluationStatusId());
            if (evaluation.getMaxScore() != null) existing.setMaxScore(evaluation.getMaxScore());
            if (evaluation.getWeight() != null) existing.setWeight(evaluation.getWeight());
            if (evaluation.getDescription() != null) existing.setDescription(evaluation.getDescription());

            validator.validateEvaluationForSave(existing, id);
            return evaluationRepository.save(existing);
        }).orElseThrow(() -> new NotFoundException("Evaluación no encontrada con id " + id));
    }

    @Override
    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }

    @Override
    public List<Evaluation> getEvaluationsByCourse(Long courseId) {
        return evaluationRepository.findByCourseId(courseId);
    }

    @Override
    public List<Evaluation> getEvaluationsBySubject(Long subjectId) {
        return evaluationRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Evaluation> getEvaluationsByStatus(String evaluationStatus) {
        Short statusId = catalogs.requireId("evaluation_statuses", evaluationStatus);
        return evaluationRepository.findByEvaluationStatusId(statusId);
    }
}
