package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.Impostos.IImpostoStrategy;

@Service
public class ImpostoService {

    private final IImpostoStrategy estrategiaAtiva;

    @Autowired
    public ImpostoService(List<IImpostoStrategy> estrategias,
                          @Value("${IMPOSTO_ATIVO:Lei12345_2020}") String codigoLeiAtiva) {
        this.estrategiaAtiva = estrategias.stream()
                .filter(e -> e.getCodigoLei().equals(codigoLeiAtiva))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estratégia de imposto não encontrada para a lei: " + codigoLeiAtiva));
    }

    public double calcular(double precoBase) {
        return estrategiaAtiva.calcular(precoBase);
    }

    public String getLeiAtiva() {
        return estrategiaAtiva.getCodigoLei();
    }
}
