package com.pizzaria.estoque.controlador;

import com.pizzaria.estoque.servico.EstoqueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.Map;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private static final Logger log = LoggerFactory.getLogger(EstoqueController.class);

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/disponibilidade")
    public ResponseEntity<Boolean> verificarDisponibilidade(@RequestBody Map<Long, Integer> ingredientes) {
        log.info("[instância {}] verificando disponibilidade", hostname());
        return ResponseEntity.ok(estoqueService.verificarDisponibilidade(ingredientes));
    }

    @PutMapping("/baixa")
    public ResponseEntity<Void> baixarEstoque(@RequestBody Map<Long, Integer> ingredientes) {
        log.info("[instância {}] baixando estoque", hostname());
        estoqueService.baixarEstoque(ingredientes);
        return ResponseEntity.ok().build();
    }

    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "desconhecido";
        }
    }
}
