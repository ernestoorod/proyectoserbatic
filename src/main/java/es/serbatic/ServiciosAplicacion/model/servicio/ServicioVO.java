package es.serbatic.ServiciosAplicacion.model.servicio;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "servicios")
@Data
public class ServicioVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Lob
    @Column(name = "icono", columnDefinition = "LONGBLOB")
    private byte[] icono;   // Icono como BLOB

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;  // Imagen como BLOB

    private boolean activo;
}
