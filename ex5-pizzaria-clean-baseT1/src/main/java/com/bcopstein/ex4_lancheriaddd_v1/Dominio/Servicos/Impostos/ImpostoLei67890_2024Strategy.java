package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Impostos;

import org.springframework.stereotype.Component;

// Nova lei: alíquota de 12% (reforma tributária fictícia para demonstrar extensibilidade)
@Component
public class ImpostoLei67890_2024Strategy implements IImpostoStrategy {

    @Override
    public double calcular(double precoBase) {
        return precoBase * 0.12;
    }

    @Override
    public String getCodigoLei() {
        return "Lei67890_2024";
    }
}
