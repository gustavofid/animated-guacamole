package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades;

public class ItemPedido {
    private Produto item;
    private int quantidade;
    private double valorUnitario;

    public ItemPedido(Produto item, int quantidade, double valorUnitario) {
        this.item = item;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public Produto getItem() { return item; }
    public int getQuantidade() { return quantidade; }
    public double getValorUnitario() { return valorUnitario; }
    public double getValorTotal() {
        return quantidade * valorUnitario;
    }
}
