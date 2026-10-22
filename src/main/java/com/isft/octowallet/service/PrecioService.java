package com.isft.octowallet.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.isft.octowallet.model.Activo;


@Service
public class PrecioService {

    // RestClient es el cliente HTTP que trae Spring. Todas las URLs arrancan con esta base.
    private final RestClient restClient = RestClient.create("https://api.coingecko.com/api/v3");

    // Caché de 60 segundos: la API gratuita limita la cantidad de pedidos por minuto,
    // y sin esto cada recarga del menú haría un pedido nuevo.
    private Map<Activo, BigDecimal> cache;
    private Instant cacheHora;

    /** Precio actual de cada activo expresado en ARS. ARS siempre vale 1. */
    public synchronized Map<Activo, BigDecimal> preciosEnArs() {
        if (cache != null && cacheHora.plusSeconds(60).isAfter(Instant.now())) {
            return cache;
        }
        try {
            // Respuesta de ejemplo: {"bitcoin":{"ars":123456.7},"ethereum":{"ars":...},"tether":{"ars":...}}
            Map<String, Map<String, BigDecimal>> respuesta = restClient.get()
                    .uri("/simple/price?ids=bitcoin,ethereum,tether&vs_currencies=ars")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Map<String, BigDecimal>>>() {});

            Map<Activo, BigDecimal> precios = new EnumMap<>(Activo.class);
            for (Activo activo : Activo.values()) {
                if (activo.esCripto()) {
                    precios.put(activo, respuesta.get(activo.getCoinGeckoId()).get("ars"));
                } else {
                    precios.put(activo, BigDecimal.ONE);
                }
            }
            cache = precios;
            cacheHora = Instant.now();
            return precios;
        } catch (RestClientException | NullPointerException e) {
            throw new OperacionException("No se pudieron obtener las cotizaciones de CoinGecko. Probá de nuevo en un minuto.");
        }
    }

    /**
     * Histórico de precios en ARS (RF08).
     * @param dias "1", "7", "30", "365" o "max" (como lo pide CoinGecko).
     * @return lista de pares [timestamp en milisegundos, precio].
     */
    public List<List<Double>> historico(Activo activo, String dias) {
        try {
            // Respuesta de ejemplo: {"prices":[[1700000000000, 123.4], ...], ...}
            Map<String, List<List<Double>>> respuesta = restClient.get()
                    .uri("/coins/{id}/market_chart?vs_currency=ars&days={dias}", activo.getCoinGeckoId(), dias)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, List<List<Double>>>>() {});
            return respuesta.get("prices");
        } catch (RestClientException | NullPointerException e) {
            throw new OperacionException("No se pudo obtener el histórico de CoinGecko para ese rango.");
        }
    }
}
