package es.serbatic.ServiciosAplicacion.control.chat;

import es.serbatic.ServiciosAplicacion.model.chat.ChatMessage;
import es.serbatic.ServiciosAplicacion.model.chat.ChatSession;
import es.serbatic.ServiciosAplicacion.model.chat.ChatMessage.MessageType;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.repository.chat.ChatMessageRepository;
import es.serbatic.ServiciosAplicacion.repository.chat.ChatSessionRepository;
import es.serbatic.ServiciosAplicacion.service.chat.ServiceChat;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatSessionRepository sessionRepo;

    @Autowired
    private ChatMessageRepository messageRepo;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private ServiceChat chatService;

    // DTOs para no exponer entidades completas
    public static class SessionDTO {
        public Long id;
        public String firstQuestion;
        public SessionDTO(Long id, String firstQuestion) {
            this.id = id;
            this.firstQuestion = firstQuestion;
        }
    }

    public static class MessageDTO {
        public String content;
        public String type;
        public MessageDTO(String content, String type) {
            this.content = content;
            this.type = type;
        }
    }

    /** Crear nueva sesión de chat */
    @PostMapping("/session")
    public Long createSession(@RequestBody Map<String, String> body) {
        UsuarioVO user = (UsuarioVO) httpSession.getAttribute("usuarioExiste");
        ChatSession cs = ChatSession.builder()
            .usuario(user)
            .firstQuestion(body.get("firstQuestion"))
            .build();
        sessionRepo.save(cs);
        return cs.getId();
    }

    /** Enviar mensaje a sesión existente */
    @PostMapping("/{sessionId}/message")
    public ResponseEntity<String> message(
        @PathVariable Long sessionId,
        @RequestBody Map<String, String> body
    ) {
        ChatSession cs = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        // Guardar mensaje enviado
        String question = body.get("text");
        ChatMessage sent = ChatMessage.builder()
            .session(cs)
            .content(question)
            .type(MessageType.SENT)
            .build();
        messageRepo.save(sent);

        // Procesar con IA
        String reply = chatService.processQuestion(question);

        // Guardar respuesta
        ChatMessage recv = ChatMessage.builder()
            .session(cs)
            .content(reply)
            .type(MessageType.RECEIVED)
            .build();
        messageRepo.save(recv);

        return ResponseEntity.ok(reply);
    }

    /** Listar sesiones del usuario como DTO */
    @GetMapping("/sessions")
    public List<SessionDTO> sessions() {
        UsuarioVO user = (UsuarioVO) httpSession.getAttribute("usuarioExiste");
        return sessionRepo.findByUsuarioOrderByCreatedAtDesc(user)
            .stream()
            .map(cs -> new SessionDTO(cs.getId(), cs.getFirstQuestion()))
            .collect(Collectors.toList());
    }

    /** Obtener mensajes de una sesión como DTO */
    @GetMapping("/session/{id}/messages")
    public List<MessageDTO> getMessages(@PathVariable Long id) {
        ChatSession cs = sessionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        return messageRepo.findBySessionOrderByTimestampAsc(cs)
            .stream()
            .map(m -> new MessageDTO(m.getContent(), m.getType().name().toLowerCase()))
            .collect(Collectors.toList());
    }

    /** Eliminar una sesión de chat por ID */
    @Transactional
    @DeleteMapping("/session/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id) {
        UsuarioVO user = (UsuarioVO) httpSession.getAttribute("usuarioExiste");

        ChatSession cs = sessionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!cs.getUsuario().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        messageRepo.deleteBySession(cs);
        sessionRepo.delete(cs);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
