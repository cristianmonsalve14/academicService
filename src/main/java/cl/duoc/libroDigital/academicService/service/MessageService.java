package cl.duoc.libroDigital.academicService.service;

import cl.duoc.libroDigital.academicService.dto.AttendanceNotifyRequest;
import cl.duoc.libroDigital.academicService.dto.ConversationDTO;
import cl.duoc.libroDigital.academicService.dto.CreateConversationRequest;
import cl.duoc.libroDigital.academicService.dto.MessageContactDTO;
import cl.duoc.libroDigital.academicService.dto.MessageDTO;
import cl.duoc.libroDigital.academicService.dto.SendMessageRequest;
import cl.duoc.libroDigital.academicService.dto.UnreadCountDTO;
import cl.duoc.libroDigital.academicService.model.Evaluation;

import java.util.List;

public interface MessageService {
    List<ConversationDTO> listMyConversations();
    ConversationDTO openOrGetConversation(CreateConversationRequest request);
    List<MessageDTO> listMessages(Long conversationId);
    MessageDTO sendMessage(Long conversationId, SendMessageRequest request);
    MessageDTO updateMessage(Long conversationId, Long messageId, SendMessageRequest request);
    void deleteMessage(Long conversationId, Long messageId);
    void deleteConversation(Long conversationId);
    List<MessageContactDTO> listContacts();
    UnreadCountDTO countUnreadMessages();

    /** Avisa al apoderado del alumno sobre el estado de asistencia de una clase. */
    MessageDTO notifyGuardianAttendance(AttendanceNotifyRequest request);

    /**
     * Avisa a los apoderados de los alumnos del curso de la asignatura
     * cuando el docente publica una nueva evaluación.
     * @return cantidad de avisos enviados
     */
    int notifyGuardiansEvaluationCreated(Evaluation evaluation, String evaluationTypeLabel);
}
