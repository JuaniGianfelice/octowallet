package com.isft.octowallet.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.isft.octowallet.config.LoginInterceptor;
import com.isft.octowallet.model.Activo;
import com.isft.octowallet.service.OperacionException;
import com.isft.octowallet.service.TransferenciaService;
import com.isft.octowallet.service.UsuarioService;

@Controller
public class TransferenciaController {

    private final TransferenciaService transferenciaService;
    private final UsuarioService usuarioService;

    public TransferenciaController(TransferenciaService transferenciaService, UsuarioService usuarioService) {
        this.transferenciaService = transferenciaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/transferencia")
    public String formulario(Model model) {
        model.addAttribute("activos", Activo.values());
        return "transferencia";
    }

    @PostMapping("/transferencia")
    public String transferir(@SessionAttribute(LoginInterceptor.USUARIO_SESION) Long usuarioId,
                             @RequestParam String alias, @RequestParam Activo activo,
                             @RequestParam BigDecimal monto, Model model, RedirectAttributes redirect) {
        try {
            transferenciaService.transferir(usuarioService.buscarPorId(usuarioId), alias, activo, monto);
            redirect.addFlashAttribute("ok", "Transferencia enviada a " + alias + ".");
            return "redirect:/menu";
        } catch (OperacionException e) {
            // Si falla, se cancela y se muestra el motivo.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activos", Activo.values());
            return "transferencia";
        }
    }
}
