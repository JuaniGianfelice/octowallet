package com.isft.octowallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isft.octowallet.model.Usuario;

/**
 * Spring Data genera la implementación sola: solo con el nombre del método
 * (findByUsername) arma la consulta SQL "WHERE username = ?".
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByAlias(String alias);

    boolean existsByUsername(String username);

    boolean existsByAlias(String alias);
}
