package com.isft.octowallet.model;


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
