package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciones
    @Column(nullable = false)
    private Long studentId;  // FK a students

    @Column(nullable = false)
    private Long evaluationId;  // FK a evaluations

    @Column(nullable = false)
    private Long subjectId;  // FK a subjects

    // Calificación (escala chilena: 1.0 a 7.0, aprobación: 4.0)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;  // 6.5 (escala 1.0 a 7.0)

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;  // 85.5% (score/maxScore * 100)

    private String letterGrade;  // "MB" (Muy Bueno 6.0-7.0), "B" (Bueno 5.0-5.9), "S" (Suficiente 4.0-4.9), "I" (Insuficiente 1.0-3.9)

    @Column(nullable = false)
    private LocalDate gradeDate;

    @Column(nullable = false)
    private String gradeStatus;  // "DEFINITIVA", "PRELIMINAR", "EN_REVISION", "AUSENTE"

    @Column(length = 1000)
    private String teacherComments;

    private Boolean isAbsent;  // alumno ausente en la evaluación

    private Long gradedByTeacherId;  // FK a teachers (quién calificó)

    // Auditoría
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (gradeStatus == null) {
            gradeStatus = "DEFINITIVA";
        }
        if (isAbsent == null) {
            isAbsent = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ✅ Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public LocalDate getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(LocalDate gradeDate) {
        this.gradeDate = gradeDate;
    }

    public String getGradeStatus() {
        return gradeStatus;
    }

    public void setGradeStatus(String gradeStatus) {
        this.gradeStatus = gradeStatus;
    }

    public String getTeacherComments() {
        return teacherComments;
    }

    public void setTeacherComments(String teacherComments) {
        this.teacherComments = teacherComments;
    }

    public Boolean getIsAbsent() {
        return isAbsent;
    }

    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    public Long getGradedByTeacherId() {
        return gradedByTeacherId;
    }

    public void setGradedByTeacherId(Long gradedByTeacherId) {
        this.gradedByTeacherId = gradedByTeacherId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
