package es.serbatic.ServiciosAplicacion.service.logs;

import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

@Service
public class LogService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<String, Long> countByLogLevel() throws Exception {
        Path path = Paths.get("logs/usuarios.log");
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                .filter(l -> l.contains(" INFO ") || l.contains(" WARN ") || l.contains(" ERROR "))
                .map(l -> {
                    if (l.contains(" INFO ")) return "INFO";
                    if (l.contains(" WARN ")) return "WARN";
                    return "ERROR";
                })
                .collect(Collectors.groupingBy(lvl -> lvl, Collectors.counting()));
        }
    }

    public Map<String, Long> countByHour() throws Exception {
        Path path = Paths.get("logs/app.log");
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                .map(l -> {
                    String ts = l.substring(0, 19);
                    LocalDateTime dt = LocalDateTime.parse(ts, FMT);
                    return dt.getHour() + ":00";
                })
                .collect(Collectors.groupingBy(hour -> hour, TreeMap::new, Collectors.counting()));
        }
    }
}
