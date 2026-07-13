package cl.duoc.libroDigital.academicService.dto;

public class CreateConversationRequest {
    /** TG | TS | GS */
    private String conversationType;
    private Long studentId;
    private Long teacherId;
    private Long guardianId;

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getGuardianId() { return guardianId; }
    public void setGuardianId(Long guardianId) { this.guardianId = guardianId; }
}
