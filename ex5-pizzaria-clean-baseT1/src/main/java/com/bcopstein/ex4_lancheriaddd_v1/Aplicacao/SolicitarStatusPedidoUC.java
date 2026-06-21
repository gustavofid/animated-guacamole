package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import org.springframework.stereotype.Component;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;

@Component
public class SolicitarStatusPedidoUC {
    private PedidoService pedidoService;

    public SolicitarStatusPedidoUC(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public StatusPedido run(long idPedido) {
        return pedidoService.consultaStatus(idPedido);
    }
}