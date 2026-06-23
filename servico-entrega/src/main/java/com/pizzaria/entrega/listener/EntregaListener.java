package com.pizzaria.entrega.listener;

import com.pizzaria.entrega.config.RabbitMQConfig;
import com.pizzaria.entrega.servico.EntregaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class EntregaListener {

    private static final Logger log = LoggerFactory.getLogger(EntregaListener.class);

    private final EntregaService entregaService;

    public EntregaListener(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_ENTREGAS)
    public void receberPedido(Long pedidoId) {
        log.info("[instância {}] consumiu pedido {} da fila", hostname(), pedidoId);
        entregaService.processarEntrega(pedidoId);
    }

    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "desconhecido";
        }
    }
}
