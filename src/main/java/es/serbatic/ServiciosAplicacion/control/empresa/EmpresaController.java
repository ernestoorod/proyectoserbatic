package es.serbatic.ServiciosAplicacion.control.empresa;

import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaVO;
import es.serbatic.ServiciosAplicacion.model.empresa.EmpresaDTO;
import es.serbatic.ServiciosAplicacion.model.servicio.ServicioVO;
import es.serbatic.ServiciosAplicacion.service.empresa.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping("/empresas")
    public String listarEmpresas(
            @RequestParam(name = "servicioID", required = false) Integer servicioId,
            Model model
    ) {
        List<EmpresaVO> empresasVO;
        ServicioVO servicio = null;

        if (servicioId != null) {
            empresasVO = empresaService.obtenerEmpresasPorServicio(servicioId);
            servicio = new ServicioVO();
            servicio.setId(servicioId);
        } else {
            empresasVO = empresaService.listarEmpresas();
        }

        LocalTime ahora = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Map<String, Object>> empresas = new ArrayList<>();

        for (EmpresaVO empresa : empresasVO) {
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", empresa.getId());
            datos.put("nombre", empresa.getNombre());

            try {
                String aperturaStr = empresa.getHoraApertura();
                String cierreStr = empresa.getHoraCierre();

                if (aperturaStr != null && cierreStr != null) {
                    LocalTime apertura = LocalTime.parse(aperturaStr, formatter);
                    LocalTime cierre = LocalTime.parse(cierreStr, formatter);

                    if (ahora.isBefore(apertura)) {
                        datos.put("estadoHorario", "Cerrado · Abre a las " + aperturaStr);
                    } else if (ahora.isAfter(cierre)) {
                        datos.put("estadoHorario", "Cerrado · Abre mañana a las " + aperturaStr);
                    } else {
                        datos.put("estadoHorario", "Abierto · Cierra a las " + cierreStr);
                    }
                } else {
                    datos.put("estadoHorario", "Horario no disponible");
                }
            } catch (Exception e) {
                datos.put("estadoHorario", "Horario inválido");
            }

            empresas.add(datos);
        }

        model.addAttribute("empresas", empresas);
        model.addAttribute("servicio", servicio);
        return "empresas";
    }


    @GetMapping("/empresa")
    public String verEmpresa(
            @RequestParam("empresaID") Integer empresaID,
            Model model
    ) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(empresaID);

        if (empresa.getImagenPortada() != null) {
            String base64 = Base64.getEncoder().encodeToString(empresa.getImagenPortada());
            model.addAttribute("imagenPortadaBase64", base64);
        }

        model.addAttribute("empresa", empresa);
        return "empresa";
    }

    @GetMapping("/empresa/{id}/imagen")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagenPortada(@PathVariable Integer id) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(id);
        if (empresa == null || empresa.getImagenPortada() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(empresa.getImagenPortada());
    }

    @GetMapping("/formularioempresa")
    public String mostrarFormularioEmpresa(
            @RequestParam(value = "empresaID", required = false) Integer empresaID,
            @RequestParam(value = "servicioID", required = false) Integer servicioID,
            Model model) {

        EmpresaVO empresa;

        if (empresaID != null) {
            empresa = empresaService.obtenerEmpresaPorId(empresaID);
        } else {
            empresa = new EmpresaVO();
            if (servicioID != null) {
                ServicioVO servicio = new ServicioVO();
                servicio.setId(servicioID);
                empresa.setServicio(servicio);
            }
        }

        if (empresa.getImagenPortada() != null) {
            String base64 = Base64.getEncoder().encodeToString(empresa.getImagenPortada());
            model.addAttribute("imagenPortadaBase64", base64);
        }

        model.addAttribute("empresa", empresa);
        return "formularioempresas";
    }

    @PostMapping("/guardar")
    public String guardarEmpresa(
            @ModelAttribute EmpresaVO empresa,
            @RequestParam(value = "imagenPortadaFile", required = false) MultipartFile imagenPortadaFile
    ) throws IOException {

        if (imagenPortadaFile != null && !imagenPortadaFile.isEmpty()) {
            empresa.setImagenPortada(imagenPortadaFile.getBytes());
        }

        empresaService.guardarEmpresa(empresa);
        return "redirect:/empresas?servicioID=" + empresa.getServicio().getId();
    }

    @GetMapping("/editar")
    public String editarEmpresa(@RequestParam("empresaID") Integer empresaID, Model model) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(empresaID);

        if (empresa.getImagenPortada() != null) {
            String base64 = Base64.getEncoder().encodeToString(empresa.getImagenPortada());
            model.addAttribute("imagenPortadaBase64", base64);
        }

        model.addAttribute("empresa", empresa);
        return "formularioempresas";
    }

    @GetMapping("/eliminar")
    public String eliminarEmpresa(@RequestParam("empresaID") Integer empresaID) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(empresaID);

        if (empresa == null || empresa.getServicio() == null) {
            return "redirect:/empresas";
        }

        Integer servicioID = empresa.getServicio().getId();
        empresaService.eliminarEmpresa(empresaID);

        return "redirect:/empresas?servicioID=" + servicioID;
    }

    @GetMapping("/mapa")
    public String abrirMapa(@RequestParam("servicioID") Long servicioID, Model model) {
        model.addAttribute("servicioID", servicioID);
        return "mapa";
    }

    @GetMapping("/api/empresas")
    public @ResponseBody List<EmpresaDTO> obtenerEmpresasPorServicioRest(@RequestParam("servicioID") Integer servicioID) {
        List<EmpresaVO> empresas = empresaService.obtenerEmpresasPorServicio(servicioID);

        return empresas.stream().map(emp -> {
            EmpresaDTO dto = new EmpresaDTO();
            dto.setId(emp.getId());
            dto.setNombre(emp.getNombre());
            dto.setDescripcion(emp.getDescripcion());
            dto.setTelefono(emp.getTelefono());
            dto.setEmail(emp.getEmail());
            dto.setPais(emp.getPais());
            dto.setCiudad(emp.getCiudad());
            dto.setDireccion(emp.getDireccion());
            dto.setSitioWeb(emp.getSitioWeb());
            dto.setActivo(emp.isActivo());
            dto.setNombreServicio(emp.getServicio() != null ? emp.getServicio().getNombre() : null);
            dto.setAceptaReservas(emp.isAceptaReservas());
            dto.setLatitud(emp.getLatitud());
            dto.setLongitud(emp.getLongitud());
            dto.setHoraApertura(emp.getHoraApertura());
            dto.setHoraCierre(emp.getHoraCierre());
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/empresa/{id}/calendario")
    public String mostrarCalendario(@PathVariable Integer id, Model model) {
        EmpresaVO empresa = empresaService.obtenerEmpresaPorId(id);
        model.addAttribute("empresa", empresa);
        return "calendario";
    }
}
