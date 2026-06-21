package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.ItemPedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.ClienteService;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.ProdutoService;

@Component
public class SubmeterPedidoUC {
    private ClienteService clienteService;
    private PedidoService pedidoService;
    private ProdutoService produtoService;

    @Autowired
    public SubmeterPedidoUC(ClienteService clienteService,
                            PedidoService pedidoService,
                            ProdutoService produtoService) {
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    @Transactional
    public Pedido run(PedidoRequest request) {
        Cliente cliente = clienteService.recuperaClientePorCpf(request.cpf());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente nao encontrado");
        }

        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoRequest itemReq : request.itens()) {
            Produto produto = produtoService.recuperaPorId(itemReq.produtoId());
            if (produto == null) {
                throw new IllegalArgumentException("Produto nao encontrado: " + itemReq.produtoId());
            }

            ItemPedido item = new ItemPedido(produto, itemReq.quantidade(), produto.getPreco());
            itens.add(item);
        }

        return pedidoService.criaPedido(cliente, itens, request.enderecoEntrega());
    }
}
