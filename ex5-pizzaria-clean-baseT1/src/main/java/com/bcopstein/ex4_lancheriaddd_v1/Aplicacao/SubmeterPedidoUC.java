package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.ItemPedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ClienteRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.EstoqueRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Ingrediente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@Component
public class SubmeterPedidoUC {
    private ClienteRepository clienteRepository;
    private PedidoRepository pedidoRepository;
    private ProdutosRepository produtosRepository;
    private EstoqueRepository estoqueRepository;

    @Autowired
    public SubmeterPedidoUC(ClienteRepository clienteRepository,
                            PedidoRepository pedidoRepository,
                            ProdutosRepository produtosRepository,
                            EstoqueRepository estoqueRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtosRepository = produtosRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional
    public Pedido run(PedidoRequest request) {
        Cliente cliente = clienteRepository.recuperaPorCpf(request.cpf());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente nao encontrado");
        }

        List<ItemPedido> itens = new ArrayList<>();
        Map<Long, Integer> ingredientesNecessarios = new HashMap<>();

        for (ItemPedidoRequest itemReq : request.itens()) {
            Produto produto = produtosRepository.recuperaProdutoPorid(itemReq.produtoId());
            if (produto == null) {
                throw new IllegalArgumentException("Produto nao encontrado: " + itemReq.produtoId());
            }

            ItemPedido item = new ItemPedido(produto, itemReq.quantidade(), produto.getPreco());
            itens.add(item);

            // Calcular ingredientes necessarios
            for (Ingrediente ing : produto.getReceita().getIngredientes()) {
                ingredientesNecessarios.merge(ing.getId(), itemReq.quantidade(), Integer::sum);
            }
        }

        // Verificar estoque
        for (Map.Entry<Long, Integer> entry : ingredientesNecessarios.entrySet()) {
            ItemEstoque itemEstoque = estoqueRepository.recuperaPorIngrediente(entry.getKey());
            if (itemEstoque == null || itemEstoque.getQuantidade() < entry.getValue()) {
                // Se faltar ingredientes, o pedido é negado
                Pedido pedidoNegado = new Pedido(
                    new Random().nextLong(1000000),
                    cliente,
                    null,
                    itens,
                    StatusPedido.NEGADO,
                    0, 0, 0, 0,
                    request.enderecoEntrega(),
                    LocalDateTime.now()
                );
                return pedidoNegado;
            }
        }

        // Se chegou aqui, tem estoque suficiente. Dar baixa no estoque.
        for (Map.Entry<Long, Integer> entry : ingredientesNecessarios.entrySet()) {
            ItemEstoque itemEstoque = estoqueRepository.recuperaPorIngrediente(entry.getKey());
            estoqueRepository.atualizaQuantidade(entry.getKey(), itemEstoque.getQuantidade() - entry.getValue());
        }

        // Calcular precos
        double baseValue = itens.stream().mapToDouble(i -> i.getValorUnitario() * i.getQuantidade()).sum();
        double imposto = baseValue * 0.10;
        
        // Desconto: > 3 pedidos nos últimos 20 dias
        LocalDateTime vinteDiasAtras = LocalDateTime.now().minusDays(20);
        List<Pedido> pedidosRecentes = pedidoRepository.recuperaPorClienteNoPeriodo(cliente.getCpf(), vinteDiasAtras);
        double desconto = 0;
        if (pedidosRecentes.size() > 3) {
            desconto = baseValue * 0.07;
        }

        double valorCobrado = (baseValue - desconto) + imposto;

        Pedido pedidoAprovado = new Pedido(
            new Random().nextLong(1000000),
            cliente,
            null,
            itens,
            StatusPedido.APROVADO,
            baseValue,
            imposto,
            desconto,
            valorCobrado,
            request.enderecoEntrega(),
            LocalDateTime.now()
        );

        return pedidoRepository.salva(pedidoAprovado);
    }
}
