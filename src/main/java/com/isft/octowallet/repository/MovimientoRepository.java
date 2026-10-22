package com.isft.octowallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isft.octowallet.model.Movimiento;
import com.isft.octowallet.model.Usuario;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByUsuarioOrderByFechaDesc(Usuario usuario);
}
