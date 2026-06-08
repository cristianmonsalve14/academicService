package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "head_teacher_id")
    private Long headTeacherId;

    private Integer maxCapacity;
    private String classroom;

    @Column(name = "course_status_id", nullable = false)
    private Short courseStatusId = 1;

    @Column(name = "shift_id")
    private Short shiftId;

    @Column(name = "level_id")
    private Short levelId = 9;

    @Column(name = "academic_year_id", nullable = false)
    private Short academicYearId = 2;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (courseStatusId == null) courseStatusId = 1;
        if (academicYearId == null) academicYearId = 2;
        if (levelId == null) levelId = 9;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getHeadTeacherId() { return headTeacherId; }
    public void setHeadTeacherId(Long headTeacherId) { this.headTeacherId = headTeacherId; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
    public Short getCourseStatusId() { return courseStatusId; }
    public void setCourseStatusId(Short courseStatusId) { this.courseStatusId = courseStatusId; }
    public Short getShiftId() { return shiftId; }
    public void setShiftId(Short shiftId) { this.shiftId = shiftId; }
    public Short getLevelId() { return levelId; }
    public void setLevelId(Short levelId) { this.levelId = levelId; }
    public Short getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(Short academicYearId) { this.academicYearId = academicYearId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
