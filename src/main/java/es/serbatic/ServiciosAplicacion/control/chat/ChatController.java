// package es.serbatic.ServiciosAplicacion.control.chat;

// import es.serbatic.ServiciosAplicacion.model.chat.PreguntaIA;
// import es.serbatic.ServiciosAplicacion.repository.chat.PreguntaIARepository;
// import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
// import es.serbatic.ServiciosAplicacion.service.chat.ChatService;
// import jakarta.servlet.http.HttpSession;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.*;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/chat")
// @CrossOrigin
// public class ChatController {

//     @Autowired
//     private ChatService chatService;

//     @Autowired
//     private PreguntaIARepository preguntaIARepository;

//     @PostMapping
//     public ResponseEntity<String> chat(@RequestBody String question, HttpSession session) {
//         String respuesta = chatService.processQuestion(question);
//         UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

//         if (usuario != null) {
//             PreguntaIA nueva = new PreguntaIA();
//             nueva.setTextoPregunta(question);
//             nueva.setRespuestaGenerada(respuesta);
//             nueva.setFecha(LocalDateTime.now());
//             nueva.setUsuario(usuario);
//             preguntaIARepository.save(nueva);
//         }

//         return ResponseEntity.ok(respuesta);
//     }

//     @GetMapping("/historial")
//     public List<PreguntaIA> getHistorial(@RequestParam Long usuarioId) {
//         UsuarioVO usuario = new UsuarioVO();
//         usuario.setId(usuarioId);
//         return preguntaIARepository.findTop5ByUsuarioOrderByFechaDesc(usuario);
//     }

//     @DeleteMapping("/historial/{id}")
//     public ResponseEntity<Void> eliminarPregunta(@PathVariable Long id, HttpSession session) {
//         Optional<PreguntaIA> pregunta = preguntaIARepository.findById(id);
//         UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

//         if (pregunta.isPresent() && usuario != null &&
//             pregunta.get().getUsuario().getId().equals(usuario.getId())) {
//             preguntaIARepository.deleteById(id);
//             return ResponseEntity.noContent().build();
//         }

//         return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//     }

//     @DeleteMapping("/historial/usuario/{usuarioId}")
//     public ResponseEntity<Void> eliminarHistorialCompleto(@PathVariable Long usuarioId, HttpSession session) {
//         UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

//         if (usuario != null && usuario.getId().equals(usuarioId)) {
//             preguntaIARepository.deleteByUsuario(usuario);
//             return ResponseEntity.noContent().build();
//         }

//         return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//     }
// }
