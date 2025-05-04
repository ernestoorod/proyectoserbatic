package es.serbatic.ServiciosAplicacion.control.servicio;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
import es.serbatic.ServiciosAplicacion.service.servicio.ServicioService;

@Controller
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioService.getAll());
        return "listaServicios";
    }

    @GetMapping("/json")
    @ResponseBody
    public List<ServicioVO> obtenerServiciosJson() {
        return servicioService.getAll();
    }

    @PostMapping("/guardar")
    public String guardarServicio(
            @ModelAttribute ServicioVO servicio,
            @RequestParam(value = "iconoFile", required = false) MultipartFile iconoFile,
            @RequestParam(value = "portadaFile", required = false) MultipartFile portadaFile,
            Model model) {

        try {
            if (servicio.getId() == null || servicio.getId() == 0) {
                if (servicioService.existePorNombre(servicio.getNombre())) {
                    throw new IllegalArgumentException("Ya existe un servicio con ese nombre.");
                }
            } else {
                ServicioVO existente = servicioService.getByNombre(servicio.getNombre());
                if (existente != null && !existente.getId().equals(servicio.getId())) {
                    throw new IllegalArgumentException("Ya existe un servicio con ese nombre.");
                }
            }

            if (iconoFile != null && !iconoFile.isEmpty()) {
                servicio.setIcono(iconoFile.getBytes());
            } else if (servicio.getId() != null) {
                ServicioVO existente = servicioService.getById(servicio.getId());
                if (existente != null) {
                    servicio.setIcono(existente.getIcono());
                }
            }

            if (portadaFile != null && !portadaFile.isEmpty()) {
                servicio.setImagen(portadaFile.getBytes());
            } else if (servicio.getId() != null) {
                ServicioVO existente = servicioService.getById(servicio.getId());
                if (existente != null) {
                    servicio.setImagen(existente.getImagen());
                }
            }

            System.out.println(">> Guardando: " + servicio.getNombre());
            System.out.println("   Icono size: " + (servicio.getIcono() != null ? servicio.getIcono().length : "null"));
            System.out.println("   Imagen size: " + (servicio.getImagen() != null ? servicio.getImagen().length : "null"));

            servicioService.save(servicio);

            return "redirect:/api/servicios/administracionServicios";

        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("servicio", servicio);
            model.addAttribute("servicios", servicioService.getAll());
            model.addAttribute("errorNombre", e.getMessage());
            return "administracionServicios";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable Integer id, Model model) {
        model.addAttribute("servicio", servicioService.getById(id));
        model.addAttribute("servicios", servicioService.getAll());
        return "administracionServicios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Integer id) {
        try {
            servicioService.deleteById(id);
            return "redirect:/api/servicios/administracionServicios?eliminado=ok";
        } catch (Exception e) {
            return "redirect:/api/servicios/administracionServicios?eliminado=error";
        }
    }

    @GetMapping("/administracionServicios")
    public String mostrarVistaAdministracion(Model model) {
        model.addAttribute("servicios", servicioService.getAll());
        model.addAttribute("servicio", new ServicioVO());
        return "administracionServicios";
    }

    @GetMapping("/icono/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerIcono(@PathVariable Integer id) {
        ServicioVO servicio = servicioService.getById(id);
        if (servicio != null && servicio.getIcono() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // O ajusta MIME según el tipo de imagen
            return new ResponseEntity<>(servicio.getIcono(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Integer id) {
        ServicioVO servicio = servicioService.getById(id);
        if (servicio != null && servicio.getImagen() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // O ajusta MIME según el tipo de imagen
            return new ResponseEntity<>(servicio.getImagen(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
