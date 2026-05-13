package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.ClienteRequestDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.ClienteResponseDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Requests.CadastroClienteRequest;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @RequestBody ClienteRequestDTO dto
    ) {

        Cliente cliente = clienteService.cadastrarCliente(
                dto.cpf(),
                dto.nome(),
                dto.celular(),
                dto.endereco(),
                dto.email(),
                dto.senha()
        );

        ClienteResponseDTO response = new ClienteResponseDTO(
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}