package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@Service
public class EntregaService implements IEntregaService {
    private Queue<Pedido> filaEntrada;
    private Pedido emTransporte;

    private ScheduledExecutorService scheduler;
    private PedidoRepository pedidoRepository;

    @Autowired
    public EntregaService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        filaEntrada = new LinkedBlockingQueue<Pedido>();
        emTransporte = null;
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private synchronized void colocaEmTransporte(Pedido pedido) {
        pedidoRepository.atualizaStatus(pedido.getId(), StatusPedido.TRANSPORTE);
        pedido.setStatus(StatusPedido.TRANSPORTE);
        emTransporte = pedido;
        scheduler.schedule(() -> pedidoEntregue(), 5, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void chegadaDePedido(Pedido pedido) {
        filaEntrada.add(pedido);
        if (emTransporte == null) {
            colocaEmTransporte(filaEntrada.poll());
        }
    }

    @Override
    public synchronized void pedidoEntregue() {
        if (emTransporte == null) {
            return;
        }
        pedidoRepository.atualizaStatus(emTransporte.getId(), StatusPedido.ENTREGUE);
        emTransporte.setStatus(StatusPedido.ENTREGUE);
        emTransporte = null;
        if (!filaEntrada.isEmpty()) {
            Pedido prox = filaEntrada.poll();
            scheduler.schedule(() -> colocaEmTransporte(prox), 1, TimeUnit.SECONDS);
        }
    }
}
