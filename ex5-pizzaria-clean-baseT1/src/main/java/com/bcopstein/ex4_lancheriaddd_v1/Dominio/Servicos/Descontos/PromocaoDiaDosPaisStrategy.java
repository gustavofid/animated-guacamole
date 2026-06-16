package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos;

import org.springframework.stereotype.Component;

// 10% de desconto promocional para todos os clientes no Dia dos Pais
@Component
public class PromocaoDiaDosPaisStrategy implements IDescontoStrategy {

    @Override
    public double calcular(String cpf, double precoBase) {
        return precoBase * 0.10;
    }

    @Override
    public String getCodigo() {
        return "PromocaoDiaDosPais";
    }
}
