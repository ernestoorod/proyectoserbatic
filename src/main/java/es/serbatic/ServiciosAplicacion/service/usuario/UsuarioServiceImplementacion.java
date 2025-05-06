package es.serbatic.ServiciosAplicacion.service.usuario;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.repository.chat.ChatMessageRepository;
import es.serbatic.ServiciosAplicacion.repository.chat.ChatSessionRepository;
import es.serbatic.ServiciosAplicacion.repository.reserva.ReservaRepository;
import es.serbatic.ServiciosAplicacion.repository.usuario.UsuarioRepository;

@Service
public class UsuarioServiceImplementacion implements UsuarioService {

    @Autowired
    private UsuarioRepository usuariorepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Override
    public void actualizar(UsuarioVO usuario) {
        usuariorepository.save(usuario);
    }

    @Override
    public void insertar(UsuarioVO usuario) {
        String passwordEncriptado = BCrypt.hashpw(usuario.getPass(), BCrypt.gensalt());
        usuario.setPass(passwordEncriptado);
        usuariorepository.save(usuario);
    }

    @Override
    public UsuarioVO getByID(Long id) {
        return usuariorepository.findById(id).orElse(null);
    }

    @Override
    public List<UsuarioVO> getAll() {
        return usuariorepository.findAll();
    }

    @Override
    public UsuarioVO autenticar(String username, String password) {
        UsuarioVO user = usuariorepository.findByUser(username);
        if (user != null && BCrypt.checkpw(password, user.getPass())) {
            return user;
        }
        return null;
    }

    @Override
    public UsuarioVO usuarioExiste(String username) {
        return usuariorepository.findByUser(username);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        // 1) Borrar todas las reservas del usuario
        reservaRepository.deleteByUsuarioId(id);
        // 2) Borrar todos los mensajes de chat de ese usuario
        chatMessageRepository.deleteByUsuarioId(id);
        // 3) Borrar todas las sesiones de chat del usuario
        chatSessionRepository.deleteByUsuarioId(id);
        // 4) Finalmente, eliminar el usuario
        usuariorepository.deleteById(id);
    }

    @Override
    public List<UsuarioVO> obtenerTodos() {
        return getAll();
    }

    @Override
    public UsuarioVO buscarPorId(Long id) {
        return getByID(id);
    }
}
