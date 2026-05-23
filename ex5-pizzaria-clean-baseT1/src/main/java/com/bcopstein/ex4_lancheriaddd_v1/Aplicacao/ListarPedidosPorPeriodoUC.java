package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidosPorPeriodo;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;

@Component
public class ListarPedidosPorPeriodoUC {
    private PedidoRepository pedidoRepository;

    @Autowired
    public ListarPedidosPorPeriodoUC(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidosPorPeriodo.DTO> run(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.recuperaEntreguesNoPeriodo(inicio, fim)
                .stream()
                .map(p -> new PedidosPorPeriodo.DTO(
                        p.getId(),
                        p.getCliente().getNome(),
                        p.getDataEntrega(),
                        p.getValorCobrado()))
                .collect(Collectors.toList());
    }
}
