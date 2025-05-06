package es.serbatic.ServiciosAplicacion.service.chat;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.repository.empresa.EmpresaRepository;
import es.serbatic.ServiciosAplicacion.repository.reserva.ReservaRepository;
import es.serbatic.ServiciosAplicacion.repository.servicio.ServicioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ServiceChat {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HttpSession session;

    private final WebClient webClient;

    public ChatServiceImpl() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader("Authorization", "Bearer TU_API_KEY")
            .build();
    }

    @Override
    public String processQuestion(String question) {
        UsuarioVO usuario = getCurrentUser();
        if (usuario == null) {
            return "No hay sesión de usuario activa.";
        }

        if (esPreguntaIrrelevante(question)) {
            return "Tu pregunta no parece estar relacionada con la aplicación. Por favor, pregúntame algo sobre empresas, servicios, usuarios o reservas.";
        }

        List<EmpresaVO> empresas = empresaRepository.findAll();
        List<ServicioVO> servicios = servicioRepository.findAll();
        List<ReservaVO> reservas = reservaRepository.findAll();

        String systemPrompt = String.format("""
            Eres un asistente de una aplicación que gestiona usuarios, empresas, servicios y reservas.

            Tu trabajo es ayudar a los usuarios respondiendo preguntas sobre sus datos, las empresas disponibles, los servicios, y sus reservas. Usa únicamente la información proporcionada.

            [USUARIO ACTUAL]
            ID: %d
            Nombre completo: %s
            Nombre de usuario: %s
            Email: %s
            Teléfono: %s
            Dirección: %s
            Rol: %s

            [EMPRESAS REGISTRADAS]
            %s

            [SERVICIOS DISPONIBLES]
            %s

            [RESERVAS DEL USUARIO]
            %s

            Responde en español, de forma clara, precisa y útil. Si no encuentras información suficiente para responder una pregunta, indica que no hay información disponible.
            Pregunta:
            """,
            usuario.getId(),
            usuario.getFullName(),
            usuario.getUser(),
            usuario.getEmail(),
            usuario.getPhone(),
            usuario.getAddress(),
            usuario.getRole(),
            construirContextoEmpresas(empresas),
            construirContextoServicios(servicios),
            construirContextoReservasUsuario(reservas, usuario)
        );

        Map<String, Object> response = webClient.post()
            .bodyValue(Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", question)
                )
            ))
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        if (response != null && response.containsKey("choices")) {
            List<?> choices = (List<?>) response.get("choices");
            if (!choices.isEmpty()) {
                Object choice = choices.get(0);
                if (choice instanceof Map<?, ?> choiceMap && choiceMap.get("message") instanceof Map<?, ?> message) {
                    return (String) message.get("content");
                }
            }
        }

        return "No se pudo obtener respuesta del modelo.";
    }

    private UsuarioVO getCurrentUser() {
        return (UsuarioVO) session.getAttribute("usuarioExiste");
    }

    private String construirContextoEmpresas(List<EmpresaVO> empresas) {
        return empresas.stream()
            .limit(20)
            .map(e -> String.format("""
                - ID: %d
                  Nombre: %s
                  Descripción: %s
                  Teléfono: %s
                  Email: %s
                  Ciudad: %s, País: %s
                  Dirección: %s
                  Web: %s
                  Activa: %s
                  Acepta reservas: %s
                  Horario: %s - %s
                  Redes: Facebook: %s, Instagram: %s, LinkedIn: %s, X: %s
                  Servicio Asociado: %s
                """,
                e.getId(), e.getNombre(), e.getDescripcion(), e.getTelefono(), e.getEmail(),
                e.getCiudad(), e.getPais(), e.getDireccion(), e.getSitioWeb(), e.isActivo(),
                e.isAceptaReservas(), e.getHoraApertura(), e.getHoraCierre(),
                e.getFacebook(), e.getInstagram(), e.getLinkedin(), e.getX(),
                e.getServicio() != null ? e.getServicio().getNombre() : "Ninguno"
            )).collect(Collectors.joining("\n"));
    }

    private String construirContextoServicios(List<ServicioVO> servicios) {
        return servicios.stream()
            .limit(20)
            .map(s -> String.format("- ID: %d, Nombre: %s, Activo: %s",
                s.getId(), s.getNombre(), s.isActivo()))
            .collect(Collectors.joining("\n"));
    }

    private String construirContextoReservasUsuario(List<ReservaVO> reservas, UsuarioVO usuario) {
        return reservas.stream()
            .filter(r -> r.getUsuario().getId().equals(usuario.getId()))
            .map(r -> String.format("- ID: %d, Fecha: %s, Hora: %s, Empresa: %s",
                r.getId(), r.getFecha(), r.getHora(),
                r.getEmpresa() != null ? r.getEmpresa().getNombre() : "Desconocida"))
            .collect(Collectors.joining("\n"));
    }

    private boolean esPreguntaIrrelevante(String pregunta) {
        String q = pregunta.toLowerCase().trim();

        return q.matches(".*(\\d+\\s*[+\\-*/^]\\s*\\d+).*") ||
               q.matches(".*\\b(capital|presidente|famoso|historia|planeta|clima|tiempo)\\b.*") || 
               q.length() < 5;
    }
}
