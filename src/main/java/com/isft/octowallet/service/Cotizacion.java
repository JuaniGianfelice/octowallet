package com.isft.octowallet.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import com.isft.octowallet.model.Activo;


public record Cotizacion(
        Activo origen,
        Activo destino,
        BigDecimal montoOrigen,
        BigDecimal precioOrigen,  
        BigDecimal precioDestino, 
        BigDecimal montoDestino,   
        Instant creada) {

    public static final Duration DURACION = Duration.ofMinutes(5);

    public boolean vencida() {
        return Instant.now().isAfter(creada.plus(DURACION));
    }

    public Instant venceA() {
        return creada.plus(DURACION);
    }
}
