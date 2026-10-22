package com.isft.octowallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isft.octowallet.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByAlias(String alias);

    boolean existsByUsername(String username);

    boolean existsByAlias(String alias);
}
