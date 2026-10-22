package com.isft.octowallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.isft.octowallet.config.LoginInterceptor;
import com.isft.octowallet.model.Usuario;
import com.isft.octowallet.service.OperacionException;
import com.isft.octowallet.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        try {
            Usuario usuario = usuarioService.login(username, password);
            // Guardamos solo el id en la sesión: es lo que usa el interceptor para saber que está logueado.
            session.setAttribute(LoginInterceptor.USUARIO_SESION, usuario.getId());
            return "redirect:/menu";
        } catch (OperacionException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String username, @RequestParam String password,
                           @RequestParam String alias, Model model) {
        try {
            usuarioService.registrar(username.trim(), password, alias.trim());
            model.addAttribute("ok", "Cuenta creada. Ya podés iniciar sesión.");
            return "login";
        } catch (OperacionException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // borra todo lo de la sesión (usuario y cotización)
        return "redirect:/login";
    }
}
