package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELACIONES =====
    @Column(name = "subject_id")
    private Long subjectId;

    // ===== DATOS DE LA EVALUACIÓN =====
    private String name;

    private LocalDate date;

    @Column(name = "evaluation_type_id", nullable = false)
    private Short evaluationTypeId = 1;

    @Column(name = "evaluation_status_id", nullable = false)
    private Short evaluationStatusId = 1;

    @Column(name = "max_score")
    private Double maxScore;

    private Double weight;

    private String description;

    // ===== AUDITORÍA =====
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== LIFECYCLE =====
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (evaluationStatusId == null) evaluationStatusId = 1;
        if (evaluationTypeId == null) evaluationTypeId = 1;

        if (date == null) {
            date = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Short getEvaluationTypeId() { return evaluationTypeId; }
    public void setEvaluationTypeId(Short evaluationTypeId) { this.evaluationTypeId = evaluationTypeId; }
    public Short getEvaluationStatusId() { return evaluationStatusId; }
    public void setEvaluationStatusId(Short evaluationStatusId) { this.evaluationStatusId = evaluationStatusId; }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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