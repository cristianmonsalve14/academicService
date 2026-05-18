package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.model.Grade;
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

    @PostMapping
    public ResponseEntity<Grade> createGrade(@RequestBody Grade grade) {
        Grade created = gradeService.createGrade(grade);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
        Optional<Grade> grade = gradeService.getGradeById(id);
        return grade.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId) {
        List<Grade> grades = gradeService.getGradesByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<Grade>> getGradesByEvaluationId(@PathVariable Long evaluationId) {
        List<Grade> grades = gradeService.getGradesByEvaluationId(evaluationId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Grade>> getGradesBySubjectId(@PathVariable Long subjectId) {
        List<Grade> grades = gradeService.getGradesBySubjectId(subjectId);
        return ResponseEntity.ok(grades);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(@PathVariable Long id, @RequestBody Grade grade) {
        Grade updated = gradeService.updateGrade(id, grade);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
