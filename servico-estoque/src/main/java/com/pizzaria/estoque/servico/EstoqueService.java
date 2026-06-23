package com.pizzaria.estoque.servico;

import com.pizzaria.estoque.entidade.ItemEstoque;
import com.pizzaria.estoque.repositorio.ItemEstoqueRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EstoqueService {

    private final ItemEstoqueRepository repository;

    public EstoqueService(ItemEstoqueRepository repository) {
        this.repository = repository;
    }

    public boolean verificarDisponibilidade(Map<Long, Integer> ingredientesNecessarios) {
        for (Map.Entry<Long, Integer> entry : ingredientesNecessarios.entrySet()) {
            ItemEstoque item = repository.findByIngredienteId(entry.getKey()).orElse(null);
            if (item == null || item.getQuantidade() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void baixarEstoque(Map<Long, Integer> ingredientesNecessarios) {
        for (Map.Entry<Long, Integer> entry : ingredientesNecessarios.entrySet()) {
            ItemEstoque item = repository.findByIngredienteId(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Ingrediente não encontrado: " + entry.getKey()));
            item.setQuantidade(item.getQuantidade() - entry.getValue());
            repository.save(item);
        }
    }
}
