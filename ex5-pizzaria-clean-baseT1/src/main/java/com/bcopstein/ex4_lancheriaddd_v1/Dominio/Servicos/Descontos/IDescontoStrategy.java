package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos;

public interface IDescontoStrategy {
    double calcular(String cpf, double precoBase);
    String getCodigo();
}
