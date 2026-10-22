package com.isft.octowallet.controller;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.isft.octowallet.config.LoginInterceptor;
import com.isft.octowallet.model.Activo;
import com.isft.octowallet.model.Usuario;
import com.isft.octowallet.service.CompraVentaService;
import com.isft.octowallet.service.Cotizacion;
import com.isft.octowallet.service.OperacionException;
import com.isft.octowallet.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CompraVentaController {

    // Clave de la sesión donde queda guardada la cotización congelada.
    private static final String COTIZACION_SESION = "cotizacion";

    private final CompraVentaService compraVentaService;
    private final UsuarioService usuarioService;

    public CompraVentaController(CompraVentaService compraVentaService, UsuarioService usuarioService) {
        this.compraVentaService = compraVentaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/compra-venta")
    public String formulario(Model model) {
        model.addAttribute("activos", Activo.values());
        return "compra-venta";
    }

    @PostMapping("/compra-venta/cotizar")
    public String cotizar(@SessionAttribute(LoginInterceptor.USUARIO_SESION) Long usuarioId,
                          @RequestParam Activo origen, @RequestParam Activo destino,
                          @RequestParam BigDecimal monto, HttpSession session, Model model) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        try {
            Cotizacion cotizacion = compraVentaService.cotizar(usuario, origen, destino, monto);
            session.setAttribute(COTIZACION_SESION, cotizacion);
            return mostrarResumen(cotizacion, model);
        } catch (OperacionException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activos", Activo.values());
            return "compra-venta";
        }
    }

    @PostMapping("/compra-venta/confirmar")
    public String confirmar(@SessionAttribute(LoginInterceptor.USUARIO_SESION) Long usuarioId,
                            HttpSession session, Model model, RedirectAttributes redirect) {
        Cotizacion cotizacion = (Cotizacion) session.getAttribute(COTIZACION_SESION);
        if (cotizacion == null) {
            return "redirect:/compra-venta";
        }
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        try {
            //Si pasaron los 5 minutos, se recalcula con precios nuevos y se vuelve a mostrar el resumen.
            if (cotizacion.vencida()) {
                Cotizacion nueva = compraVentaService.cotizar(usuario,
                        cotizacion.origen(), cotizacion.destino(), cotizacion.montoOrigen());
                session.setAttribute(COTIZACION_SESION, nueva);
                model.addAttribute("aviso", "La cotización venció y se recalculó con los precios actuales. Revisá y confirmá de nuevo.");
                return mostrarResumen(nueva, model);
            }
            compraVentaService.confirmar(usuario, cotizacion);
            session.removeAttribute(COTIZACION_SESION);
            // Flash attribute: el mensaje sobrevive al redirect y se muestra una sola vez en el menú.
            redirect.addFlashAttribute("ok", "Operación realizada con éxito.");
            return "redirect:/menu";
        } catch (OperacionException e) {
            session.removeAttribute(COTIZACION_SESION);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activos", Activo.values());
            return "compra-venta";
        }
    }

    //Si el usuario no confirma, vuelve al menú principal.
    @PostMapping("/compra-venta/cancelar")
    public String cancelar(HttpSession session) {
        session.removeAttribute(COTIZACION_SESION);
        return "redirect:/menu";
    }

    private String mostrarResumen(Cotizacion cotizacion, Model model) {
        LocalTime vence = LocalTime.ofInstant(cotizacion.venceA(), ZoneId.systemDefault());
        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("venceA", vence.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return "resumen-compra-venta";
    }
}
