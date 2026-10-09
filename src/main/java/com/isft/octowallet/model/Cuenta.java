package com.isft.octowallet.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Cuenta de un usuario para UN activo, con su saldo.
 * Cada usuario tiene una cuenta por activo (ARS, BTC, ETH, USDT).
 * La restricción única (usuario_id, activo) impide que tenga dos cuentas del mismo activo.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "activo"}))
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchas cuentas pertenecen a un usuario -> columna usuario_id (clave foránea).
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // STRING guarda "BTC" en la tabla en vez de un número (más legible y no se rompe si se reordena el enum).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Activo activo;

    // BigDecimal y no double: double tiene errores de redondeo que con plata no se aceptan.
    // scale = 8 porque las cripto usan hasta 8 decimales.
    @Column(nullable = false, precision = 30, scale = 8)
    private BigDecimal saldo;

    public Cuenta() {
    }

    public Cuenta(Usuario usuario, Activo activo, BigDecimal saldo) {
        this.usuario = usuario;
        this.activo = activo;
        this.saldo = saldo;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Activo getActivo() { return activo; }
    public BigDecimal getSaldo() { return saldo; }

    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
