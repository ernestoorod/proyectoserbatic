package es.serbatic.ServiciosAplicacion.repository.reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;

public interface ReservaRepository extends JpaRepository<ReservaVO, Long> {

    List<ReservaVO> findByEmpresaAndFecha(EmpresaVO empresa, LocalDate fecha);
    List<ReservaVO> findByUsuarioId(Long usuarioId);
    List<ReservaVO> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    boolean existsByEmpresaAndFechaAndHora(EmpresaVO emp,
                                           LocalDate fecha,
                                           LocalTime hora);

    /**
     * Borra todas las reservas asociadas a un usuario.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ReservaVO r WHERE r.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}
