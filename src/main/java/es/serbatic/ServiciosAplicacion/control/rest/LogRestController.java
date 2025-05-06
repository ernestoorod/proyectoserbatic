package es.serbatic.ServiciosAplicacion.control.rest;

import es.serbatic.ServiciosAplicacion.service.logs.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogRestController {

    @Autowired
    private LogService logService;

    /**
     * Devuelve la distribución de logs por nivel (INFO, WARN, ERROR).
     * GET /api/logs/levels
     */
    @GetMapping("/levels")
    public ResponseEntity<Map<String, Long>> getCountsByLevel() {
        try {
            Map<String, Long> counts = logService.countByLogLevel();
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            // Podrías añadir un cuerpo de error más detallado
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Devuelve el número de eventos de log agrupados por hora.
     * GET /api/logs/hours
     */
    @GetMapping("/hours")
    public ResponseEntity<Map<String, Long>> getCountsByHour() {
        try {
            Map<String, Long> counts = logService.countByHour();
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
