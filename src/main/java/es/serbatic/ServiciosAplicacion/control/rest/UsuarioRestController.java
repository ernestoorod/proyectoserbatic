package es.serbatic.ServiciosAplicacion.control.rest;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.service.usuario.UsuarioServiceImplementacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioRestController {

    @Autowired
    private UsuarioServiceImplementacion service;

    @GetMapping
    public List<UsuarioVO> obtenerUsuarios() {
        return service.getAll();
    }

    // Sólo “/{id}/rol” porque ya estás en /api/usuarios
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoRol = body.get("rol");
        UsuarioVO usuario = service.buscarPorId(id);
        if (usuario != null) {
            usuario.setRole(nuevoRol);
            service.actualizar(usuario);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
