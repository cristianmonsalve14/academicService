package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELACIONES =====
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "course_id")
    private Long courseId;

    // ===== DATOS MATRÍCULA =====
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "academic_year_id", nullable = false)
    private Short academicYearId = 2;

    @Column(name = "enrollment_status_id", nullable = false)
    private Short enrollmentStatusId = 1;

    @Column(name = "is_regular")
    private Boolean isRegular;

    private String observations;

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

        if (enrollmentStatusId == null) enrollmentStatusId = 1;
        if (enrollmentDate == null) enrollmentDate = LocalDate.now();
        if (academicYearId == null) academicYearId = 2;
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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Short getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Short academicYearId) { this.academicYearId = academicYearId; }
    public Short getEnrollmentStatusId() { return enrollmentStatusId; }
    public void setEnrollmentStatusId(Short enrollmentStatusId) { this.enrollmentStatusId = enrollmentStatusId; }

    public Boolean getIsRegular() {
        return isRegular;
    }

    public void setIsRegular(Boolean isRegular) {
        this.isRegular = isRegular;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
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