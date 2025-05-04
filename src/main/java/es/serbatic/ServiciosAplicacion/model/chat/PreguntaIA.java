package es.serbatic.ServiciosAplicacion.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PreguntaIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioVO usuario;

    @JsonProperty("usuarioId")
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    @Lob
    @Column(name = "texto_pregunta", columnDefinition = "TEXT")
    @JsonProperty("textoPregunta")
    private String textoPregunta;

    @Lob
    @Column(name = "respuesta_generada", columnDefinition = "LONGTEXT")
    @JsonProperty("respuestaGenerada")
    private String respuestaGenerada;

    @JsonProperty("respondida")
    private boolean respondida;

    @JsonProperty("fecha")
    private LocalDateTime fecha;

    public PreguntaIA() {
        this.fecha = LocalDateTime.now();
        this.respondida = false;
    }
}
