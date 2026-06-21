package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos.IDescontoStrategy;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Descontos.SemDescontoStrategy;

@Service
public class DescontoService {

    private final Map<String, IDescontoStrategy> estrategias;
    private IDescontoStrategy estrategiaAtiva;

    @Autowired
    public DescontoService(List<IDescontoStrategy> estrategias) {
        this.estrategias = estrategias.stream()
                .collect(Collectors.toMap(IDescontoStrategy::getCodigo, Function.identity()));
        this.estrategiaAtiva = this.estrategias.get("SemDesconto");
    }

    public void setDescontoAtivo(String codigo) {
        IDescontoStrategy nova = estrategias.get(codigo);
        if (nova == null) {
            throw new IllegalArgumentException("Desconto não encontrado: " + codigo);
        }
        this.estrategiaAtiva = nova;
    }

    public double calcular(String cpf, double precoBase) {
        return estrategiaAtiva.calcular(cpf, precoBase);
    }

    public String getDescontoAtivo() {
        return estrategiaAtiva.getCodigo();
    }

    public List<String> listarDescontosDisponiveis() {
        return estrategias.keySet().stream().sorted().toList();
    }
}
