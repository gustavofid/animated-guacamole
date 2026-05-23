package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@Component
public class EntregarPedidoUC {
    private PedidoRepository pedidoRepository;

    @Autowired
    public EntregarPedidoUC(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido run(long idPedido) {
        Pedido pedido = pedidoRepository.recuperaPorId(idPedido);

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        // Simplificação: permite entregar pedidos que estejam em TRANSPORTE ou PRONTO
        // ou mesmo APROVADO para fins de teste
        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.NEGADO) {
            throw new IllegalStateException("Pedido cancelado ou negado não pode ser entregue");
        }

        pedidoRepository.atualizaStatus(idPedido, StatusPedido.ENTREGUE);
        pedido.setStatus(StatusPedido.ENTREGUE);

        return pedido;
    }
}
