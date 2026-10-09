package com.isft.octowallet.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isft.octowallet.model.Activo;
import com.isft.octowallet.model.Cuenta;
import com.isft.octowallet.model.Movimiento;
import com.isft.octowallet.model.TipoMovimiento;
import com.isft.octowallet.model.Usuario;
import com.isft.octowallet.repository.CuentaRepository;
import com.isft.octowallet.repository.MovimientoRepository;
import com.isft.octowallet.repository.UsuarioRepository;

/** Transferencias por alias (RF05 y RF06). */
@Service
public class TransferenciaService {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public TransferenciaService(UsuarioRepository usuarioRepository, CuentaRepository cuentaRepository,
                                MovimientoRepository movimientoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Valida y ejecuta la transferencia. El dinero va a la cuenta del destinatario
     * del MISMO activo que se envía (BTC -> cuenta BTC del otro).
     */
    @Transactional
    public void transferir(Usuario origen, String aliasDestino, Activo activo, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new OperacionException("El monto tiene que ser mayor a 0.");
        }

        // RF06: verificar que la cuenta destino exista.
        Usuario destino = usuarioRepository.findByAlias(aliasDestino.trim())
                .orElseThrow(() -> new OperacionException("No existe ningún usuario con el alias \"" + aliasDestino + "\"."));
        if (destino.getId().equals(origen.getId())) {
            throw new OperacionException("No podés transferirte a vos mismo.");
        }

        Cuenta cuentaOrigen = cuentaRepository.findByUsuarioAndActivo(origen, activo)
                .orElseThrow(() -> new OperacionException("No tenés cuenta en " + activo + "."));
        Cuenta cuentaDestino = cuentaRepository.findByUsuarioAndActivo(destino, activo)
                .orElseThrow(() -> new OperacionException("El destinatario no tiene cuenta en " + activo + "."));

        // RF05: verificar disponibilidad de fondos.
        if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
            throw new OperacionException("Saldo insuficiente en " + activo + ".");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(monto));
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        // Un movimiento para cada uno: el que envía lo ve como "enviada" y el otro como "recibida".
        movimientoRepository.save(Movimiento.transferencia(origen, TipoMovimiento.TRANSFERENCIA_ENVIADA,
                activo, monto, destino.getAlias()));
        movimientoRepository.save(Movimiento.transferencia(destino, TipoMovimiento.TRANSFERENCIA_RECIBIDA,
                activo, monto, origen.getAlias()));
    }
}
