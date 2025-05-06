package es.serbatic.ServiciosAplicacion.service.usuario;

import java.util.List;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;

/**
 * Servicio para operaciones CRUD de Usuario.
 */
public interface UsuarioService {

    /**
     * Actualiza un usuario existente.
     * @param usuario objeto UsuarioVO con datos actualizados
     */
    void actualizar(UsuarioVO usuario);

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param usuario objeto UsuarioVO a insertar
     */
    void insertar(UsuarioVO usuario);

    /**
     * Recupera un usuario por su ID.
     * @param id identificador del usuario
     * @return UsuarioVO o null si no existe
     */
    UsuarioVO getByID(Long id);

    /**
     * Devuelve todos los usuarios.
     * @return lista de UsuarioVO
     */
    List<UsuarioVO> getAll();

    /**
     * Autentica un usuario según nombre y contraseña.
     * @param user nombre de usuario
     * @param pass contraseña en texto plano (verificar con BCrypt fuera)
     * @return UsuarioVO si existe, null en caso contrario
     */
    UsuarioVO autenticar(String user, String pass);

    /**
     * Comprueba si existe un usuario por nombre.
     * @param username nombre de usuario
     * @return UsuarioVO o null si no existe
     */
    UsuarioVO usuarioExiste(String username);

    /**
     * Elimina un usuario y sus sesiones asociadas.
     * @param id identificador del usuario
     */
    void eliminar(Long id);

    // Alias conveniencia:

    List<UsuarioVO> obtenerTodos();
    UsuarioVO buscarPorId(Long id);
}