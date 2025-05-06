package es.serbatic.ServiciosAplicacion.repository.chat;

import es.serbatic.ServiciosAplicacion.model.chat.ChatSession;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByUsuarioOrderByCreatedAtDesc(UsuarioVO usuario);

    /**
     * Borra todas las sesiones de chat de un usuario por su ID.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatSession c WHERE c.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Alternativamente, si ya tienes la entidad UsuarioVO:
     */
    @Modifying
    @Transactional
    void deleteByUsuario(UsuarioVO usuario);
}
