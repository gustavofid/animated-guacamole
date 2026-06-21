package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos;

import org.springframework.stereotype.Component;

// Estratégia padrão: sem desconto ativo
@Component
public class SemDescontoStrategy implements IDescontoStrategy {

    @Override
    public double calcular(String cpf, double precoBase) {
        return 0.0;
    }

    @Override
    public String getCodigo() {
        return "SemDesconto";
    }
}
