// EmailService.java
package es.serbatic.ServiciosAplicacion.service.email;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;

/**
 * Interfaz para el servicio de envío de emails.
 */
public interface EmailService {
    /**
     * Envía un email de confirmación de reserva a la empresa.
     *
     * @param empresa La empresa destinataria.
     * @param reserva La reserva confirmada.
     */
    void sendReservationConfirmationToCompany(EmpresaVO empresa, ReservaVO reserva);

    /**
     * Envía un email de confirmación de reserva al cliente.
     *
     * @param usuario El cliente destinatario.
     * @param reserva La reserva confirmada.
     */
    void sendReservationConfirmationToClient(UsuarioVO usuario, ReservaVO reserva);
}