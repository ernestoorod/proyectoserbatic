package es.serbatic.ServiciosAplicacion.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
import es.serbatic.ServiciosAplicacion.service.servicio.ServicioService;

@Controller
public class HomeController {

    @Autowired
    private ServicioService servicioService;
    
    @GetMapping("/")
    public String index(Model model) {
        try {
            List<ServicioVO> servicios = servicioService.getAll();
            if (servicios != null && !servicios.isEmpty()) {
                model.addAttribute("servicios", servicios);
            }
        } catch (DataAccessException ex) {
            System.err.println("No se pudo acceder a la tabla 'servicios': " + ex.getMessage());
        }
        return "index";
    }

    @GetMapping("/busqueda")
    public String goBusqueda(){
        return "busqueda";
    }       

    @GetMapping("/chat")
    public String goChat() {
        return "chat";
    }

}
