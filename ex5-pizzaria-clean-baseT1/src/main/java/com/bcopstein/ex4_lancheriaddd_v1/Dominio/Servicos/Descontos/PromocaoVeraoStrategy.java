package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos;

import org.springframework.stereotype.Component;

// 5% de desconto promocional para todos os clientes durante o verão
@Component
public class PromocaoVeraoStrategy implements IDescontoStrategy {

    @Override
    public double calcular(String cpf, double precoBase) {
        return precoBase * 0.05;
    }

    @Override
    public String getCodigo() {
        return "PromocaoVerao";
    }
}
