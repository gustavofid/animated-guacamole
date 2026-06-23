package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Config.RabbitMQConfig;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@Service
public class EntregaService implements IEntregaService {

    private final RabbitTemplate rabbitTemplate;
    private final PedidoRepository pedidoRepository;

    public EntregaService(RabbitTemplate rabbitTemplate, PedidoRepository pedidoRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void chegadaDePedido(Pedido pedido) {
        pedidoRepository.atualizaStatus(pedido.getId(), StatusPedido.TRANSPORTE);
        pedido.setStatus(StatusPedido.TRANSPORTE);
        // publica o ID do pedido na fila — o servico-entrega consome e processa
        rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_ENTREGAS, pedido.getId());
    }

    @Override
    public void pedidoEntregue() {
        // não utilizado: a confirmação vem do servico-entrega via PATCH /pedido/{id}/entregar
    }
}
