package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByCourseId(Long courseId);

    List<Evaluation> findBySubjectId(Long subjectId);

    List<Evaluation> findByEvaluationStatus(String evaluationStatus);
}