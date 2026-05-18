package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.model.Grade;

import java.util.List;
import java.util.Optional;

public interface GradeService {
    
    Grade createGrade(Grade grade);
    
    List<Grade> getAllGrades();
    
    Optional<Grade> getGradeById(Long id);
    
    Grade updateGrade(Long id, Grade grade);
    
    void deleteGrade(Long id);
    
    List<Grade> getGradesByStudentId(Long studentId);
    
    List<Grade> getGradesByEvaluationId(Long evaluationId);
    
    List<Grade> getGradesBySubjectId(Long subjectId);
}
