
package es.serbatic.ServiciosAplicacion.repository.chat;

import es.serbatic.ServiciosAplicacion.model.chat.ChatMessage;
import es.serbatic.ServiciosAplicacion.model.chat.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionOrderByTimestampAsc(ChatSession session);
    void deleteBySession(ChatSession session);

}
