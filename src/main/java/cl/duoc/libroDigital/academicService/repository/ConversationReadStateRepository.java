package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.ConversationReadState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationReadStateRepository extends JpaRepository<ConversationReadState, Long> {

    Optional<ConversationReadState> findByConversationIdAndUserId(Long conversationId, Long userId);

    List<ConversationReadState> findByUserIdAndConversationIdIn(Long userId, List<Long> conversationIds);

    void deleteByConversationId(Long conversationId);
}
