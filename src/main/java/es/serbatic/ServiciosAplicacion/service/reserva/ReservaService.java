package es.serbatic.ServiciosAplicacion.service.reserva;

import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaService {
    List<String> horasLibres(Integer empresaId, LocalDate fecha);
    void reservar(Integer empresaId, LocalDate fecha, LocalTime hora, Long usuarioId);
    List<ReservaVO> obtenerReservasPorUsuario(Long usuarioId);
    void cancelarReserva(Long reservaId, Long usuarioId);
    List<ReservaVO> obtenerTodasLasReservas();
    void cancelarReservaComoJefe(Long reservaId);
}
