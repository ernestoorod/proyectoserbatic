package es.serbatic.ServiciosAplicacion.model.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// La anotacion Data de Lombok genera automaticamente los metodos getter, setter, equals, hashCode y toString 
@Data
public class UsuarioDTO {

    // El ID es generado automáticamente y no se incluye en el DTO para la creación de un nuevo usuario

    // El nombre completo no puede estar vacío y no puede ser el mismo que el de otro usuario
    @NotBlank(message = "El nombre completo no puede estar vacío")
    private String fullName;

    // El nombre de usuario no puede estar vacío y no puede ser el mismo que el de otro usuario
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String user;

    // La contraseña no puede estar vacía y debe tener al menos 8 caracteres
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String pass;

    // El correo electrónico no puede estar vacío y debe tener un formato válido
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    // El teléfono no puede estar vacío y debe tener un formato válido (ejemplo: 123456789)
    @NotBlank(message = "El teléfono no puede estar vacío")
    private String phone;

    // La dirección no puede estar vacía
    @NotBlank(message = "La dirección no puede estar vacía")
    private String address;

    // El rol no puede estar vacío, pero no se valida si es admin o trabajador
    @NotBlank(message = "El rol no puede estar vacío")
    private String role;

    // Este campo solo se valida si el rol es admin (validación condicional en el backend)
    private String adminKey;

    
}
