package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "courses")
public class Course {
    @Column(nullable = false)
    private String name; // Nombre del curso

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Información del curso
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Column(nullable = false)
    private String grade;  // "1° Básico", "7° Básico", "1° Medio", "4° Medio"


    @Column(nullable = false)
    private LocalDate academicYear;  // Fecha de inicio del curso

    private String shift;  // "MAÑANA", "TARDE", "VESPERTINO"

    // Profesor jefe
    private Long headTeacherId;  // FK a teachers

    // Capacidad
    private Integer maxCapacity;  // 40


    // Ubicación
    private String classroom;  // "Sala 201"

    private String level;  // "BASICA", "MEDIA"

    @Column(nullable = false)
    private String courseStatus;  // "ACTIVO", "CERRADO"

    // Auditoría
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (courseStatus == null) {
            courseStatus = "ACTIVO";
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    public LocalDate getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(LocalDate academicYear) {
        this.academicYear = academicYear;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Long getHeadTeacherId() {
        return headTeacherId;
    }

    public void setHeadTeacherId(Long headTeacherId) {
        this.headTeacherId = headTeacherId;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }


    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
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
