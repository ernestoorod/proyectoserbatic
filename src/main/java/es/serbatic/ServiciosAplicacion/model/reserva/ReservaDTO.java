package es.serbatic.ServiciosAplicacion.model.reserva;

import lombok.Data;

@Data
public class ReservaDTO {
    private Long id;
    private String fecha;
    private String hora;
    private Long usuarioId;
    private Long empresaId;
}
