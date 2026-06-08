package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.GradeDTO;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.GradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private CatalogLookupService catalogs;

    private GradeDTO toDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.setId(grade.getId());
        dto.setStudentId(grade.getStudentId());
        dto.setEvaluationId(grade.getEvaluationId());
        evaluationRepository.findById(grade.getEvaluationId())
                .map(Evaluation::getSubjectId)
                .ifPresent(dto::setSubjectId);
        dto.setScore(grade.getScore());
        dto.setGradeDate(grade.getGradeDate());
        dto.setGradeStatus(catalogs.code("grade_statuses", grade.getGradeStatusId()));
        dto.setTeacherComments(grade.getTeacherComments());
        dto.setIsAbsent(grade.getIsAbsent());
        dto.setGradedByTeacherId(grade.getGradedByTeacherId());
        dto.setCreatedAt(grade.getCreatedAt());
        dto.setUpdatedAt(grade.getUpdatedAt());
        return dto;
    }

    private Grade toEntity(GradeDTO dto) {
        Grade grade = new Grade();
        grade.setId(dto.getId());
        grade.setStudentId(dto.getStudentId());
        grade.setEvaluationId(dto.getEvaluationId());
        grade.setScore(dto.getScore());
        grade.setGradeDate(dto.getGradeDate());
        grade.setGradeStatusId(catalogs.requireId("grade_statuses", dto.getGradeStatus()));
        grade.setTeacherComments(dto.getTeacherComments());
        grade.setIsAbsent(dto.getIsAbsent());
        grade.setGradedByTeacherId(dto.getGradedByTeacherId());
        return grade;
    }

    @PostMapping
    public ResponseEntity<GradeDTO> createGrade(@RequestBody GradeDTO dto) {
        Grade created = gradeService.createGrade(toEntity(dto));
        return ResponseEntity.ok(toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades().stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> getGradeById(@PathVariable Long id) {
        Optional<Grade> grade = gradeService.getGradeById(id);
        return grade.map(g -> ResponseEntity.ok(toDTO(g)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getGradesByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId).stream().map(this::toDTO).toList());
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<GradeDTO>> getGradesByEvaluationId(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(gradeService.getGradesByEvaluationId(evaluationId).stream().map(this::toDTO).toList());
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradeDTO>> getGradesBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(gradeService.getGradesBySubjectId(subjectId).stream().map(this::toDTO).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Long id, @RequestBody GradeDTO dto) {
        Grade updated = gradeService.updateGrade(id, toEntity(dto));
        if (updated != null) {
            return ResponseEntity.ok(toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
