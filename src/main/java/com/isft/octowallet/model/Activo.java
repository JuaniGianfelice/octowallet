package com.isft.octowallet.model;

/**
 * Activos que maneja la billetera.
 * Es un enum (y no una tabla) porque la lista es fija y no cambia en tiempo de ejecución.
 * Cada activo guarda el id que usa CoinGecko para buscar su precio.
 * ARS no tiene id porque es la moneda base: su precio en ARS siempre es 1.
 */
public enum Activo {
    ARS(null),
    BTC("bitcoin"),
    ETH("ethereum"),
    USDT("tether");

    private final String coinGeckoId;

    Activo(String coinGeckoId) {
        this.coinGeckoId = coinGeckoId;
    }

    public String getCoinGeckoId() {
        return coinGeckoId;
    }

    /** true si el activo es una cripto (tiene precio e histórico en CoinGecko). */
    public boolean esCripto() {
        return coinGeckoId != null;
    }
}
