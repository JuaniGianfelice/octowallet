package com.isft.octowallet.service;

/**
 * Error de negocio (saldo insuficiente, alias inexistente, etc.).
 * El mensaje es el que se le muestra al usuario en pantalla.
 */
public class OperacionException extends RuntimeException {
    public OperacionException(String mensaje) {
        super(mensaje);
    }
}
