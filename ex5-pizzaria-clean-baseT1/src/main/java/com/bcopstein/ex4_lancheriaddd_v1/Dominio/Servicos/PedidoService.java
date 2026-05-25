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
    private IPagamentoService pagamentoService;
    private ICozinhaService cozinhaService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, 
                         EstoqueService estoqueService,
                         FinanceiroService financeiroService,
                         IPagamentoService pagamentoService,
                         ICozinhaService cozinhaService) {
        this.pedidoRepository = pedidoRepository;
        this.estoqueService = estoqueService;
        this.financeiroService = financeiroService;
        this.pagamentoService = pagamentoService;
        this.cozinhaService = cozinhaService;
    }

    public Map<Long, Integer> calculaIngredientesNecessarios(List<ItemPedido> itens) {
        Map<Long, Integer> ingredientesNecessarios = new HashMap<>();
        for (ItemPedido item : itens) {
            for (Ingrediente ing : item.getItem().getReceita().getIngredientes()) {
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
                LocalDateTime.now(),
                null
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
            LocalDateTime.now(),
            null
        );

        return pedidoRepository.salva(pedido);
    }

    public StatusPedido consultaStatus(long idPedido) {
        Pedido pedido = pedidoRepository.recuperaPorId(idPedido);

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        return pedido.getStatus();
    }

    public Pedido cancelaPedido(long idPedido) {
        Pedido pedido = pedidoRepository.recuperaPorId(idPedido);

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new IllegalStateException("Somente pedidos aprovados e ainda não pagos podem ser cancelados");
        }

        pedidoRepository.atualizaStatus(idPedido, StatusPedido.CANCELADO);
        pedido.setStatus(StatusPedido.CANCELADO);

        return pedido;
    }

    public Pedido pagarPedido(long idPedido) {
        Pedido pedido = pedidoRepository.recuperaPorId(idPedido);

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new IllegalStateException("Somente pedidos aprovados podem ser pagos");
        }

        if (!pagamentoService.processaPagamento(pedido)) {
            throw new IllegalStateException("Pagamento não autorizado");
        }

        LocalDateTime dataHoraPagamento = LocalDateTime.now();
        pedidoRepository.atualizaPagamento(idPedido, StatusPedido.PAGO, dataHoraPagamento);
        pedido.setStatus(StatusPedido.PAGO);
        pedido.setDataHoraPagamento(dataHoraPagamento);

        cozinhaService.chegadaDePedido(pedido);

        return pedido;
    }
}
