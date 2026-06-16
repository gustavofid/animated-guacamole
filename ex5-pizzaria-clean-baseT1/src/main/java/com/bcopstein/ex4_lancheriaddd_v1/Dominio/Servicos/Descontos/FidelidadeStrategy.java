package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

// 7% de desconto para clientes com mais de 3 pedidos nos últimos 20 dias
@Component
public class FidelidadeStrategy implements IDescontoStrategy {

    private final PedidoRepository pedidoRepository;

    @Autowired
    public FidelidadeStrategy(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public double calcular(String cpf, double precoBase) {
        LocalDateTime vinteDiasAtras = LocalDateTime.now().minusDays(20);
        List<Pedido> pedidosRecentes = pedidoRepository.recuperaPorClienteNoPeriodo(cpf, vinteDiasAtras, LocalDateTime.now());
        if (pedidosRecentes.size() > 3) {
            return precoBase * 0.07;
        }
        return 0.0;
    }

    @Override
    public String getCodigo() {
        return "Fidelidade";
    }
}
