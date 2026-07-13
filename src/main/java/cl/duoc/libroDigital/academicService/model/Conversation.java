package cl.duoc.libroDigital.academicService.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * AD = administrativo-docente
     * AG = administrativo-apoderado
     * AS = administrativo-alumno
     * TG = docente-apoderado
     * TS = docente-alumno
     */
    @Column(name = "conversation_type", nullable = false, length = 8)
    private String conversationType;

    @Column(name = "thread_key", nullable = false, unique = true, length = 80)
    private String threadKey;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "guardian_id")
    private Long guardianId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public String getThreadKey() { return threadKey; }
    public void setThreadKey(String threadKey) { this.threadKey = threadKey; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getGuardianId() { return guardianId; }
    public void setGuardianId(Long guardianId) { this.guardianId = guardianId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
