package es.serbatic.ServiciosAplicacion.service.reserva;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.repository.reserva.ReservaRepository;
import es.serbatic.ServiciosAplicacion.service.email.EmailService;
import es.serbatic.ServiciosAplicacion.service.empresa.EmpresaService;
import es.serbatic.ServiciosAplicacion.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImplementacion implements ReservaService {

    private final ReservaRepository reservaRepo;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final EmailService emailService;  // <-- Inyectamos el servicio de correo

    @Override
    public List<String> horasLibres(Integer empresaId, LocalDate fecha) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(empresaId);

        DateTimeFormatter fmtHHmm = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime apertura = LocalTime.parse(empresa.getHoraApertura(), fmtHHmm);
        LocalTime cierre   = LocalTime.parse(empresa.getHoraCierre(),   fmtHHmm);

        Set<LocalTime> ocupadas = reservaRepo
            .findByEmpresaAndFecha(empresa, fecha)
            .stream()
            .map(ReservaVO::getHora)
            .collect(Collectors.toSet());

        List<String> libres = new ArrayList<>();
        for (LocalTime t = apertura; t.isBefore(cierre); t = t.plusMinutes(30)) {
            if (!ocupadas.contains(t)) {
                libres.add(t.format(fmtHHmm));
            }
        }
        return libres;
    }

    @Override
    @Transactional
    public void reservar(Integer empresaId, LocalDate fecha, LocalTime hora, Long usuarioId) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(empresaId);
        UsuarioVO usuario = usuarioService.getByID(usuarioId);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
        }

        List<ReservaVO> reservasDelDia = reservaRepo.findByUsuarioIdAndFecha(usuarioId, fecha);
        if (reservasDelDia.size() >= 2) {
            throw new IllegalStateException("Ya tienes 2 reservas para este día.");
        }

        boolean ocupado = reservaRepo
            .existsByEmpresaAndFechaAndHora(empresa, fecha, hora);
        if (ocupado) {
            throw new IllegalStateException("Hueco ocupado");
        }

        // Creación y guardado de la reserva
        ReservaVO r = new ReservaVO();
        r.setEmpresa(empresa);
        r.setFecha(fecha);
        r.setHora(hora);
        r.setUsuario(usuario);
        reservaRepo.save(r);

        // Disparamos correos de confirmación a empresa y cliente
        emailService.sendReservationConfirmationToCompany(empresa, r);
        emailService.sendReservationConfirmationToClient(usuario, r);
    }

    @Override
    public List<ReservaVO> obtenerReservasPorUsuario(Long usuarioId) {
        return reservaRepo.findByUsuarioId(usuarioId);
    }

    @Override
    public void cancelarReserva(Long reservaId, Long usuarioId) {
        ReservaVO reserva = reservaRepo.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalStateException("No puedes cancelar una reserva que no es tuya");
        }

        reservaRepo.delete(reserva);
    }

    @Override
    public List<ReservaVO> obtenerTodasLasReservas() {
        return reservaRepo.findAll();
    }

    @Override
    public void cancelarReservaComoJefe(Long reservaId) {
        ReservaVO reserva = reservaRepo.findById(reservaId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        reservaRepo.delete(reserva);
    }
}
