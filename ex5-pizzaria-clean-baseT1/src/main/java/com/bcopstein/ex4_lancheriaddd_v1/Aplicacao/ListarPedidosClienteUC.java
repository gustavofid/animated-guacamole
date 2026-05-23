package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.PedidoClienteDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;

@Component
public class ListarPedidosClienteUC {
    private PedidoRepository pedidoRepository;

    @Autowired
    public ListarPedidosClienteUC(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidoClienteDTO> run(String cpf, LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.recuperaPorClienteNoPeriodo(cpf, inicio, fim)
                .stream()
                .map(p -> new PedidoClienteDTO(
                        p.getId(),
                        p.getStatus().name(),
                        p.getDataCriacao(),
                        p.getValorCobrado()))
                .collect(Collectors.toList());
    }
}
