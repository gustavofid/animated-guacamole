package com.pizzaria.entrega.servico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EntregaService {

    private static final Logger log = LoggerFactory.getLogger(EntregaService.class);

    private final RestTemplate restTemplate;

    public EntregaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void processarEntrega(Long pedidoId) {
        log.info("Iniciando entrega do pedido {}", pedidoId);

        try {
            // Simula o tempo de transporte
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Notifica o monolito que o pedido foi entregue
        String url = "http://servico-pizzaria/pedido/" + pedidoId + "/entregar";
        restTemplate.patchForObject(url, null, Void.class);

        log.info("Pedido {} entregue com sucesso", pedidoId);
    }
}
