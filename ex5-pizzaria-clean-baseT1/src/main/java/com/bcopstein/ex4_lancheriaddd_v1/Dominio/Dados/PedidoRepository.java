package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.time.LocalDateTime;
import java.util.List;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

public interface PedidoRepository {
    Pedido salva(Pedido pedido);
    Pedido recuperaPorId(long id);
    List<Pedido> recuperaPorClienteNoPeriodo(String cpf, LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> recuperaEntreguesNoPeriodo(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> recuperaEntreguesPorClienteNoPeriodo(String cpf, LocalDateTime inicio, LocalDateTime fim);
    void atualizaStatus(long id, StatusPedido status);
}
