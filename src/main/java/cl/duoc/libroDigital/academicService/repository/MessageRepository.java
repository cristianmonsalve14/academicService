package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    List<Message> findByConversationIdIn(List<Long> conversationIds);

    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(Long conversationId);

    void deleteByConversationId(Long conversationId);
}
