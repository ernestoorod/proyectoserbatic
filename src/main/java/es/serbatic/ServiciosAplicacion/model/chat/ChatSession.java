package es.serbatic.ServiciosAplicacion.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;

@Entity
@Table(name = "chat_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioVO usuario;

    @Column(name = "first_question", nullable = false)
    private String firstQuestion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Inicializa createdAt antes de persistir */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
