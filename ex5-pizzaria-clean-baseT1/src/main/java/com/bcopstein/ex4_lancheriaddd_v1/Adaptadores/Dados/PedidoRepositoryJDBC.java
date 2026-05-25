package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ClienteRepository;
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
    private ClienteRepository clienteRepository;

    @Autowired
    public PedidoRepositoryJDBC(JdbcTemplate jdbcTemplate, 
                                ProdutosRepository produtoRepository,
                                ClienteRepository clienteRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Pedido salva(Pedido pedido) {
        String sql = """
            INSERT INTO pedidos
            (cliente_cpf, endereco_entrega, status,
            valor, impostos, desconto, valor_cobrado,
            data_criacao, data_hora_pagamento, data_entrega)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pedido.getCliente().getCpf());
            ps.setString(2, pedido.getEnderecoEntrega());
            ps.setString(3, pedido.getStatus().name());
            ps.setDouble(4, pedido.getValor());
            ps.setDouble(5, pedido.getImpostos());
            ps.setDouble(6, pedido.getDesconto());
            ps.setDouble(7, pedido.getValorCobrado());
            ps.setTimestamp(8, java.sql.Timestamp.valueOf(pedido.getDataCriacao()));
            ps.setTimestamp(9, pedido.getDataHoraPagamento() != null ? java.sql.Timestamp.valueOf(pedido.getDataHoraPagamento()) : null);
            ps.setTimestamp(10, pedido.getDataEntrega() != null ? java.sql.Timestamp.valueOf(pedido.getDataEntrega()) : null);
            return ps;
        }, keyHolder);

        long generatedId = keyHolder.getKey().longValue();
        pedido.setId(generatedId);

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
                data_criacao, data_hora_pagamento, data_entrega
            FROM pedidos
            WHERE id = ?
        """;

        var pedidos = this.jdbcTemplate.query(
            sql,
            ps -> ps.setLong(1, id),
            (rs, rowNum) -> new Pedido(
                rs.getLong("id"),
                clienteRepository.recuperaPorCpf(rs.getString("cliente_cpf")),
                rs.getTimestamp("data_hora_pagamento") != null ? rs.getTimestamp("data_hora_pagamento").toLocalDateTime() : null,
                null,
                StatusPedido.valueOf(rs.getString("status")),
                rs.getDouble("valor"),
                rs.getDouble("impostos"),
                rs.getDouble("desconto"),
                rs.getDouble("valor_cobrado"),
                rs.getString("endereco_entrega"),
                rs.getTimestamp("data_criacao") != null ? rs.getTimestamp("data_criacao").toLocalDateTime() : null,
                rs.getTimestamp("data_entrega") != null ? rs.getTimestamp("data_entrega").toLocalDateTime() : null
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
    public List<Pedido> recuperaPorClienteNoPeriodo(String cpf, LocalDateTime inicio, LocalDateTime fim) {
        String sql = """
            SELECT id, cliente_cpf, endereco_entrega, status,
                valor, impostos, desconto, valor_cobrado,
                data_criacao, data_hora_pagamento, data_entrega
            FROM pedidos
            WHERE cliente_cpf = ? AND data_criacao BETWEEN ? AND ?
        """;

        return this.jdbcTemplate.query(
            sql,
            ps -> {
                ps.setString(1, cpf);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(inicio));
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(fim));
            },
            (rs, rowNum) -> {
                Pedido p = new Pedido(
                    rs.getLong("id"),
                    clienteRepository.recuperaPorCpf(rs.getString("cliente_cpf")),
                    rs.getTimestamp("data_hora_pagamento") != null ? rs.getTimestamp("data_hora_pagamento").toLocalDateTime() : null,
                    null,
                    StatusPedido.valueOf(rs.getString("status")),
                    rs.getDouble("valor"),
                    rs.getDouble("impostos"),
                    rs.getDouble("desconto"),
                    rs.getDouble("valor_cobrado"),
                    rs.getString("endereco_entrega"),
                    rs.getTimestamp("data_criacao") != null ? rs.getTimestamp("data_criacao").toLocalDateTime() : null,
                    rs.getTimestamp("data_entrega") != null ? rs.getTimestamp("data_entrega").toLocalDateTime() : null
                );
                p.setItens(recuperaItensPedido(p.getId()));
                return p;
            }
        );
    }

    @Override
    public List<Pedido> recuperaEntreguesNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = """
            SELECT id, cliente_cpf, endereco_entrega, status,
                valor, impostos, desconto, valor_cobrado,
                data_criacao, data_hora_pagamento, data_entrega
            FROM pedidos
            WHERE status = 'ENTREGUE' AND data_entrega BETWEEN ? AND ?
        """;

        return this.jdbcTemplate.query(
            sql,
            ps -> {
                ps.setTimestamp(1, java.sql.Timestamp.valueOf(inicio));
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(fim));
            },
            (rs, rowNum) -> {
                Pedido p = new Pedido(
                    rs.getLong("id"),
                    clienteRepository.recuperaPorCpf(rs.getString("cliente_cpf")),
                    rs.getTimestamp("data_hora_pagamento") != null ? rs.getTimestamp("data_hora_pagamento").toLocalDateTime() : null,
                    null,
                    StatusPedido.valueOf(rs.getString("status")),
                    rs.getDouble("valor"),
                    rs.getDouble("impostos"),
                    rs.getDouble("desconto"),
                    rs.getDouble("valor_cobrado"),
                    rs.getString("endereco_entrega"),
                    rs.getTimestamp("data_criacao") != null ? rs.getTimestamp("data_criacao").toLocalDateTime() : null,
                    rs.getTimestamp("data_entrega") != null ? rs.getTimestamp("data_entrega").toLocalDateTime() : null
                );
                p.setItens(recuperaItensPedido(p.getId()));
                return p;
            }
        );
    }

    @Override
    public List<Pedido> recuperaEntreguesPorClienteNoPeriodo(String cpf, LocalDateTime inicio, LocalDateTime fim) {
        String sql = """
            SELECT id, cliente_cpf, endereco_entrega, status,
                valor, impostos, desconto, valor_cobrado,
                data_criacao, data_hora_pagamento, data_entrega
            FROM pedidos
            WHERE cliente_cpf = ? AND status = 'ENTREGUE' AND data_entrega BETWEEN ? AND ?
        """;

        return this.jdbcTemplate.query(
            sql,
            ps -> {
                ps.setString(1, cpf);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(inicio));
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(fim));
            },
            (rs, rowNum) -> {
                Pedido p = new Pedido(
                    rs.getLong("id"),
                    clienteRepository.recuperaPorCpf(rs.getString("cliente_cpf")),
                    rs.getTimestamp("data_hora_pagamento") != null ? rs.getTimestamp("data_hora_pagamento").toLocalDateTime() : null,
                    null,
                    StatusPedido.valueOf(rs.getString("status")),
                    rs.getDouble("valor"),
                    rs.getDouble("impostos"),
                    rs.getDouble("desconto"),
                    rs.getDouble("valor_cobrado"),
                    rs.getString("endereco_entrega"),
                    rs.getTimestamp("data_criacao") != null ? rs.getTimestamp("data_criacao").toLocalDateTime() : null,
                    rs.getTimestamp("data_entrega") != null ? rs.getTimestamp("data_entrega").toLocalDateTime() : null
                );
                p.setItens(recuperaItensPedido(p.getId()));
                return p;
            }
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

    @Override
    public void atualizaStatus(long id, StatusPedido status) {
        String sql;
        if (status == StatusPedido.ENTREGUE) {
            sql = """
                UPDATE pedidos
                SET status = ?, data_entrega = CURRENT_TIMESTAMP
                WHERE id = ?
            """;
        } else {
            sql = """
                UPDATE pedidos
                SET status = ?
                WHERE id = ?
            """;
        }

        jdbcTemplate.update(sql, status.name(), id);
    }

    @Override
    public void atualizaPagamento(long id, StatusPedido status, LocalDateTime dataHoraPagamento) {
        String sql = """
            UPDATE pedidos
            SET status = ?, data_hora_pagamento = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
            sql,
            status.name(),
            java.sql.Timestamp.valueOf(dataHoraPagamento),
            id
        );
    }
}
