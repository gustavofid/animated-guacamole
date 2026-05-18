package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Ingrediente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@Service
public class PedidoService {
    private PedidoRepository pedidoRepository;
    private EstoqueService estoqueService;
    private FinanceiroService financeiroService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, 
                         EstoqueService estoqueService,
                         FinanceiroService financeiroService) {
        this.pedidoRepository = pedidoRepository;
        this.estoqueService = estoqueService;
        this.financeiroService = financeiroService;
    }

    public Map<Long, Integer> calculaIngredientesNecessarios(List<ItemPedido> itens) {
        Map<Long, Integer> ingredientesNecessarios = new HashMap<>();
        for (ItemPedido item : itens) {
            for (Ingrediente ing : item.getProduto().getReceita().getIngredientes()) {
                ingredientesNecessarios.merge(ing.getId(), item.getQuantidade(), Integer::sum);
            }
        }
        return ingredientesNecessarios;
    }

    public Pedido criaPedido(Cliente cliente, List<ItemPedido> itens, String enderecoEntrega) {
        Map<Long, Integer> ingredientesNecessarios = calculaIngredientesNecessarios(itens);

        if (!estoqueService.verificaDisponibilidade(ingredientesNecessarios)) {
             return new Pedido(
                cliente,
                null,
                itens,
                StatusPedido.NEGADO,
                0, 0, 0, 0,
                enderecoEntrega,
                LocalDateTime.now()
            );
        }

        estoqueService.baixaEstoque(ingredientesNecessarios);

        double baseValue = financeiroService.calculaPrecoBase(itens);
        double imposto = financeiroService.calculaImposto(baseValue);
        double desconto = financeiroService.calculaDesconto(cliente.getCpf(), baseValue);
        double valorCobrado = (baseValue - desconto) + imposto;

        Pedido pedido = new Pedido(
            cliente,
            null,
            itens,
            StatusPedido.APROVADO,
            baseValue,
            imposto,
            desconto,
            valorCobrado,
            enderecoEntrega,
            LocalDateTime.now()
        );

        return pedidoRepository.salva(pedido);
    }
}
