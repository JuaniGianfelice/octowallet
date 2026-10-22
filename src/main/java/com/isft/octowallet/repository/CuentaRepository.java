package com.isft.octowallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isft.octowallet.model.Activo;
import com.isft.octowallet.model.Cuenta;
import com.isft.octowallet.model.Usuario;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByUsuarioOrderByActivo(Usuario usuario);

    Optional<Cuenta> findByUsuarioAndActivo(Usuario usuario, Activo activo);
}
