package cl.duoc.libroDigital.academicService.service.impl;

import cl.duoc.libroDigital.academicService.exception.NotFoundException;
import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.repository.GradeRepository;
import cl.duoc.libroDigital.academicService.service.GradeService;
import cl.duoc.libroDigital.academicService.validation.AcademicEntityValidator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final AcademicEntityValidator validator;

    public GradeServiceImpl(GradeRepository gradeRepository, AcademicEntityValidator validator) {
        this.gradeRepository = gradeRepository;
        this.validator = validator;
    }

    @Override
    public Grade createGrade(Grade grade) {
        validator.validateGradeForSave(grade, null);
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
        return gradeRepository.findById(id).map(existing -> {
            if (grade.getStudentId() != null) existing.setStudentId(grade.getStudentId());
            if (grade.getEvaluationId() != null) existing.setEvaluationId(grade.getEvaluationId());
            if (grade.getScore() != null) existing.setScore(grade.getScore());
            if (grade.getGradeDate() != null) existing.setGradeDate(grade.getGradeDate());
            if (grade.getGradeStatusId() != null) existing.setGradeStatusId(grade.getGradeStatusId());
            if (grade.getTeacherComments() != null) existing.setTeacherComments(grade.getTeacherComments());
            if (grade.getIsAbsent() != null) existing.setIsAbsent(grade.getIsAbsent());
            if (grade.getGradedByTeacherId() != null) existing.setGradedByTeacherId(grade.getGradedByTeacherId());

            validator.validateGradeForSave(existing, id);
            return gradeRepository.save(existing);
        }).orElseThrow(() -> new NotFoundException("Nota no encontrada con id " + id));
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
