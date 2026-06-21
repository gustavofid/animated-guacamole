package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;

@Service
public class FinanceiroService {

    private final ImpostoService impostoService;
    private final DescontoService descontoService;

    @Autowired
    public FinanceiroService(ImpostoService impostoService, DescontoService descontoService) {
        this.impostoService = impostoService;
        this.descontoService = descontoService;
    }

    public double calculaPrecoBase(List<ItemPedido> itens) {
        return itens.stream().mapToDouble(i -> i.getValorUnitario() * i.getQuantidade()).sum();
    }

    public double calculaImposto(double precoBase) {
        return impostoService.calcular(precoBase);
    }

    public double calculaDesconto(String cpf, double precoBase) {
        return descontoService.calcular(cpf, precoBase);
    }
}
