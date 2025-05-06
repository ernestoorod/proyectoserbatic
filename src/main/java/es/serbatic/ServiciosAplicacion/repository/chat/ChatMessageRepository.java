package es.serbatic.ServiciosAplicacion.repository.chat;

import es.serbatic.ServiciosAplicacion.model.chat.ChatMessage;
import es.serbatic.ServiciosAplicacion.model.chat.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionOrderByTimestampAsc(ChatSession session);

    /**
     * Borra todos los mensajes de una sesión dada.
     */
    @Modifying
    @Transactional
    void deleteBySession(ChatSession session);

    /**
     * Borra todos los mensajes asociados a un usuario (vía sus sesiones).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMessage m WHERE m.session.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}