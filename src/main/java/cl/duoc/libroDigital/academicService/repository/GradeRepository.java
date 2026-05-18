package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByStudentId(Long studentId);
    
    List<Grade> findByEvaluationId(Long evaluationId);
    
    List<Grade> findBySubjectId(Long subjectId);
}
