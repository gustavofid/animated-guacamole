package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;

@Component
public class PagarPedidoUC {
    private PedidoService pedidoService;

    public PagarPedidoUC(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public Pedido run(long idPedido) {
        return pedidoService.pagarPedido(idPedido);
    }
}

