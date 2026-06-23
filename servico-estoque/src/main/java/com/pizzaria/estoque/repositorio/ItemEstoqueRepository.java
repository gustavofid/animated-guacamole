package com.pizzaria.estoque.repositorio;

import com.pizzaria.estoque.entidade.ItemEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {
    Optional<ItemEstoque> findByIngredienteId(Long ingredienteId);
}
