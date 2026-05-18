package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.PedidoCanceladoDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.StatusPedidoDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.CancelarPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.SolicitarStatusPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.SubmeterPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.StatusPedido;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    private SubmeterPedidoUC submeterPedidoUC;
    private SolicitarStatusPedidoUC solicitarStatusPedidoUC;
    private CancelarPedidoUC cancelarPedidoUC;

    @Autowired
    public PedidoController(SubmeterPedidoUC submeterPedidoUC, SolicitarStatusPedidoUC solicitarStatusPedidoUC, CancelarPedidoUC cancelarPedidoUC) {
        this.submeterPedidoUC = submeterPedidoUC;
        this.solicitarStatusPedidoUC = solicitarStatusPedidoUC;
        this.cancelarPedidoUC = cancelarPedidoUC;
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
}
