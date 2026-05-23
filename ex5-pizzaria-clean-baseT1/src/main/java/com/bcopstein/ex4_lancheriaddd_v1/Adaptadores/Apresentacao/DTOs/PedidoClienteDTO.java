package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs;

import java.time.LocalDateTime;

public record PedidoClienteDTO(
    long id,
    String status,
    LocalDateTime dataCriacao,
    double valorCobrado
) {}
