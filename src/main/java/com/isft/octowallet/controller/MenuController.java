package com.isft.octowallet.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.isft.octowallet.config.LoginInterceptor;
import com.isft.octowallet.model.Activo;
import com.isft.octowallet.model.Cuenta;
import com.isft.octowallet.model.Usuario;
import com.isft.octowallet.repository.CuentaRepository;
import com.isft.octowallet.service.OperacionException;
import com.isft.octowallet.service.PrecioService;
import com.isft.octowallet.service.UsuarioService;

@Controller
public class MenuController {

    //Una fila de la tabla de saldos. valorArs queda null si CoinGecko no respondio
    public record FilaSaldo(Activo activo, BigDecimal saldo, BigDecimal valorArs) {}

    private final UsuarioService usuarioService;
    private final CuentaRepository cuentaRepository;
    private final PrecioService precioService;

    public MenuController(UsuarioService usuarioService, CuentaRepository cuentaRepository,
                          PrecioService precioService) {
        this.usuarioService = usuarioService;
        this.cuentaRepository = cuentaRepository;
        this.precioService = precioService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/menu";
    }

    // @SessionAttribute toma el id del usuario logueado directamente de la sesión.
    @GetMapping("/menu")
    public String menu(@SessionAttribute(LoginInterceptor.USUARIO_SESION) Long usuarioId, Model model) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        List<Cuenta> cuentas = cuentaRepository.findByUsuarioOrderByActivo(usuario);

        Map<Activo, BigDecimal> precios = null;
        try {
            precios = precioService.preciosEnArs();
        } catch (OperacionException e) {
            model.addAttribute("error", e.getMessage());
        }

        List<FilaSaldo> filas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Cuenta cuenta : cuentas) {
            BigDecimal valor = null;
            if (precios != null) {
                valor = cuenta.getSaldo().multiply(precios.get(cuenta.getActivo()));
                total = total.add(valor);
            }
            filas.add(new FilaSaldo(cuenta.getActivo(), cuenta.getSaldo(), valor));
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("filas", filas);
        model.addAttribute("total", precios != null ? total : null);
        return "menu";
    }
}
