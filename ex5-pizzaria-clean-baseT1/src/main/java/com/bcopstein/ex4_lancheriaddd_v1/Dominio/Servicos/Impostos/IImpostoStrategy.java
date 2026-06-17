package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Impostos;

public interface IImpostoStrategy {
    double calcular(double precoBase);
    String getId();
}
