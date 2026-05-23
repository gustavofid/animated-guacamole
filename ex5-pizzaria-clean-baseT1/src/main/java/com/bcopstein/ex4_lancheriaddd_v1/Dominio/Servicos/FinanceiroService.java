package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Service
public class FinanceiroService {
    private PedidoRepository pedidoRepository;

    @Autowired
    public FinanceiroService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public double calculaPrecoBase(List<ItemPedido> itens) {
        return itens.stream().mapToDouble(i -> i.getValorUnitario() * i.getQuantidade()).sum();
    }

    public double calculaImposto(double precoBase) {
        return precoBase * 0.10;
    }

    public double calculaDesconto(String cpf, double precoBase) {
        LocalDateTime vinteDiasAtras = LocalDateTime.now().minusDays(20);
        List<Pedido> pedidosRecentes = pedidoRepository.recuperaPorClienteNoPeriodo(cpf, vinteDiasAtras, LocalDateTime.now());
        if (pedidosRecentes.size() > 3) {
            return precoBase * 0.07;
        }
        return 0.0;
    }
}
