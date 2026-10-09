package com.isft.octowallet.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import com.isft.octowallet.model.Activo;

/**
 * Cotización "congelada" de una compra/venta (RF04).
 * Se guarda en la sesión del usuario mientras ve el resumen.
 * record = clase inmutable de solo datos (Java genera constructor y getters: origen(), destino(), etc.).
 */
public record Cotizacion(
        Activo origen,
        Activo destino,
        BigDecimal montoOrigen,
        BigDecimal precioOrigen,   // precio en ARS del activo que se vende
        BigDecimal precioDestino,  // precio en ARS del activo que se compra
        BigDecimal montoDestino,   // lo que va a recibir el usuario
        Instant creada) {

    // RF04: el valor queda congelado 5 minutos.
    public static final Duration DURACION = Duration.ofMinutes(5);

    public boolean vencida() {
        return Instant.now().isAfter(creada.plus(DURACION));
    }

    public Instant venceA() {
        return creada.plus(DURACION);
    }
}
