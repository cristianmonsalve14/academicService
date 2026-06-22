package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.GradeDTO;
import cl.duoc.libroDigital.academicService.model.Evaluation;
import cl.duoc.libroDigital.academicService.model.Grade;
import cl.duoc.libroDigital.academicService.model.Student;
import cl.duoc.libroDigital.academicService.model.Subject;
import cl.duoc.libroDigital.academicService.model.Teacher;
import cl.duoc.libroDigital.academicService.repository.EvaluationRepository;
import cl.duoc.libroDigital.academicService.repository.StudentRepository;
import cl.duoc.libroDigital.academicService.repository.SubjectRepository;
import cl.duoc.libroDigital.academicService.repository.TeacherRepository;
import cl.duoc.libroDigital.academicService.service.CatalogLookupService;
import cl.duoc.libroDigital.academicService.service.GradeService;
import cl.duoc.libroDigital.academicService.security.AcademicAccessService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;
    private final EvaluationRepository evaluationRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final CatalogLookupService catalogs;
    private final AcademicAccessService access;

    public GradeController(
            GradeService gradeService,
            EvaluationRepository evaluationRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            TeacherRepository teacherRepository,
            CatalogLookupService catalogs,
            AcademicAccessService access) {
        this.gradeService = gradeService;
        this.evaluationRepository = evaluationRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.catalogs = catalogs;
        this.access = access;
    }

    private GradeDTO toDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.setId(grade.getId());
        dto.setStudentId(grade.getStudentId());
        dto.setEvaluationId(grade.getEvaluationId());
        evaluationRepository.findById(grade.getEvaluationId()).ifPresent(evaluation -> {
            dto.setSubjectId(evaluation.getSubjectId());
            dto.setEvaluationName(evaluation.getName());
            if (evaluation.getSubjectId() != null) {
                subjectRepository.findById(evaluation.getSubjectId())
                        .map(Subject::getSubjectName)
                        .ifPresent(dto::setSubjectName);
            }
        });
        if (grade.getStudentId() != null) {
            studentRepository.findById(grade.getStudentId())
                    .map(this::formatStudentName)
                    .ifPresent(dto::setStudentName);
        }
        if (grade.getGradedByTeacherId() != null) {
            teacherRepository.findById(grade.getGradedByTeacherId())
                    .map(this::formatTeacherName)
                    .ifPresent(dto::setGradedByTeacherName);
        }
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

    private String formatStudentName(Student student) {
        StringBuilder name = new StringBuilder();
        if (student.getFirstName() != null && !student.getFirstName().isBlank()) {
            name.append(student.getFirstName().trim());
        }
        if (student.getLastName() != null && !student.getLastName().isBlank()) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(student.getLastName().trim());
        }
        return name.length() > 0 ? name.toString() : "Estudiante sin nombre";
    }

    private String formatTeacherName(Teacher teacher) {
        StringBuilder name = new StringBuilder();
        if (teacher.getFirstName() != null && !teacher.getFirstName().isBlank()) {
            name.append(teacher.getFirstName().trim());
        }
        if (teacher.getLastName() != null && !teacher.getLastName().isBlank()) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(teacher.getLastName().trim());
        }
        return name.length() > 0 ? name.toString() : "Docente sin nombre";
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
        if (access.isTeacher()) {
            grade.setGradedByTeacherId(access.requireTeacherId());
        } else {
            grade.setGradedByTeacherId(dto.getGradedByTeacherId());
        }
        return grade;
    }

    @PostMapping
    public ResponseEntity<GradeDTO> createGrade(@RequestBody GradeDTO dto) {
        access.ensureCanManageGradeForEvaluation(dto.getEvaluationId());
        access.ensureCanReadStudent(dto.getStudentId());
        Grade created = gradeService.createGrade(toEntity(dto));
        return ResponseEntity.ok(toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        if (access.isAdmin()) {
            return ResponseEntity.ok(gradeService.getAllGrades().stream().map(this::toDTO).toList());
        }
        if (access.isStudent()) {
            Long studentId = access.requireStudentId();
            return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId).stream().map(this::toDTO).toList());
        }
        if (access.isGuardian()) {
            Long guardianId = access.requireGuardianId();
            List<GradeDTO> grades = gradeService.getAllGrades().stream()
                    .filter(grade -> access.guardianStudentIds(guardianId).contains(grade.getStudentId()))
                    .map(this::toDTO)
                    .toList();
            return ResponseEntity.ok(grades);
        }
        Long teacherId = access.requireTeacherId();
        List<Long> subjectIds = access.teacherSubjectIds(teacherId);
        return ResponseEntity.ok(gradeService.getAllGrades().stream()
                .filter(grade -> {
                    Optional<Evaluation> evaluation = evaluationRepository.findById(grade.getEvaluationId());
                    return evaluation.map(Evaluation::getSubjectId)
                            .map(subjectIds::contains)
                            .orElse(false);
                })
                .map(this::toDTO)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> getGradeById(@PathVariable Long id) {
        Optional<Grade> grade = gradeService.getGradeById(id);
        if (grade.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        access.ensureCanReadGrade(id);
        return ResponseEntity.ok(toDTO(grade.get()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getGradesByStudentId(@PathVariable Long studentId) {
        access.ensureCanReadStudent(studentId);
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId).stream().map(this::toDTO).toList());
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<GradeDTO>> getGradesByEvaluationId(@PathVariable Long evaluationId) {
        access.ensureCanReadEvaluation(evaluationId);
        return ResponseEntity.ok(gradeService.getGradesByEvaluationId(evaluationId).stream().map(this::toDTO).toList());
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradeDTO>> getGradesBySubjectId(@PathVariable Long subjectId) {
        access.ensureCanReadSubject(subjectId);
        return ResponseEntity.ok(gradeService.getGradesBySubjectId(subjectId).stream().map(this::toDTO).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Long id, @RequestBody GradeDTO dto) {
        access.ensureCanManageGrade(id);
        Grade updated = gradeService.updateGrade(id, toEntity(dto));
        if (updated != null) {
            return ResponseEntity.ok(toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        access.ensureCanManageGrade(id);
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
