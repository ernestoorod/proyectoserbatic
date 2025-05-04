// package es.serbatic.ServiciosAplicacion.service.chat;

// import es.serbatic.ServiciosAplicacion.model.chat.PreguntaIA;
// import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
// import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
// import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
// import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
// import es.serbatic.ServiciosAplicacion.repository.chat.PreguntaIARepository;
// import es.serbatic.ServiciosAplicacion.repository.empresa.EmpresaRepository;
// import es.serbatic.ServiciosAplicacion.repository.reserva.ReservaRepository;
// import es.serbatic.ServiciosAplicacion.repository.servicio.ServicioRepository;
// import jakarta.servlet.http.HttpSession;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class ChatServiceImpl implements ChatService {

//     @Autowired private EmpresaRepository empresaRepository;
//     @Autowired private ServicioRepository servicioRepository;
//     @Autowired private ReservaRepository reservaRepository;
//     @Autowired private PreguntaIARepository preguntaIARepository;
//     @Autowired private HttpSession session;

//     private final WebClient webClient;

//     public ChatServiceImpl() {
//         this.webClient = WebClient.builder()
//                 .baseUrl("https://api.openai.com/v1/chat/completions")
//                 .defaultHeader("Authorization", "Bearer tuapikey")
//                 .build();
//     }

//     @Override
//     public String processQuestion(String question) {
//         UsuarioVO usuario = getCurrentUser();
//         if (usuario == null) {
//             return "No hay sesión de usuario activa.";
//         }

//         Optional<PreguntaIA> preguntaExistente = preguntaIARepository.findAll().stream()
//                 .filter(p -> p.getTextoPregunta().trim().equalsIgnoreCase(question.trim()))
//                 .findFirst();

//         if (preguntaExistente.isPresent()) {
//             return preguntaExistente.get().getRespuestaGenerada();
//         }

//         List<EmpresaVO> empresas = empresaRepository.findAll();
//         List<ServicioVO> servicios = servicioRepository.findAll();
//         List<ReservaVO> reservas = reservaRepository.findAll();

//         String datosContexto = """
//         [USUARIO ACTUAL]
//         ID: %d, Nombre completo: %s, Usuario: %s, Email: %s, Teléfono: %s, Dirección: %s, Rol: %s

//         [EMPRESAS]
//         %s

//         [SERVICIOS]
//         %s

//         [RESERVAS]
//         %s
//         """.formatted(
//                 usuario.getId(),
//                 usuario.getFullName(),
//                 usuario.getUser(),
//                 usuario.getEmail(),
//                 usuario.getPhone(),
//                 usuario.getAddress(),
//                 usuario.getRole(),

//                 empresas.stream()
//                         .map(e -> "ID: " + e.getId()
//                                 + ", Nombre: " + e.getNombre()
//                                 + ", Descripción: " + e.getDescripcion()
//                                 + ", Teléfono: " + e.getTelefono()
//                                 + ", Email: " + e.getEmail()
//                                 + ", Ciudad: " + e.getCiudad()
//                                 + ", País: " + e.getPais()
//                                 + ", Dirección: " + e.getDireccion()
//                                 + ", Activa: " + e.isActivo()
//                                 + ", Acepta reservas: " + e.isAceptaReservas()
//                                 + ", Servicio: " + (e.getServicio() != null ? e.getServicio().getNombre() : "N/A")
//                                 + ", Horario: " + e.getHoraApertura() + " - " + e.getHoraCierre())
//                         .collect(Collectors.joining("\n")),

//                 servicios.stream()
//                         .map(s -> "ID: " + s.getId()
//                                 + ", Nombre: " + s.getNombre()
//                                 + ", Activo: " + s.isActivo())
//                         .collect(Collectors.joining("\n")),

//                 reservas.stream()
//                         .map(r -> "ID: " + r.getId()
//                                 + ", Fecha: " + r.getFecha()
//                                 + ", Hora: " + r.getHora()
//                                 + ", Empresa: " + r.getEmpresa().getNombre()
//                                 + ", Ciudad: " + r.getEmpresa().getCiudad()
//                                 + ", Usuario: " + r.getUsuario().getFullName())
//                         .collect(Collectors.joining("\n"))
//         );

//         String systemPrompt = """
//         Eres un asistente de una aplicación de gestión de usuarios, empresas, servicios y reservas.

//         Aquí tienes los datos actuales del sistema:
//         %s

//         Usa los datos para responder al usuario logueado (ID: %d, Usuario: %s) de forma clara y directa.
//         Si no puedes responder con la información disponible, simplemente dilo.

//         Pregunta:
//         """.formatted(datosContexto, usuario.getId(), usuario.getUser());

//         Map<String, Object> response = webClient.post()
//                 .bodyValue(Map.of(
//                         "model", "gpt-3.5-turbo",
//                         "messages", List.of(
//                                 Map.of("role", "system", "content", systemPrompt),
//                                 Map.of("role", "user", "content", question)
//                         )
//                 ))
//                 .retrieve()
//                 .bodyToMono(Map.class)
//                 .block();

//         String content = "No se pudo obtener respuesta del modelo.";
//         if (response != null && response.containsKey("choices")) {
//             Object choice = ((List<?>) response.get("choices")).get(0);
//             if (choice instanceof Map<?, ?> choiceMap) {
//                 Map<?, ?> message = (Map<?, ?>) choiceMap.get("message");
//                 content = (String) message.get("content");
//             }
//         }

//         PreguntaIA nuevaPregunta = new PreguntaIA();
//         nuevaPregunta.setTextoPregunta(question);
//         nuevaPregunta.setRespuestaGenerada(content);
//         nuevaPregunta.setRespondida(true);
//         nuevaPregunta.setFecha(LocalDateTime.now());
//         nuevaPregunta.setUsuario(usuario);
//         preguntaIARepository.save(nuevaPregunta);

//         return content;
//     }

//     private UsuarioVO getCurrentUser() {
//         return (UsuarioVO) session.getAttribute("usuarioExiste");
//     }
// }