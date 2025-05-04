package es.serbatic.ServiciosAplicacion.model.reserva;

import java.time.LocalDate;
import java.time.LocalTime;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "reservas",
    uniqueConstraints = @UniqueConstraint(columnNames = {"empresa_id", "fecha", "hora"})
)
@Data
public class ReservaVO {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaVO empresa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioVO usuario;    

    private LocalDate fecha;

    private LocalTime hora;
}
