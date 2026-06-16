package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Impostos;

import org.springframework.stereotype.Component;

// Lei vigente: imposto único de 10% sobre o valor dos itens
@Component
public class ImpostoLei12345_2020Strategy implements IImpostoStrategy {

    @Override
    public double calcular(double precoBase) {
        return precoBase * 0.10;
    }

    @Override
    public String getCodigoLei() {
        return "Lei12345_2020";
    }
}
