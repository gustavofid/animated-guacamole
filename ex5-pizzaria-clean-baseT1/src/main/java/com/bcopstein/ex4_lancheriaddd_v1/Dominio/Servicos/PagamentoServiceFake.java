package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Service
public class PagamentoServiceFake implements IPagamentoService {
    @Override
    public boolean processaPagamento(Pedido pedido) {
        return true;
    }
}

