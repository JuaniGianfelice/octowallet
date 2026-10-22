package com.isft.octowallet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.isft.octowallet.config.LoginInterceptor;
import com.isft.octowallet.repository.MovimientoRepository;
import com.isft.octowallet.service.UsuarioService;

@Controller
public class HistorialController {

    private final MovimientoRepository movimientoRepository;
    private final UsuarioService usuarioService;

    public HistorialController(MovimientoRepository movimientoRepository, UsuarioService usuarioService) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/historial")
    public String historial(@SessionAttribute(LoginInterceptor.USUARIO_SESION) Long usuarioId, Model model) {
        model.addAttribute("movimientos",
                movimientoRepository.findByUsuarioOrderByFechaDesc(usuarioService.buscarPorId(usuarioId)));
        return "historial";
    }
}
