package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.ClienteService;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner carregarDados(
            ClienteService clienteService
    ) {

        return args -> {

            clienteService.cadastrarCliente(
                    "9001",
                    "Huguinho Pato",
                    "51985744566",
                    "Rua das Flores, 100",
                    "huguinho.pato@email.com",
                    "123456"
            );

            clienteService.cadastrarCliente(
                    "9002",
                    "Zezinho Pato",
                    "5199172079",
                    "Av. Central, 200",
                    "zezinho.pato@email.com",
                    "123456"
            );
        };
    }
}