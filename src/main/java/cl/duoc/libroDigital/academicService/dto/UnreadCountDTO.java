package cl.duoc.libroDigital.academicService.dto;

public class UnreadCountDTO {
    private long unreadCount;

    public UnreadCountDTO() {}

    public UnreadCountDTO(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}
