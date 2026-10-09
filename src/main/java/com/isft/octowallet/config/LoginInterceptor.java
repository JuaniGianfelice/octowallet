package com.isft.octowallet.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Se ejecuta ANTES de cada controlador. Si no hay usuario logueado en la sesión,
 * redirige al login. Así no hace falta chequear el login en cada pantalla.
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    // Nombre de la clave donde se guarda el id del usuario logueado en la sesión.
    public static final String USUARIO_SESION = "usuarioId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false); // false = no crear una sesión nueva
        if (session == null || session.getAttribute(USUARIO_SESION) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false; // corta acá: no llega al controlador
        }
        return true;
    }
}
