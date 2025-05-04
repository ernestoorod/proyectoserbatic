package es.serbatic.ServiciosAplicacion.model.usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// La anotacion Data de Lombok genera automaticamente los metodos getter, setter, equals, hashCode y toString
@Data
// La anotacion Entity indica que esta clase es una entidad JPA
@Entity
// La anotacion Table indica el nombre de la tabla en la base de datos
@Table(name = "usuario")
public class UsuarioVO {
    // La anotacion Id indica que este campo es la clave primaria de la entidad
    @Id
    // La anotacion GeneratedValue indica que el valor de este campo se generara automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String fullName;
    private String user; // No puede ser el mismo que el de otro usuario
    private String pass;
    private String email;
    private String phone;
    private String address;
    private String role; // Puede ser usuario normal, trabajador o admin, para ser admin tienes que meter una clave unica
    // La clave unica sera una clave la cual se escribira en un cuadro de texto que aparecera al poner la opcion de usuario admin
}
