package cl.duoc.libroDigital.academicService.controller;

import cl.duoc.libroDigital.academicService.dto.AttendanceNotifyRequest;
import cl.duoc.libroDigital.academicService.dto.ConversationDTO;
import cl.duoc.libroDigital.academicService.dto.CreateConversationRequest;
import cl.duoc.libroDigital.academicService.dto.MessageContactDTO;
import cl.duoc.libroDigital.academicService.dto.MessageDTO;
import cl.duoc.libroDigital.academicService.dto.SendMessageRequest;
import cl.duoc.libroDigital.academicService.dto.UnreadCountDTO;
import cl.duoc.libroDigital.academicService.service.MessageService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API de mensajería: conversaciones y mensajes entre oficina, docente, apoderado y alumno.
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public List<ConversationDTO> listConversations() {
        return messageService.listMyConversations();
    }

    @GetMapping("/unread-count")
    public UnreadCountDTO unreadCount() {
        return messageService.countUnreadMessages();
    }

    @PostMapping("/conversations")
    public ConversationDTO openConversation(@RequestBody CreateConversationRequest request) {
        return messageService.openOrGetConversation(request);
    }

    @DeleteMapping("/conversations/{id}")
    public void deleteConversation(@PathVariable("id") Long id) {
        messageService.deleteConversation(id);
    }

    @GetMapping("/conversations/{id}/messages")
    public List<MessageDTO> listMessages(@PathVariable("id") Long id) {
        return messageService.listMessages(id);
    }

    @PostMapping("/conversations/{id}/messages")
    public MessageDTO sendMessage(
            @PathVariable("id") Long id,
            @RequestBody SendMessageRequest request) {
        return messageService.sendMessage(id, request);
    }

    @PostMapping("/attendance-notify")
    public MessageDTO notifyGuardianAttendance(@RequestBody AttendanceNotifyRequest request) {
        return messageService.notifyGuardianAttendance(request);
    }

    @PutMapping("/conversations/{conversationId}/messages/{messageId}")
    public MessageDTO updateMessage(
            @PathVariable("conversationId") Long conversationId,
            @PathVariable("messageId") Long messageId,
            @RequestBody SendMessageRequest request) {
        return messageService.updateMessage(conversationId, messageId, request);
    }

    @DeleteMapping("/conversations/{conversationId}/messages/{messageId}")
    public void deleteMessage(
            @PathVariable("conversationId") Long conversationId,
            @PathVariable("messageId") Long messageId) {
        messageService.deleteMessage(conversationId, messageId);
    }

    @GetMapping("/contacts")
    public List<MessageContactDTO> listContacts() {
        return messageService.listContacts();
    }
}
