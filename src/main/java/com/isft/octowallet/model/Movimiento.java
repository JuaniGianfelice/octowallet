package com.isft.octowallet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Registro de cada operación para el historial (RF07).
 * Una sola tabla para los tres tipos:
 *  - COMPRA_VENTA: usa origen y destino (vendí X de A, recibí Y de B).
 *  - TRANSFERENCIA_ENVIADA / RECIBIDA: usa solo origen (activo y monto) + alias de la otra persona.
 */
@Entity
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dueño del movimiento: es en el historial de quién aparece.
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Activo activoOrigen;

    @Column(nullable = false, precision = 30, scale = 8)
    private BigDecimal montoOrigen;

    // Solo para COMPRA_VENTA (en transferencias queda null).
    @Enumerated(EnumType.STRING)
    private Activo activoDestino;

    @Column(precision = 30, scale = 8)
    private BigDecimal montoDestino;

    // Solo para transferencias: alias de quien envió o recibió.
    private String aliasContraparte;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Movimiento() {
    }

    /** Constructor para compra/venta. */
    public static Movimiento compraVenta(Usuario usuario, Activo origen, BigDecimal montoOrigen,
                                         Activo destino, BigDecimal montoDestino) {
        Movimiento m = new Movimiento();
        m.usuario = usuario;
        m.tipo = TipoMovimiento.COMPRA_VENTA;
        m.activoOrigen = origen;
        m.montoOrigen = montoOrigen;
        m.activoDestino = destino;
        m.montoDestino = montoDestino;
        m.fecha = LocalDateTime.now();
        return m;
    }

    /** Constructor para transferencias (enviada o recibida). */
    public static Movimiento transferencia(Usuario usuario, TipoMovimiento tipo, Activo activo,
                                           BigDecimal monto, String aliasContraparte) {
        Movimiento m = new Movimiento();
        m.usuario = usuario;
        m.tipo = tipo;
        m.activoOrigen = activo;
        m.montoOrigen = monto;
        m.aliasContraparte = aliasContraparte;
        m.fecha = LocalDateTime.now();
        return m;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public TipoMovimiento getTipo() { return tipo; }
    public Activo getActivoOrigen() { return activoOrigen; }
    public BigDecimal getMontoOrigen() { return montoOrigen; }
    public Activo getActivoDestino() { return activoDestino; }
    public BigDecimal getMontoDestino() { return montoDestino; }
    public String getAliasContraparte() { return aliasContraparte; }
    public LocalDateTime getFecha() { return fecha; }
}
