package cl.duoc.libroDigital.academicService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GradeDTO {
    private Long id;
    private Long studentId;
    private Long evaluationId;
    private Long subjectId;
    private BigDecimal score;
    private LocalDate gradeDate;
    private String gradeStatus;
    private String teacherComments;
    private Boolean isAbsent;
    private Long gradedByTeacherId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public LocalDate getGradeDate() { return gradeDate; }
    public void setGradeDate(LocalDate gradeDate) { this.gradeDate = gradeDate; }
    public String getGradeStatus() { return gradeStatus; }
    public void setGradeStatus(String gradeStatus) { this.gradeStatus = gradeStatus; }
    public String getTeacherComments() { return teacherComments; }
    public void setTeacherComments(String teacherComments) { this.teacherComments = teacherComments; }
    public Boolean getIsAbsent() { return isAbsent; }
    public void setIsAbsent(Boolean isAbsent) { this.isAbsent = isAbsent; }
    public Long getGradedByTeacherId() { return gradedByTeacherId; }
    public void setGradedByTeacherId(Long gradedByTeacherId) { this.gradedByTeacherId = gradedByTeacherId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
