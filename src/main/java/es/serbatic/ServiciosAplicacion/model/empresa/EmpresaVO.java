package es.serbatic.ServiciosAplicacion.model.empresa;

import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "empresas")
@Data
public class EmpresaVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String descripcion;
    private String telefono;
    private String email;
    private String pais;
    private String ciudad;
    private String direccion;
    private String sitioWeb;

    @Lob
    @Column(name = "imagen_portada", columnDefinition = "LONGBLOB")
    private byte[] imagenPortada;

    private boolean activo;
    private boolean aceptaReservas;
    private Double latitud;
    private Double longitud;
    private String horaApertura;
    private String horaCierre;
    private String facebook;
    private String instagram;
    private String linkedin;
    private String x;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private ServicioVO servicio;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaVO> reservas;
}
