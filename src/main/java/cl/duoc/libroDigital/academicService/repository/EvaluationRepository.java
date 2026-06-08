package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT e FROM Evaluation e JOIN Subject s ON e.subjectId = s.id WHERE s.courseId = :courseId")
    List<Evaluation> findByCourseId(@Param("courseId") Long courseId);

    List<Evaluation> findBySubjectId(Long subjectId);

    List<Evaluation> findByEvaluationStatusId(Short evaluationStatusId);
}
