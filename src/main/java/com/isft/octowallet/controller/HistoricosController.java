package com.isft.octowallet.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.isft.octowallet.model.Activo;
import com.isft.octowallet.service.OperacionException;
import com.isft.octowallet.service.PrecioService;

@Controller
public class HistoricosController {

    // Valor del parámetro "days" de CoinGecko.
    private static final Map<String, String> RANGOS = Map.of(
            "dia", "1",
            "semana", "7",
            "mes", "30",
            "anio", "365",
            "inicio", "max");

    private final PrecioService precioService;

    public HistoricosController(PrecioService precioService) {
        this.precioService = precioService;
    }

    @GetMapping("/historicos")
    public String historicos(@RequestParam(defaultValue = "BTC") Activo activo,
                             @RequestParam(defaultValue = "semana") String rango, Model model) {
        List<Activo> criptos = new ArrayList<>();
        for (Activo a : Activo.values()) {
            if (a.esCripto()) criptos.add(a);
        }
        model.addAttribute("criptos", criptos);
        model.addAttribute("activo", activo);
        model.addAttribute("rango", rango);

        if (!activo.esCripto() || !RANGOS.containsKey(rango)) {
            model.addAttribute("error", "Elegí una cripto y un rango válido.");
            return "historicos";
        }

        try {
            // En el rango "día" se muestra la hora - en el resto, la fecha.
            DateTimeFormatter formato = DateTimeFormatter.ofPattern(rango.equals("dia") ? "HH:mm" : "dd/MM/yyyy");
            List<String> etiquetas = new ArrayList<>();
            List<Double> valores = new ArrayList<>();
            for (List<Double> punto : precioService.historico(activo, RANGOS.get(rango))) {
                LocalDateTime fecha = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(punto.get(0).longValue()), ZoneId.systemDefault());
                etiquetas.add(fecha.format(formato));
                valores.add(punto.get(1));
            }
            model.addAttribute("etiquetas", etiquetas);
            model.addAttribute("valores", valores);
        } catch (OperacionException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "historicos";
    }
}
