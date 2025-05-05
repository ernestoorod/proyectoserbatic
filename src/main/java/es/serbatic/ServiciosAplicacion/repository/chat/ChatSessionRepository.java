
package es.serbatic.ServiciosAplicacion.repository.chat;

import es.serbatic.ServiciosAplicacion.model.chat.ChatSession;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUsuarioOrderByCreatedAtDesc(UsuarioVO usuario);
}
