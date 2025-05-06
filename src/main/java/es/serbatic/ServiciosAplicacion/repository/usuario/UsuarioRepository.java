package es.serbatic.ServiciosAplicacion.repository.usuario;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioVO, Long> {

    UsuarioVO findByUser(String user);
    boolean existsByUserAndPass(String user, String pass);

    /**
     * Borra todas las sesiones de chat asociadas a este usuario.
     */
    @Modifying
    @Transactional
    @Query("""
      DELETE FROM ChatSession c
      WHERE c.usuario.id = :usuarioId
      """)
    void deleteChatSessionsByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Borra el usuario con el id dado.
     */
    @Modifying
    @Transactional
    @Query("""
      DELETE FROM UsuarioVO u
      WHERE u.id = :usuarioId
      """)
    void deleteUsuarioById(@Param("usuarioId") Long usuarioId);
}
