package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EstoqueService {

    private final RestTemplate restTemplate;

    public EstoqueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verificaDisponibilidade(Map<Long, Integer> ingredientesNecessarios) {
        Boolean disponivel = restTemplate.postForObject(
                "http://servico-estoque/estoque/disponibilidade",
                ingredientesNecessarios,
                Boolean.class
        );
        return Boolean.TRUE.equals(disponivel);
    }

    public void baixaEstoque(Map<Long, Integer> ingredientesNecessarios) {
        restTemplate.put(
                "http://servico-estoque/estoque/baixa",
                ingredientesNecessarios
        );
    }
}
