package es.serbatic.ServiciosAplicacion.service.usuario;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioDTO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.repository.usuario.UsuarioRepository;

@Service
public class UsuarioServiceImplementacion implements UsuarioService {

    @Autowired
    private UsuarioRepository usuariorepository;

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
        List<UsuarioVO> usuarios = usuariorepository.findAll();
        for (UsuarioVO usuario : usuarios) {
            if (usuario.getUser().equals(username) && BCrypt.checkpw(password, usuario.getPass())) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public UsuarioVO usuarioExiste(String username) {
        List<UsuarioVO> usuarios = usuariorepository.findAll();
        for (UsuarioVO usuario : usuarios) {
            if (usuario.getUser().equals(username)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public void eliminar(Long id) {
        usuariorepository.deleteById(id); 
    }

    @Override
    public UsuarioVO buscarPorId(Long id) {
        return usuariorepository.findById(id).orElse(null);
    }

    // Método auxiliar idéntico a getAll, por compatibilidad
    public List<UsuarioVO> obtenerTodos() {
        return usuariorepository.findAll();
    }

    public interface UsuarioService {
        void insertarUsuario(UsuarioDTO usuario);
    }
}
