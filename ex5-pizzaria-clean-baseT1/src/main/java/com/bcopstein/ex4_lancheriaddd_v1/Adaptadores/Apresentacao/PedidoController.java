package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.PedidoCanceladoDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.PedidoClienteDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.StatusPedidoDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.CancelarPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.EntregarPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.ListarPedidosClienteUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.ListarPedidosEntreguesClienteUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.ListarPedidosPorPeriodoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.SolicitarStatusPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.SubmeterPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidosPorPeriodo;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    private SubmeterPedidoUC submeterPedidoUC;
    private SolicitarStatusPedidoUC solicitarStatusPedidoUC;
    private CancelarPedidoUC cancelarPedidoUC;
    private ListarPedidosPorPeriodoUC listarPedidosPorPeriodoUC;
    private EntregarPedidoUC entregarPedidoUC;
    private ListarPedidosClienteUC listarPedidosClienteUC;
    private ListarPedidosEntreguesClienteUC listarPedidosEntreguesClienteUC;

    @Autowired
    public PedidoController(SubmeterPedidoUC submeterPedidoUC, 
                            SolicitarStatusPedidoUC solicitarStatusPedidoUC, 
                            CancelarPedidoUC cancelarPedidoUC,
                            ListarPedidosPorPeriodoUC listarPedidosPorPeriodoUC,
                            EntregarPedidoUC entregarPedidoUC,
                            ListarPedidosClienteUC listarPedidosClienteUC,
                            ListarPedidosEntreguesClienteUC listarPedidosEntreguesClienteUC) {
        this.submeterPedidoUC = submeterPedidoUC;
        this.solicitarStatusPedidoUC = solicitarStatusPedidoUC;
        this.cancelarPedidoUC = cancelarPedidoUC;
        this.listarPedidosPorPeriodoUC = listarPedidosPorPeriodoUC;
        this.entregarPedidoUC = entregarPedidoUC;
        this.listarPedidosClienteUC = listarPedidosClienteUC;
        this.listarPedidosEntreguesClienteUC = listarPedidosEntreguesClienteUC;
    }

    @PostMapping("/submeter")
    @CrossOrigin("*")
    public Pedido submeterPedido(@RequestBody PedidoRequest request) {
        return submeterPedidoUC.run(request);
    }

    @GetMapping("/{id}/status")
    @CrossOrigin("*")
    public StatusPedidoDTO consultarStatus(@PathVariable long id) {
        StatusPedido status = solicitarStatusPedidoUC.run(id);
        return new StatusPedidoDTO(id, status.name());
    }

    @PatchMapping("/{id}/cancelar")
    @CrossOrigin("*")
    public PedidoCanceladoDTO cancelarPedido(@PathVariable long id) {
        Pedido pedido = cancelarPedidoUC.run(id);

        return new PedidoCanceladoDTO(
            pedido.getId(),
            pedido.getStatus().name(),
            "Pedido cancelado com sucesso"
        );
    }

    @PatchMapping("/{id}/entregar")
    @CrossOrigin("*")
    public Pedido entregarPedido(@PathVariable long id) {
        return entregarPedidoUC.run(id);
    }

    @GetMapping("/entregues")
    @CrossOrigin("*")
    public List<PedidosPorPeriodo.DTO> listarEntregues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return listarPedidosPorPeriodoUC.run(inicio, fim);
    }

    @GetMapping("/cliente/{cpf}")
    @CrossOrigin("*")
    public List<PedidoClienteDTO> listarPedidosCliente(
            @PathVariable String cpf,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return listarPedidosClienteUC.run(cpf, inicio, fim);
    }

    @GetMapping("/entregues/cliente/{cpf}")
    @CrossOrigin("*")
    public List<PedidosPorPeriodo.DTO> listarPedidosEntreguesCliente(
            @PathVariable String cpf,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return listarPedidosEntreguesClienteUC.run(cpf, inicio, fim);
    }
}
