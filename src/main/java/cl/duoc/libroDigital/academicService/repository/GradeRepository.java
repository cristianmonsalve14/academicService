package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByEvaluationId(Long evaluationId);

    @Query("SELECT g FROM Grade g JOIN Evaluation e ON g.evaluationId = e.id WHERE e.subjectId = :subjectId")
    List<Grade> findBySubjectId(@Param("subjectId") Long subjectId);
}
