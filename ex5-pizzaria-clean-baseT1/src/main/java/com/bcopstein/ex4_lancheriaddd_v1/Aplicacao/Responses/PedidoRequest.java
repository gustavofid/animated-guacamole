package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses;

import java.util.List;

public record PedidoRequest(
    String cpf,
    String enderecoEntrega,
    List<ItemPedidoRequest> itens
) {}
