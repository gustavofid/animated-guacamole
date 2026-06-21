package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ClienteRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;

@Repository
public class ClienteRepositoryJDBC implements ClienteRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ClienteRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Cliente recuperaPorCpf(String cpf) {
        String sql = "SELECT cpf, nome, celular, endereco, email, senha FROM clientes WHERE cpf = ?";

        return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new Cliente(
                rs.getString("cpf"),
                rs.getString("nome"),
                rs.getString("celular"),
                rs.getString("endereco"),
                rs.getString("email"),
                rs.getString("senha")
            ),
            cpf
        );
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        String sql = "SELECT cpf, nome, celular, endereco, email, senha FROM clientes WHERE email = ?";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new Cliente(
                rs.getString("cpf"),
                rs.getString("nome"),
                rs.getString("celular"),
                rs.getString("endereco"),
                rs.getString("email"),
                rs.getString("senha")
            ),
            email
        ).stream().findFirst();
    }

    @Override
    public Cliente salvar(Cliente cliente) {

        String sql = """
            INSERT INTO clientes (cpf, nome, celular, endereco, email, senha)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
            sql,
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getCelular(),
            cliente.getEndereco(),
            cliente.getEmail(),
            cliente.getSenha()
        );

        return cliente;
    }
}