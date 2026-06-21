package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@Service
public class CozinhaService implements ICozinhaService {
    private Queue<Pedido> filaEntrada;
    private Pedido emPreparacao;
    private Queue<Pedido> filaSaida;

    private ScheduledExecutorService scheduler;
    private PedidoRepository pedidoRepository;
    private IEntregaService entregaService;

    @Autowired
    public CozinhaService(PedidoRepository pedidoRepository, IEntregaService entregaService) {
        this.pedidoRepository = pedidoRepository;
        this.entregaService = entregaService;
        filaEntrada = new LinkedBlockingQueue<Pedido>();
        emPreparacao = null;
        filaSaida = new LinkedBlockingQueue<Pedido>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private synchronized void colocaEmPreparacao(Pedido pedido){
        pedidoRepository.atualizaStatus(pedido.getId(), StatusPedido.PREPARACAO);
        pedido.setStatus(StatusPedido.PREPARACAO);
        emPreparacao = pedido;
        System.out.println("Pedido em preparacao: "+pedido);
        // Agenda pedidoPronto para ser chamado em 5 segundos
        scheduler.schedule(() -> pedidoPronto(), 5, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void chegadaDePedido(Pedido p) {
        filaEntrada.add(p);
        pedidoRepository.atualizaStatus(p.getId(), StatusPedido.AGUARDANDO);
        p.setStatus(StatusPedido.AGUARDANDO);
        System.out.println("Pedido na fila de entrada: "+p);
        if (emPreparacao == null) {
            colocaEmPreparacao(filaEntrada.poll());
        }
    }

    @Override
    public synchronized void pedidoPronto() {
        if (emPreparacao == null) {
            return;
        }
        pedidoRepository.atualizaStatus(emPreparacao.getId(), StatusPedido.PRONTO);
        emPreparacao.setStatus(StatusPedido.PRONTO);
        filaSaida.add(emPreparacao);
        System.out.println("Pedido na fila de saida: "+emPreparacao);
        entregaService.chegadaDePedido(emPreparacao);
        emPreparacao = null;
        // Se tem pedidos na fila, programa a preparação para daqui a 1 segundo
        if (!filaEntrada.isEmpty()){
            Pedido prox = filaEntrada.poll();
            scheduler.schedule(() -> colocaEmPreparacao(prox), 1, TimeUnit.SECONDS);
        }
    }
}
