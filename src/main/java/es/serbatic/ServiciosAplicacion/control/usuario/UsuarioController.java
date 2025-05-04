package es.serbatic.ServiciosAplicacion.control.usuario;

import java.util.Collections;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioDTO;
import es.serbatic.ServiciosAplicacion.model.usuario.UsuarioVO;
import es.serbatic.ServiciosAplicacion.service.usuario.UsuarioServiceImplementacion;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioServiceImplementacion service;

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
        Model model
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
        Model model
    ) {
        UsuarioVO usuarioExiste = service.autenticar(user.getUser(), user.getPass());
        if (usuarioExiste != null) {
            session.setAttribute("usuarioExiste", usuarioExiste);
            return redirect != null && !redirect.isBlank() ? "redirect:" + redirect : "redirect:/";
        }

        model.addAttribute("errorMessage", "La cuenta no existe o las credenciales son incorrectas.");
        model.addAttribute("userInfo", user);
        model.addAttribute("redirect", redirect);
        return "login";
    }


    @GetMapping("/logout")
    public String logout(
        HttpSession session,
        @RequestParam(value = "redirect", required = false) String redirect
    ) {
        session.invalidate();
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }
        return "redirect:/";
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

        // Guardamos en sesión el redirect al entrar a perfil
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

        // Permitimos USER, ADMIN y JEFE_EMPRESA
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
}
