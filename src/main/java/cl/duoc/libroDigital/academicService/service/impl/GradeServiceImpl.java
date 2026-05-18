package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.repository.GradeRepository;
import cl.duoc.libroDigital.academicService.service.GradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Override
    public Grade createGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @Override
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    @Override
    public Grade updateGrade(Long id, Grade grade) {
        Optional<Grade> existingGrade = gradeRepository.findById(id);
        if (existingGrade.isPresent()) {
            Grade updatedGrade = existingGrade.get();
            
            if (grade.getStudentId() != null) {
                updatedGrade.setStudentId(grade.getStudentId());
            }
            if (grade.getEvaluationId() != null) {
                updatedGrade.setEvaluationId(grade.getEvaluationId());
            }
            if (grade.getSubjectId() != null) {
                updatedGrade.setSubjectId(grade.getSubjectId());
            }
            if (grade.getScore() != null) {
                updatedGrade.setScore(grade.getScore());
            }
            if (grade.getPercentage() != null) {
                updatedGrade.setPercentage(grade.getPercentage());
            }
            if (grade.getLetterGrade() != null) {
                updatedGrade.setLetterGrade(grade.getLetterGrade());
            }
            if (grade.getGradeDate() != null) {
                updatedGrade.setGradeDate(grade.getGradeDate());
            }
            if (grade.getGradeStatus() != null) {
                updatedGrade.setGradeStatus(grade.getGradeStatus());
            }
            if (grade.getTeacherComments() != null) {
                updatedGrade.setTeacherComments(grade.getTeacherComments());
            }
            if (grade.getIsAbsent() != null) {
                updatedGrade.setIsAbsent(grade.getIsAbsent());
            }
            if (grade.getGradedByTeacherId() != null) {
                updatedGrade.setGradedByTeacherId(grade.getGradedByTeacherId());
            }
            
            return gradeRepository.save(updatedGrade);
        }
        return null;
    }

    @Override
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    @Override
    public List<Grade> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Override
    public List<Grade> getGradesByEvaluationId(Long evaluationId) {
        return gradeRepository.findByEvaluationId(evaluationId);
    }

    @Override
    public List<Grade> getGradesBySubjectId(Long subjectId) {
        return gradeRepository.findBySubjectId(subjectId);
    }
}
