package es.serbatic.ServiciosAplicacion.service.email;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.reserva.ReservaVO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Servicio de envío de emails en HTML neutro (grises y blancos),
 * para empresa y cliente, con botón de Google Calendar.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final DateTimeFormatter GCAL_FMT =
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final DateTimeFormatter MONTH_FMT =
        DateTimeFormatter.ofPattern("MMM");
    private static final DateTimeFormatter DAY_FMT =
        DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter FULLDATE_FMT =
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM");

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendReservationConfirmationToCompany(EmpresaVO empresa, ReservaVO reserva) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setTo(empresa.getEmail());
            helper.setSubject("Reserva confirmada: " + reserva.getId());
            helper.setText(buildBodyHtml(empresa.getNombre(),
                                         reserva,
                                         reserva.getUsuario().getFullName(),
                                         createGoogleCalendarLink(reserva),
                                         false), true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email a la empresa", e);
        }
    }

    @Override
    public void sendReservationConfirmationToClient(UsuarioVO usuario, ReservaVO reserva) {
        try {
            String calendarUrl = createGoogleCalendarLink(reserva);
            MimeMessage mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setTo(usuario.getEmail());
            helper.setSubject("Tu reserva en " + reserva.getEmpresa().getNombre() + " está confirmada");
            helper.setText(buildBodyHtml(usuario.getFullName(),
                                         reserva,
                                         reserva.getEmpresa().getNombre(),
                                         calendarUrl,
                                         true), true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email al cliente", e);
        }
    }

    /** Genera la URL para añadir al calendario de Google (1h de duración) */
    private String createGoogleCalendarLink(ReservaVO r) {
        ZonedDateTime start = r.getFecha().atTime(r.getHora())
                               .atZone(ZoneId.systemDefault())
                               .withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime end   = start.plusHours(1);
        String s = start.format(GCAL_FMT), e = end.format(GCAL_FMT);
        String title = URLEncoder.encode("Reserva en " + r.getEmpresa().getNombre(), StandardCharsets.UTF_8);
        String details = URLEncoder.encode("ID Reserva: " + r.getId(), StandardCharsets.UTF_8);
        return String.format(
          "https://calendar.google.com/calendar/render?action=TEMPLATE" +
          "&text=%s&dates=%s/%s&details=%s",
          title, s, e, details
        );
    }

    /**
     * Genera el HTML genérico para empresa o cliente.
     *
     * @param destinatarioNombre Nombre para el saludo.
     * @param r                   ReservaVO.
     * @param infoAdicional       Nombre de cliente (para empresa) o nombre de empresa (para cliente).
     * @param calLink             URL de Google Calendar.
     * @param esCliente           Si es true, mostrará “Con: Empresa”; si false, “Con: Cliente”.
     */
    private String buildBodyHtml(String destinatarioNombre,
                                 ReservaVO r,
                                 String infoAdicional,
                                 String calLink,
                                 boolean esCliente) {

        String mes = r.getFecha().format(MONTH_FMT).toUpperCase();
        String dia = r.getFecha().format(DAY_FMT);
        String fechaCompleta = capitalize(r.getFecha().format(FULLDATE_FMT));

        return """
        <html>
        <body style="margin:0;padding:20px;font-family:Arial,sans-serif;background:#f2f2f2;">
        <table width="100%%" cellpadding="0" cellspacing="0" style="max-width:600px;margin:auto;background:#ffffff;border-radius:6px;overflow:hidden;">
          <tr>
            <td style="background:#e0e0e0;padding:16px 24px;">
              <h1 style="margin:0;font-size:22px;color:#333333;">¡Reserva confirmada!</h1>
            </td>
          </tr>
          <tr>
            <td style="padding:24px;color:#444444;font-size:16px;line-height:1.5;">
              <p>Hola <strong>%s</strong>,</p>
              <table cellpadding="0" cellspacing="0" style="width:100%%;margin:16px 0;">
                <tr>
                  <td width="100" valign="top" style="background:#dddddd;text-align:center;padding:12px;border-radius:4px;">
                    <div style="font-size:12px;font-weight:bold;color:#555555;">%s</div>
                    <div style="font-size:28px;font-weight:bold;color:#222222;margin-top:4px;">%s</div>
                  </td>
                  <td style="padding-left:16px;">
                    <p style="margin:4px 0;font-size:16px;"><strong>%s</strong></p>
                    <p style="margin:4px 0;font-size:16px;">%s</p>
                    <p style="margin:4px 0;font-size:16px;">Con: %s</p>
                  </td>
                </tr>
              </table>
              <p style="text-align:left;margin:24px 0;">
                <a href="%s"
                   style="display:inline-block;padding:12px 20px;background:#888888;color:#ffffff;text-decoration:none;border-radius:4px;font-size:16px;">
                  Añadir a Google Calendar
                </a>
              </p>
              <p style="font-size:14px;color:#777777;margin-top:32px;">
                Gracias por confiar en nosotros.<br>
                Saludos,<br>
                El equipo de MIXO
              </p>
            </td>
          </tr>
        </table>
        </body>
        </html>
        """.formatted(
            destinatarioNombre,
            mes, dia,
            fechaCompleta,
            r.getHora().toString(),
            infoAdicional,
            calLink
        );
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
