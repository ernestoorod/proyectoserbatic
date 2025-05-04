package es.serbatic.ServiciosAplicacion.service.usuario;

import java.util.List;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;

public interface UsuarioService {
    
	// Método para actualizar un usuario existente en la base de datos.
    public void actualizar(UsuarioVO usuario);
	// Metodo para insertar un nuevo usuario en la base de datos.
	public void insertar(UsuarioVO usuario);
	// Este método busca un usuario en la base de datos según su ID.
	public UsuarioVO getByID(Long id);
	// Este método devuelve una lista de objetos UsuarioVO que representan todos los usuarios en la base de datos.
	public List<UsuarioVO> getAll();
	// Este método busca un usuario en la base de datos según su nombre de usuario y contraseña.
	public UsuarioVO autenticar(String user, String pass);
	// Este método verifica si un usuario existe en la base de datos según su nombre de usuario.
	public UsuarioVO usuarioExiste(String username);
	// Este método elimina un usuario de la base de datos según su ID.
	public void eliminar(Long id);

	public List<UsuarioVO> obtenerTodos();
    public UsuarioVO buscarPorId(Long id);


}
