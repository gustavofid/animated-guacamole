package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.EstoqueRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;

@Service
public class EstoqueService {
    private EstoqueRepository estoqueRepository;

    @Autowired
    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public boolean verificaDisponibilidade(Map<Long, Integer> ingredientesNecessarios) {
        for (Map.Entry<Long, Integer> entry : ingredientesNecessarios.entrySet()) {
            ItemEstoque itemEstoque = estoqueRepository.recuperaPorIngrediente(entry.getKey());
            if (itemEstoque == null || itemEstoque.getQuantidade() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void baixaEstoque(Map<Long, Integer> ingredientesNecessarios) {
        for (Map.Entry<Long, Integer> entry : ingredientesNecessarios.entrySet()) {
            ItemEstoque itemEstoque = estoqueRepository.recuperaPorIngrediente(entry.getKey());
            if (itemEstoque != null) {
                estoqueRepository.atualizaQuantidade(entry.getKey(), itemEstoque.getQuantidade() - entry.getValue());
            }
        }
    }
}
