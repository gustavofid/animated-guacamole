package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.util.Optional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;

public interface ClienteRepository {
    Cliente recuperaPorCpf(String cpf);
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorEmail(String email);
}
