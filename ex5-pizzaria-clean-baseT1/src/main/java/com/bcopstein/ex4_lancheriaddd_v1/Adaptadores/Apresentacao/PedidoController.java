package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.SubmeterPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.PedidoRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    private SubmeterPedidoUC submeterPedidoUC;

    @Autowired
    public PedidoController(SubmeterPedidoUC submeterPedidoUC) {
        this.submeterPedidoUC = submeterPedidoUC;
    }

    @PostMapping("/submeter")
    @CrossOrigin("*")
    public Pedido submeterPedido(@RequestBody PedidoRequest request) {
        return submeterPedidoUC.run(request);
    }
}
