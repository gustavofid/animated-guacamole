package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs;

public record ClienteRequestDTO(
    String cpf,
    String nome,
    String celular,
    String endereco,
    String email,
    String senha
) {}