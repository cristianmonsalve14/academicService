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

    // Calificación (escala chilena: 1.0 a 7.0, aprobación: 4.0)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;  // 6.5 (escala 1.0 a 7.0)

    @Column(nullable = false)
    private LocalDate gradeDate;

    @Column(name = "grade_status_id", nullable = false)
    private Short gradeStatusId = 1;

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
        if (gradeStatusId == null) gradeStatusId = 1;
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

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public LocalDate getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(LocalDate gradeDate) {
        this.gradeDate = gradeDate;
    }

    public Short getGradeStatusId() { return gradeStatusId; }
    public void setGradeStatusId(Short gradeStatusId) { this.gradeStatusId = gradeStatusId; }

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
