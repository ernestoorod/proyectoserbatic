package es.serbatic.ServiciosAplicacion.repository.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioVO, Long> {
    UsuarioVO findByUser(String user);
    boolean existsByUserAndPass(String user, String pass);
}
