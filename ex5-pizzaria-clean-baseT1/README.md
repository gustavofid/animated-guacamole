# Pizzaria Clean Architecture - Backend

Este projeto é um sistema de gestão de pedidos de uma tele-pizza, desenvolvido seguindo os princípios da **Arquitetura Limpa (Clean Architecture)** e **Domain-Driven Design (DDD)**.

## Arquitetura

O projeto está organizado em quatro camadas principais para garantir o isolamento e a testabilidade:

1.  **Domínio (Domain):** Contém as entidades de negócio, interfaces de repositório e serviços de domínio. É o núcleo do sistema e não depende de nenhuma camada externa.
2.  **Aplicação (Application):** Implementa os Casos de Uso (Use Cases), orquestrando a lógica de negócio entre o domínio e os adaptadores.
3.  **Adaptadores (Adapters):** Interface entre o mundo externo e o sistema. Inclui:
    *   **Apresentação:** Controllers REST e DTOs.
    *   **Dados:** Implementações dos repositórios (neste caso, usando JDBC).
    *   **Configuração:** Configurações de segurança (JWT), beans e carregamento de dados.

---

## Casos de Uso (UC) - Implementação

### UC1: Registrar cliente no sistema
*   **Descrição:** Permite que um novo cliente se cadastre informando nome, cpf, celular, endereço, email e senha.
*   **Implementação:** O `ClienteController` recebe um `ClienteRequestDTO`, que é processado pelo `ClienteService`. A senha é criptografada antes de ser salva no `ClienteRepository`.

### UC2: Autenticar no sistema
*   **Descrição:** O cliente se autentica usando email (usuário) e senha para obter um token JWT.
*   **Implementação:** O `AuthController` valida as credenciais contra o `ClienteRepository`. Se válidas, o `JwtService` gera um token para sessões futuras.

### UC3: Carregar cardápio
*   **Descrição:** O cliente solicita o cardápio disponível.
*   **Implementação:** O `CardapioController` utiliza o `RecuperarCardapioUC`. Atualmente, ele retorna o cardápio de ID 1 (cardápio corrente). O `CardapioPresenter` formata a saída, destacando as sugestões do "chef".

### UC4: Submeter pedido para aprovação
*   **Descrição:** O cliente envia uma lista de itens para um pedido. O sistema verifica o estoque, calcula impostos e descontos.
*   **Implementação:** 
    *   `SubmeterPedidoUC` coordena a operação.
    *   `PedidoService.criaPedido` realiza a lógica principal:
        *   **Estoque:** Verifica se há ingredientes suficientes via `EstoqueService`.
        *   **Financeiro:** Calcula o valor base, imposto (10%) e desconto (7% para clientes com mais de 3 pedidos nos últimos 20 dias) através do `FinanceiroService`.
    *   Se o estoque for insuficiente, o pedido é criado com status `NEGADO`. Caso contrário, os ingredientes são baixados do estoque e o status é definido como `APROVADO`.

### UC5: Solicitar status de pedido
*   **Descrição:** Consulta o status atual de um pedido pelo seu ID.
*   **Implementação:** O `SolicitarStatusPedidoUC` consulta o `PedidoRepository` através do `PedidoService`.

### UC6: Cancelar pedido
*   **Descrição:** Permite o cancelamento de um pedido que foi `APROVADO`, mas ainda não foi pago.
*   **Implementação:** O `CancelarPedidoUC` verifica o status atual e, se permitido, altera para `CANCELADO`.

### UC7: Pagar pedido
*   **Descrição:** Processa o pagamento de um pedido aprovado.
*   **Implementação:** 
    *   O `PagarPedidoUC` aciona o `PedidoService.pagarPedido`.
    *   O status muda para `PAGO`.
    *   O pedido é enviado para a `CozinhaService` (simulada), que inicia o fluxo de preparação (AGUARDANDO -> PREPARAÇÃO -> PRONTO).

### UC8: Listar os pedidos entregues entre duas datas
*   **Descrição:** Relatório de todos os pedidos finalizados em um determinado período.
*   **Implementação:** O `ListarPedidosPorPeriodoUC` busca pedidos no `PedidoRepository` filtrando pelo status `ENTREGUE` e o intervalo de datas.

