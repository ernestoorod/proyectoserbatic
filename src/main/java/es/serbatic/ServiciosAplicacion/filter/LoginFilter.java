package es.serbatic.ServiciosAplicacion.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/accesoUsuario");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String username = request.getParameter("user");

        if ("bloqueado".equalsIgnoreCase(username)) {
            System.out.println("[LOGIN BLOQUEADO] Intento de acceso con el usuario bloqueado: " + username);
            response.sendRedirect("/login?error=usuario_bloqueado");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
