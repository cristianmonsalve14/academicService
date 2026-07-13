package cl.duoc.libroDigital.academicService.dto;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long id;
    private Long conversationId;
    private String senderRole;
    private Long senderUserId;
    private Long senderProfileId;
    private String senderName;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean mine;
    private boolean edited;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public Long getSenderProfileId() { return senderProfileId; }
    public void setSenderProfileId(Long senderProfileId) { this.senderProfileId = senderProfileId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isMine() { return mine; }
    public void setMine(boolean mine) { this.mine = mine; }
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
}
