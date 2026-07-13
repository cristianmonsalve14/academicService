package cl.duoc.libroDigital.academicService.dto;

import java.time.LocalDateTime;

public class ConversationDTO {
    private Long id;
    private String conversationType;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Long guardianId;
    private String guardianName;
    private String title;
    private String lastMessagePreview;
    private LocalDateTime updatedAt;

    /** Mensajes no leídos para el usuario actual (serializa como unreadCount). */
    public long unreadCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public Long getGuardianId() { return guardianId; }
    public void setGuardianId(Long guardianId) { this.guardianId = guardianId; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
