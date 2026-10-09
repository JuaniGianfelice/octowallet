package com.isft.octowallet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Usuario de la billetera (RF01 login, RF05 transferencias por alias).
 * @Entity hace que JPA cree y maneje la tabla "usuario" en MySQL.
 */
@Entity
public class Usuario {

    // @Id = clave primaria. IDENTITY = MySQL genera el número solo (AUTO_INCREMENT).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true: la base de datos no deja repetir el nombre de usuario.
    @Column(nullable = false, unique = true)
    private String username;

    // Acá se guarda el HASH de BCrypt, nunca la contraseña en texto plano.
    @Column(nullable = false)
    private String password;

    // Alias único: es lo que se usa para recibir transferencias (no usamos CVU).
    @Column(nullable = false, unique = true)
    private String alias;

    // JPA necesita un constructor vacío.
    public Usuario() {
    }

    public Usuario(String username, String password, String alias) {
        this.username = username;
        this.password = password;
        this.alias = alias;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getAlias() { return alias; }
}
