package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.util.List;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;

public interface EstoqueRepository {
    List<ItemEstoque> recuperaTodos();
    ItemEstoque recuperaPorIngrediente(long ingredienteId);
    void atualizaQuantidade(long ingredienteId, int novaQuantidade);
}
