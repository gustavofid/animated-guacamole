package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.LoginRequestDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.DTOs.LoginResponseDTO;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Config.JwtService;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ClienteRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto
    ) {

        Cliente cliente = clienteRepository
                .buscarPorEmail(dto.email())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        boolean senhaCorreta =
                passwordEncoder.matches(
                        dto.senha(),
                        cliente.getSenha()
                );
                
        if (!senhaCorreta) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.gerarToken(cliente.getEmail());

        return ResponseEntity.ok(
                new LoginResponseDTO(token)
        );
    }
}
