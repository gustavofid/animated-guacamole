package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.DescontoService;

@RestController
@RequestMapping("/admin/descontos")
public class DescontoController {

    private final DescontoService descontoService;

    @Autowired
    public DescontoController(DescontoService descontoService) {
        this.descontoService = descontoService;
    }

    // UC3 — Lista as políticas de desconto disponíveis
    @GetMapping
    public ResponseEntity<List<String>> listarDescontos() {
        return ResponseEntity.ok(descontoService.listarDescontosDisponiveis());
    }

    // UC4 — Define a política de desconto corrente
    @PostMapping("/ativo")
    public ResponseEntity<Map<String, String>> setDescontoAtivo(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        descontoService.setDescontoAtivo(codigo);
        return ResponseEntity.ok(Map.of(
                "mensagem", "Desconto ativo atualizado com sucesso",
                "desconto", codigo));
    }

    @GetMapping("/ativo")
    public ResponseEntity<Map<String, String>> getDescontoAtivo() {
        return ResponseEntity.ok(Map.of("desconto", descontoService.getDescontoAtivo()));
    }
}
