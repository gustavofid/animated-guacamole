package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses;

import java.time.LocalDateTime;

public class PedidosPorPeriodo {
    public record DTO(
        long id,
        String clienteNome,
        LocalDateTime dataEntrega,
        double valorCobrado
    ) {}
}