### UC9: Listar os pedidos de um determinado cliente entregues entre duas datas
*   **Descrição:** Relatório personalizado para o cliente ver seu histórico de pedidos entregues.
*   **Implementação:** O `ListarPedidosEntreguesClienteUC` funciona de forma similar ao UC8, mas adiciona o filtro por CPF do cliente.

---

## Serviços de Domínio

*   **PedidoService:** Coração da lógica de pedidos (criação, status, pagamento).
*   **FinanceiroService:** Encapsula as regras de cálculo de impostos e descontos.
*   **EstoqueService:** Gerencia a disponibilidade e baixa de ingredientes.
*   **CozinhaService / EntregaService:** Simulam o ciclo de vida do pedido após o pagamento, gerenciando as transições de status automaticamente.
*   **ClienteService:** Gerencia o cadastro e recuperação de clientes.

---

# Guia de Teste e Endpoints (Postman)

Este guia contém os endpoints formatados para facilitar a importação e teste via Postman.

**Base URL:** `http://localhost:8080`

### 1. Fluxo de Autenticação (Aberto)

#### UC1: Cadastrar Cliente
```bash
curl --location --request POST 'http://localhost:8080/clientes' \
--header 'Content-Type: application/json' \
--data-raw '{
  "cpf": "12345678901",
  "nome": "João Silva",
  "celular": "51999999999",
  "endereco": "Rua Exemplo, 123",
  "email": "joao@email.com",
  "senha": "senha123"
}'
```

#### UC2: Login
```bash
curl --location --request POST 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "joao@email.com",
  "senha": "senha123"
}'
```
> **Dica Postman:** Copie o `token` da resposta e configure na aba **Authorization** como **Bearer Token**.

---

### 2. Pedidos e Cardápio (Requer Autenticação)

#### UC3: Consultar Cardápio Corrente
```bash
curl --location --request GET 'http://localhost:8080/cardapio/corrente' \
--header 'Authorization: Bearer {{token}}'
```

#### UC4: Submeter Novo Pedido
```bash
curl --location --request POST 'http://localhost:8080/pedido/submeter' \
--header 'Authorization: Bearer {{token}}' \
--header 'Content-Type: application/json' \
--data-raw '{
  "cpf": "12345678901",
  "itens": [
    { "produtoId": 1, "quantidade": 1 },
    { "produtoId": 3, "quantidade": 2 }
  ],
  "enderecoEntrega": "Rua Exemplo, 123"
}'
```

#### UC5: Consultar Status do Pedido
```bash
curl --location --request GET 'http://localhost:8080/pedido/20/status' \
--header 'Authorization: Bearer {{token}}'
```

#### UC6: Cancelar Pedido (Apenas Aprovados)
```bash
curl --location --request PATCH 'http://localhost:8080/pedido/20/cancelar' \
--header 'Authorization: Bearer {{token}}'
```

#### UC7: Pagar Pedido
```bash
curl --location --request PATCH 'http://localhost:8080/pedido/20/pagar' \
--header 'Authorization: Bearer {{token}}'
```

---

### 3. Relatórios e Operações Administrativas

#### UC8: Listar Pedidos Entregues (Geral)
```bash
curl --location --request GET 'http://localhost:8080/pedido/entregues?inicio=2026-05-01T00:00:00&fim=2026-05-31T23:59:59' \
--header 'Authorization: Bearer {{token}}'
```

#### UC9: Listar Entregues por Cliente
```bash
curl --location --request GET 'http://localhost:8080/pedido/entregues/cliente/12345678901?inicio=2026-05-01T00:00:00&fim=2026-05-31T23:59:59' \
--header 'Authorization: Bearer {{token}}'
```

---

### IDs de Referência (Baseados em data.sql)

| ID | Produto | Preço |
| :--- | :--- | :--- |
| 1 | Pizza calabresa | 55.00 |
| 2 | Pizza queijo e presunto | 60.00 |
| 3 | Pizza margherita | 40.00 |

**Clientes Pré-cadastrados:**
- `teste@email.com` / `123456` (CPF: 12345678901)
- `huguinho.pato@email.com` / `123456` (CPF: 9001)
- `zezinho.pato@email.com` / `123456` (CPF: 9002)
