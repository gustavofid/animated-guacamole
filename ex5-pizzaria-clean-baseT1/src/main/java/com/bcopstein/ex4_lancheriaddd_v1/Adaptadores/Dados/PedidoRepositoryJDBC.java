package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;


@Component
public class PedidoRepositoryJDBC implements PedidoRepository{
    private JdbcTemplate jdbcTemplate;
    private ProdutosRepository produtoRepository;

    @Autowired
    public PedidoRepositoryJDBC(JdbcTemplate jdbcTemplate, ProdutosRepository produtoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public Pedido salva(Pedido pedido) {
        String sql = """
            INSERT INTO pedidos
            (id, cliente_cpf, endereco_entrega, status,
            valor, impostos, desconto, valor_cobrado,
            data_criacao, data_hora_pagamento)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
            pedido.getId(),
            pedido.getCliente().getCpf(),
            pedido.getEnderecoEntrega(),
            pedido.getStatus().name(),
            pedido.getValor(),
            pedido.getImpostos(),
            pedido.getDesconto(),
            pedido.getValorCobrado(),
            pedido.getDataCriacao(),
            pedido.getDataHoraPagamento()
        );

        String sqlItens = """
            INSERT INTO itens_pedido
            (pedido_id, produto_id, quantidade, valor_unitario)
            VALUES (?, ?, ?, ?)
        """;

        for (var item : pedido.getItens()) {
            jdbcTemplate.update(sqlItens,
                pedido.getId(),
                item.getItem().getId(),
                item.getQuantidade(),
                item.getValorUnitario()
            );
        }

        return pedido;
    }

    @Override
    public Pedido recuperaPorId(long id) {
        String sql = """
            SELECT id, cliente_cpf, endereco_entrega, status,
                valor, impostos, desconto, valor_cobrado,
                data_criacao, data_hora_pagamento
            FROM pedidos
            WHERE id = ?
        """;

        var pedidos = this.jdbcTemplate.query(
            sql,
            ps -> ps.setLong(1, id),
            (rs, rowNum) -> new Pedido(
                rs.getLong("id"),
                null,
                rs.getTimestamp("data_hora_pagamento") != null ? rs.getTimestamp("data_hora_pagamento").toLocalDateTime() : null,
                null,
                StatusPedido.valueOf(rs.getString("status")),
                rs.getDouble("valor"),
                rs.getDouble("impostos"),
                rs.getDouble("desconto"),
                rs.getDouble("valor_cobrado"),
                rs.getString("endereco_entrega"),
                rs.getTimestamp("data_criacao") != null ? rs.getTimestamp("data_criacao").toLocalDateTime() : null
            )
        );

        if (pedidos.isEmpty()) {
            return null;
        }

        Pedido pedido = pedidos.get(0);
        pedido.setItens(recuperaItensPedido(id));
        return pedido;
    }

    @Override
    public List<Pedido> recuperaPorClienteNoPeriodo(String cpf, java.time.LocalDateTime inicio) {
        String sql = """
            SELECT id, cliente_cpf, endereco_entrega, status,
                valor, impostos, desconto, valor_cobrado,
                data_criacao, data_hora_pagamento
            FROM pedidos
            WHERE cliente_cpf = ? AND data_criacao >= ?
        """;

        return this.jdbcTemplate.query(
            sql,
            ps -> {
                ps.setString(1, cpf);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(inicio));
            },
            (rs, rowNum) -> new Pedido(
                rs.getLong("id"),
                null,
                rs.getTimestamp("data_hora_pagamento") != null ? rs.getTimestamp("data_hora_pagamento").toLocalDateTime() : null,
                null,
                StatusPedido.valueOf(rs.getString("status")),
                rs.getDouble("valor"),
                rs.getDouble("impostos"),
                rs.getDouble("desconto"),
                rs.getDouble("valor_cobrado"),
                rs.getString("endereco_entrega"),
                rs.getTimestamp("data_criacao") != null ? rs.getTimestamp("data_criacao").toLocalDateTime() : null
            )
        );
    }

    private List<ItemPedido> recuperaItensPedido(long pedidoId) {
        String sql = """
            SELECT produto_id, quantidade, valor_unitario
            FROM itens_pedido
            WHERE pedido_id = ?
        """;

        return this.jdbcTemplate.query(
            sql,
            ps -> ps.setLong(1, pedidoId),
            (rs, rowNum) -> {
                Produto produto = produtoRepository.recuperaProdutoPorid(rs.getLong("produto_id"));

                return new ItemPedido(
                    produto,
                    rs.getInt("quantidade"),
                    rs.getDouble("valor_unitario")
                );
            }
        );
    }
}
