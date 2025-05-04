package es.serbatic.ServiciosAplicacion.model.servicio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {

    private Integer id;
    private String nombre;
    private byte[] icono;    // Icono como byte[]
    private byte[] imagen;   // Imagen como byte[]
    private boolean activo;
}
