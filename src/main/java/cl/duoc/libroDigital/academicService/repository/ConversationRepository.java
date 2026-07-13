package cl.duoc.libroDigital.academicService.repository;

import cl.duoc.libroDigital.academicService.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByThreadKey(String threadKey);

    List<Conversation> findByTeacherIdOrderByUpdatedAtDesc(Long teacherId);

    List<Conversation> findByGuardianIdOrderByUpdatedAtDesc(Long guardianId);

    List<Conversation> findByStudentIdOrderByUpdatedAtDesc(Long studentId);

    List<Conversation> findByConversationTypeInOrderByUpdatedAtDesc(Collection<String> types);
}
