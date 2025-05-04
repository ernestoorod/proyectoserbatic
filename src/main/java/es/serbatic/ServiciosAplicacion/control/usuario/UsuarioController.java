package es.serbatic.ServiciosAplicacion.control.usuario;

import java.util.Collections;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioDTO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.service.usuario.UsuarioServiceImplementacion;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImplementacion service;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @GetMapping("/registro")
    public String mostrarRegistro(
        @ModelAttribute("userInfo") UsuarioDTO user,
        @RequestParam(value = "redirect", required = false) String redirect,
        Model model
    ) {
        model.addAttribute("redirect", redirect);
        return "registro";
    }

    @PostMapping("/registrarUsuario")
    public String registrarUsuario(
        @ModelAttribute("userInfo") UsuarioDTO userDto,
        @RequestParam(value = "redirect", required = false) String redirect,
        Model model,
        HttpServletRequest request
    ) {
        if (service.usuarioExiste(userDto.getUser()) != null) {
            model.addAttribute("errorMessage", "Este nombre de usuario ya está en uso.");
            model.addAttribute("redirect", redirect);
            return "registro";
        }

        userDto.setRole("USER");

        UsuarioVO user = new UsuarioVO();
        user.setFullName(userDto.getFullName());
        user.setUser(userDto.getUser());
        user.setPass(userDto.getPass());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setRole(userDto.getRole());

        service.insertar(user);

        logger.info("Registro exitoso para usuario: {} desde IP: {}", user.getUser(), getClientIp(request));

        return "redirect:/login" + (redirect != null && !redirect.isBlank() ? "?redirect=" + redirect : "");
    }

    @GetMapping("/login")
    public String mostrarLogin(
        @ModelAttribute("userInfo") UsuarioDTO user,
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "redirect", required = false) String redirect,
        Model model
    ) {
        if (error != null) {
            model.addAttribute("errorMessage", "La cuenta no existe o las credenciales son incorrectas.");
        }
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/accesoUsuario")
    public String accesoUsuario(
        @ModelAttribute("userInfo") UsuarioDTO user,
        @RequestParam(value = "redirect", required = false) String redirect,
        HttpSession session,
        Model model,
        HttpServletRequest request
    ) {
        UsuarioVO usuarioExiste = service.autenticar(user.getUser(), user.getPass());
        String ip = getClientIp(request);

        if (usuarioExiste != null) {
            logger.info("Inicio de sesión exitoso para usuario: {} desde IP: {}", user.getUser(), ip);
            session.setAttribute("usuarioExiste", usuarioExiste);
            return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/";
        }

        logger.warn("Intento fallido de login para usuario: {} desde IP: {}", user.getUser(), ip);
        model.addAttribute("errorMessage", "La cuenta no existe o las credenciales son incorrectas.");
        model.addAttribute("userInfo", user);
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(
        HttpSession session,
        HttpServletRequest request,
        @RequestParam(value = "redirect", required = false) String redirect
    ) {
        UsuarioVO actual = (UsuarioVO) session.getAttribute("usuarioExiste");
        if (actual != null) {
            logger.info("Cierre de sesión de usuario: {} desde IP: {}", actual.getUser(), getClientIp(request));
        }
        session.invalidate();
        return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/";
    }

    @GetMapping("/perfil")
    public String verPerfil(
        HttpSession session,
        Model model,
        HttpServletRequest request,
        @RequestParam(value = "redirect", required = false) String redirect
    ) {
        UsuarioVO usuario = (UsuarioVO) session.getAttribute("usuarioExiste");
        if (usuario == null) {
            return "redirect:/login";
        }

        if (redirect != null && !redirect.isBlank()) {
            session.setAttribute("redirectPerfil", redirect);
        } else if (session.getAttribute("redirectPerfil") == null) {
            String referer = request.getHeader("Referer");
            if (referer != null && referer.contains("/empresas")) {
                session.setAttribute("redirectPerfil", "/empresas");
            } else {
                session.setAttribute("redirectPerfil", "/");
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("volverUrl", session.getAttribute("redirectPerfil"));

        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
        @ModelAttribute UsuarioVO usuarioActualizado,
        @RequestParam("passActual") String passActual,
        @RequestParam(value = "nuevaPass", required = false) String nuevaPass,
        @RequestParam(value = "repetirPass", required = false) String repetirPass,
        HttpSession session,
        Model model
    ) {
        UsuarioVO actual = (UsuarioVO) session.getAttribute("usuarioExiste");

        if (!BCrypt.checkpw(passActual, actual.getPass())) {
            model.addAttribute("error", "La contraseña actual no es válida.");
            model.addAttribute("usuario", actual);
            return "perfil";
        }

        UsuarioVO existente = service.usuarioExiste(usuarioActualizado.getUser());
        if (existente != null && existente.getId() != actual.getId()) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            model.addAttribute("usuario", actual);
            return "perfil";
        }

        if (nuevaPass != null && !nuevaPass.isBlank()) {
            if (!nuevaPass.equals(repetirPass)) {
                model.addAttribute("error", "Las nuevas contraseñas no coinciden.");
                model.addAttribute("usuario", actual);
                return "perfil";
            }
            usuarioActualizado.setPass(BCrypt.hashpw(nuevaPass, BCrypt.gensalt()));
        } else {
            usuarioActualizado.setPass(actual.getPass());
        }

        usuarioActualizado.setId(actual.getId());
        usuarioActualizado.setRole(actual.getRole());

        service.actualizar(usuarioActualizado);
        session.setAttribute("usuarioExiste", usuarioActualizado);

        return "redirect:/";
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarPerfil(HttpSession session) {
        UsuarioVO actual = (UsuarioVO) session.getAttribute("usuarioExiste");
        if (actual != null) {
            logger.info("Usuario eliminado: {}", actual.getUser());
            service.eliminar(actual.getId());
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        UsuarioVO admin = (UsuarioVO) session.getAttribute("usuarioExiste");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/perfil";
        }

        model.addAttribute("usuarios", service.obtenerTodos());
        model.addAttribute("volverUrl", session.getAttribute("redirectPerfil"));
        return "gestionarUsuarios";
    }

    @PostMapping("/admin/cambiarRol")
    public String cambiarRolDesdeAdmin(
        @RequestParam("id") Long id,
        @RequestParam("rol") String rol,
        HttpSession session
    ) {
        UsuarioVO admin = (UsuarioVO) session.getAttribute("usuarioExiste");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/perfil";
        }

        if (rol.equalsIgnoreCase("USER") || rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("JEFE_EMPRESA")) {
            UsuarioVO usuario = service.buscarPorId(id);
            if (usuario != null) {
                usuario.setRole(rol.toUpperCase());
                service.actualizar(usuario);
                if (usuario.getId().equals(admin.getId())) {
                    session.setAttribute("usuarioExiste", usuario);
                }
            }
        }

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/perfil/verificar-pass")
    @ResponseBody
    public Map<String, Boolean> verificarPass(
        @RequestBody Map<String, String> request,
        HttpSession session
    ) {
        String passActual = request.get("passActual");
        UsuarioVO actual = (UsuarioVO) session.getAttribute("usuarioExiste");
        boolean correcto = actual != null && BCrypt.checkpw(passActual, actual.getPass());
        return Collections.singletonMap("correcto", correcto);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}
