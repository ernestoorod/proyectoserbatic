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

        // Construir contexto con datos de usuario, empresas, servicios y reservas
        List<EmpresaVO> empresas = empresaRepository.findAll();
        List<ServicioVO> servicios = servicioRepository.findAll();
        List<ReservaVO> reservas = reservaRepository.findAll();

        String contextoEmpresas = empresas.stream()
            .map(e -> "ID: " + e.getId()
                + ", Nombre: " + e.getNombre()
                + ", Descripción: " + e.getDescripcion()
                + ", Ciudad: " + e.getCiudad()
                + ", Activa: " + e.isActivo())
            .collect(Collectors.joining("\n"));

        String contextoServicios = servicios.stream()
            .map(s -> "ID: " + s.getId()
                + ", Nombre: " + s.getNombre()
                + ", Activo: " + s.isActivo())
            .collect(Collectors.joining("\n"));

        String contextoReservas = reservas.stream()
            .map(r -> "ID: " + r.getId()
                + ", Fecha: " + r.getFecha()
                + ", Usuario: " + r.getUsuario().getFullName())
            .collect(Collectors.joining("\n"));

        String systemPrompt = String.format("""
            Eres un asistente de una aplicación de gestión de usuarios, empresas, servicios y reservas.

            [USUARIO]
            ID: %d, Usuario: %s, Email: %s

            [EMPRESAS]
            %s

            [SERVICIOS]
            %s

            [RESERVAS]
            %s

            Responde de forma clara y directa. Si no hay suficiente información, dilo.
            Pregunta:
            """,
            usuario.getId(),
            usuario.getUser(),
            usuario.getEmail(),
            contextoEmpresas,
            contextoServicios,
            contextoReservas
        );

        // Llamada a OpenAI
        Map<String, Object> response = webClient.post()
            .bodyValue(Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user",   "content", question)
                )
            ))
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        // Extraer contenido
        String content = "No se pudo obtener respuesta del modelo.";
        if (response != null && response.containsKey("choices")) {
            Object choice = ((List<?>) response.get("choices")).get(0);
            if (choice instanceof Map<?, ?> choiceMap) {
                Map<?, ?> message = (Map<?, ?>) choiceMap.get("message");
                content = (String) message.get("content");
            }
        }

        return content;
    }

    /** Recupera el usuario actual de la sesión HTTP */
    private UsuarioVO getCurrentUser() {
        return (UsuarioVO) session.getAttribute("usuarioExiste");
    }
}
