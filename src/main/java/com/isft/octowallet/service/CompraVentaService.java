package com.isft.octowallet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isft.octowallet.model.Activo;
import com.isft.octowallet.model.Cuenta;
import com.isft.octowallet.model.Movimiento;
import com.isft.octowallet.model.Usuario;
import com.isft.octowallet.repository.CuentaRepository;
import com.isft.octowallet.repository.MovimientoRepository;

@Service
public class CompraVentaService {

    private final PrecioService precioService;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public CompraVentaService(PrecioService precioService, CuentaRepository cuentaRepository,
                              MovimientoRepository movimientoRepository) {
        this.precioService = precioService;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }


    public Cotizacion cotizar(Usuario usuario, Activo origen, Activo destino, BigDecimal monto) {
        if (origen == destino) {
            throw new OperacionException("Elegí dos activos distintos.");
        }
        if (monto == null || monto.signum() <= 0) {
            throw new OperacionException("El monto tiene que ser mayor a 0.");
        }
        validarSaldo(buscarCuenta(usuario, origen), monto);

        Map<Activo, BigDecimal> precios = precioService.preciosEnArs();
        BigDecimal precioOrigen = precios.get(origen);
        BigDecimal precioDestino = precios.get(destino);
        BigDecimal montoDestino = monto.multiply(precioOrigen)
                .divide(precioDestino, 8, RoundingMode.HALF_DOWN);

        return new Cotizacion(origen, destino, monto, precioOrigen, precioDestino, montoDestino, Instant.now());
    }


    @Transactional
    public void confirmar(Usuario usuario, Cotizacion cotizacion) {
        Cuenta cuentaOrigen = buscarCuenta(usuario, cotizacion.origen());
        Cuenta cuentaDestino = buscarCuenta(usuario, cotizacion.destino());

        // Se vuelve a validar: el saldo pudo cambiar mientras el usuario miraba el resumen.
        validarSaldo(cuentaOrigen, cotizacion.montoOrigen());

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(cotizacion.montoOrigen()));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(cotizacion.montoDestino()));
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        movimientoRepository.save(Movimiento.compraVenta(usuario,
                cotizacion.origen(), cotizacion.montoOrigen(),
                cotizacion.destino(), cotizacion.montoDestino()));
    }

    private Cuenta buscarCuenta(Usuario usuario, Activo activo) {
        return cuentaRepository.findByUsuarioAndActivo(usuario, activo)
                .orElseThrow(() -> new OperacionException("No tenés cuenta en " + activo + "."));
    }

    private void validarSaldo(Cuenta cuenta, BigDecimal monto) {
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new OperacionException("Saldo insuficiente en " + cuenta.getActivo() + ".");
        }
    }
}
