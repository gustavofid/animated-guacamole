package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ClienteRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;

@Service
public class ClienteService {

    private ClienteRepository clienteRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ClienteService(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Cliente cadastrarCliente(
            String cpf,
            String nome,
            String celular,
            String endereco,
            String email,
            String senha
    ) {

        clienteRepository.buscarPorEmail(email)
                .ifPresent(cliente -> {
                    throw new RuntimeException("Email já cadastrado");
                });

        String senhaCriptografada =
                passwordEncoder.encode(senha);

        Cliente cliente = new Cliente(
                cpf,
                nome,
                celular,
                endereco,
                email,
                senhaCriptografada
        );

        return clienteRepository.salvar(cliente);
    }

    public Cliente recuperaClientePorCpf(String cpf) {
        return clienteRepository.recuperaPorCpf(cpf);
    }
}