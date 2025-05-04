package es.serbatic.ServiciosAplicacion.control.reserva;

import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.service.reserva.ReservaService;
import es.serbatic.ServiciosAplicacion.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @GetMapping("/empresas/{empresaId}/horas-libres")
    @ResponseBody
    public List<String> horasLibres(
            @PathVariable Integer empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        try {
            return reservaService.horasLibres(empresaId, fecha);
        } catch (Exception e) {
            log.error("Error obteniendo horas libres para empresa {} y fecha {}: {}", empresaId, fecha, e.getMessage(), e);
            throw new RuntimeException("Error interno al obtener disponibilidad.");
        }
    }

    @PostMapping("/empresas/{empresaId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearReserva(
            @PathVariable Integer empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime hora,
            HttpSession session) {

        UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

        if (usuario == null) {
            throw new IllegalStateException("Debe iniciar sesión para reservar.");
        }

        Long usuarioId = (long) usuario.getId();
        reservaService.reservar(empresaId, fecha, hora, usuarioId);
    }

    @GetMapping
    @ResponseBody
    public List<ReservaVO> listarReservas(Principal principal) {
        String username = principal.getName();
        Long usuarioId = (long) usuarioService.usuarioExiste(username).getId();
        return reservaService.obtenerReservasPorUsuario(usuarioId);
    }

    @GetMapping("/reservas/mias")
    public String verMisReservas(HttpSession session, Model model) {
        UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

        if (usuario == null) {
            return "redirect:/login";
        }

        List<ReservaVO> reservas;

        if ("JEFE_EMPRESA".equalsIgnoreCase(usuario.getRole())) {
            reservas = reservaService.obtenerTodasLasReservas(); // ya implementado
        } else {
            reservas = reservaService.obtenerReservasPorUsuario((long) usuario.getId());
        }

        model.addAttribute("reservas", reservas);
        model.addAttribute("usuario", usuario); // ✅ esto es clave

        return "misreservas";
    }


    @PostMapping("/cancelar")
    public String cancelarReserva(@RequestParam Long reservaId, HttpSession session) {
        UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

        if (usuario == null) {
            return "redirect:/login";
        }

        if ("jefe_empresa".equalsIgnoreCase(usuario.getRole())) {
            reservaService.cancelarReservaComoJefe(reservaId);
        } else {
            reservaService.cancelarReserva(reservaId, usuario.getId());
        }

        return "redirect:/api/reservas/reservas/mias";
    }

    @GetMapping("/empresas/{empresaId}/primer-dia-disponible")
    @ResponseBody
    public String primerDiaDisponible(@PathVariable Integer empresaId) {
        LocalDate fecha = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            List<String> horas = reservaService.horasLibres(empresaId, fecha);
            if (horas != null && !horas.isEmpty()) {
                return fecha.toString();
            }
            fecha = fecha.plusDays(1);
        }
        throw new RuntimeException("No hay disponibilidad en los próximos 30 días.");
    }

    @GetMapping("/todas")
    @ResponseBody
    public List<ReservaVO> listarTodasLasReservas(HttpSession session) {
        UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");

        if (usuario == null || !"jefe_empresa".equalsIgnoreCase(usuario.getRole())) {
            throw new SecurityException("Acceso denegado.");
        }

        return reservaService.obtenerTodasLasReservas();
    }
}
