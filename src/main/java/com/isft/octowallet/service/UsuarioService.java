package com.isft.octowallet.service;

import java.math.BigDecimal;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isft.octowallet.model.Activo;
import com.isft.octowallet.model.Cuenta;
import com.isft.octowallet.model.Usuario;
import com.isft.octowallet.repository.CuentaRepository;
import com.isft.octowallet.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final BigDecimal SALDO_INICIAL_ARS = new BigDecimal("1000000");

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Inyección por constructor: Spring pasa los repositorios automáticamente.
    public UsuarioService(UsuarioRepository usuarioRepository, CuentaRepository cuentaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cuentaRepository = cuentaRepository;
    }


    @Transactional
    public Usuario registrar(String username, String password, String alias) {
        if (username.isBlank() || password.isBlank() || alias.isBlank()) {
            throw new OperacionException("Completá todos los campos.");
        }
        if (usuarioRepository.existsByUsername(username)) {
            throw new OperacionException("Ese nombre de usuario ya existe.");
        }
        if (usuarioRepository.existsByAlias(alias)) {
            throw new OperacionException("Ese alias ya está en uso.");
        }

        Usuario usuario = usuarioRepository.save(
                new Usuario(username, passwordEncoder.encode(password), alias));

        for (Activo activo : Activo.values()) {
            BigDecimal saldo = (activo == Activo.ARS) ? SALDO_INICIAL_ARS : BigDecimal.ZERO;
            cuentaRepository.save(new Cuenta(usuario, activo, saldo));
        }
        return usuario;
    }

    /** Devuelve el usuario si usuario y contraseña son correctos; si no, lanza error. */
    public Usuario login(String username, String password) {
        // Mismo mensaje para "no existe" y "contraseña mal": así no se revela qué usuarios existen.
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new OperacionException("Usuario o contraseña incorrectos."));
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new OperacionException("Usuario o contraseña incorrectos.");
        }
        return usuario;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new OperacionException("Usuario no encontrado."));
    }
}
