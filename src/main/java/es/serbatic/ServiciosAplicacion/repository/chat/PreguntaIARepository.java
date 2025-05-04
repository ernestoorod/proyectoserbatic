package es.serbatic.ServiciosAplicacion.repository.chat;

import es.serbatic.ServiciosAplicacion.model.chat.PreguntaIA;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreguntaIARepository extends JpaRepository<PreguntaIA, Long> {

    Optional<PreguntaIA> findTopByTextoPregunta(String textoPregunta);

    List<PreguntaIA> findByUsuarioOrderByFechaDesc(UsuarioVO usuario);

    List<PreguntaIA> findTop5ByUsuarioOrderByFechaDesc(UsuarioVO usuario);

    void deleteByUsuario(UsuarioVO usuario);
}
