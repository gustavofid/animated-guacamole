package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.EstoqueRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Ingrediente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;

@Repository
public class EstoqueRepositoryJDBC implements EstoqueRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EstoqueRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ItemEstoque> recuperaTodos() {
        String sql = """
            SELECT ie.id, ie.quantidade, i.id as ingrediente_id, i.descricao
            FROM itensEstoque ie
            JOIN ingredientes i ON ie.ingrediente_id = i.id
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Ingrediente ingrediente = new Ingrediente(rs.getLong("ingrediente_id"), rs.getString("descricao"));
            return new ItemEstoque(ingrediente, rs.getInt("quantidade"));
        });
    }

    @Override
    public ItemEstoque recuperaPorIngrediente(long ingredienteId) {
        String sql = """
            SELECT ie.id, ie.quantidade, i.id as ingrediente_id, i.descricao
            FROM itensEstoque ie
            JOIN ingredientes i ON ie.ingrediente_id = i.id
            WHERE ie.ingrediente_id = ?
        """;
        List<ItemEstoque> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Ingrediente ingrediente = new Ingrediente(rs.getLong("ingrediente_id"), rs.getString("descricao"));
            return new ItemEstoque(ingrediente, rs.getInt("quantidade"));
        }, ingredienteId);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void atualizaQuantidade(long ingredienteId, int novaQuantidade) {
        String sql = "UPDATE itensEstoque SET quantidade = ? WHERE ingrediente_id = ?";
        jdbcTemplate.update(sql, novaQuantidade, ingredienteId);
    }
}
