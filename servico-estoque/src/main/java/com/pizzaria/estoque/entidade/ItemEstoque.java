package com.pizzaria.estoque.entidade;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_estoque")
public class ItemEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;

    @Column(name = "ingrediente_id", unique = true)
    private Long ingredienteId;

    public Long getId() { return id; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Long getIngredienteId() { return ingredienteId; }
}
