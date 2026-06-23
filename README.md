# Pizzaria Microsserviços

Sistema de pedidos de pizzaria implementado como arquitetura de microsserviços com Spring Boot, Spring Cloud, RabbitMQ e PostgreSQL.

**Disciplina:** Projeto e Arquitetura de Software – PUCRS  
**Professor:** Bernardo Copstein  
**Alunos:** Gustavo de Freitas Fidélis, Gabriela Hoppe, Leonardo Gemin Pereira, William de Oliveira Klein

---

## Sumário

- [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
- [Arquitetura Interna do Monólito (Clean Architecture)](#arquitetura-interna-do-monólito-clean-architecture)
- [Serviços](#serviços)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Dados Pré-carregados](#dados-pré-carregados)
- [Casos de Uso](#casos-de-uso)
- [Rotas e Exemplos](#rotas-e-exemplos)
- [Fluxo Completo de um Pedido](#fluxo-completo-de-um-pedido)
- [Monitoramento](#monitoramento)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Decisões Técnicas Relevantes](#decisões-técnicas-relevantes)

---

## Visão Geral da Arquitetura

```
                        ┌─────────────────────────────────────┐
                        │           EUREKA SERVER              │
                        │         localhost:8761               │
                        │   (registro e descoberta de serviços)│
                        └──────────────┬──────────────────────┘
                                       │ todos os serviços se registram aqui
           ┌───────────────────────────┼────────────────────────┐
           │                           │                        │
    ┌──────▼──────┐            ┌───────▼───────┐       ┌───────▼────────┐
    │   GATEWAY   │            │   PIZZARIA    │       │    ESTOQUE     │
    │  port 8765  │──────────► │  port 8080    │──────►│  port 8081     │
    │  (JWT auth) │            │  (monólito)   │  HTTP │  (x3 instâncias│
    └─────────────┘            └───────┬───────┘       └────────┬───────┘
         ▲                             │                        │
         │ todas as                    │ publica ID             │ PostgreSQL
         │ requisições                 │ na fila                │
         │ externas                    ▼                ┌───────▼────────┐
                               ┌──────────────┐        │ postgres-estoque│
                               │   RABBITMQ   │        │  port 5432     │
                               │  port 5672   │        └────────────────┘
                               └──────┬───────┘
                                      │ consuming
                               ┌──────▼───────┐
                               │   ENTREGA    │
                               │  port 8082   │
                               │(x3 instâncias)│
                               └──────────────┘
```

Todo tráfego externo entra pelo **Gateway** na porta `8765`. Os demais serviços ficam na rede interna Docker e se comunicam via Eureka (descoberta de serviços).

---

## Arquitetura Interna do Monólito (Clean Architecture)

O monólito (`servico-pizzaria`) foi desenvolvido seguindo os princípios da **Clean Architecture** com quatro camadas bem definidas:

```
┌──────────────────────────────────────────────────┐
│                  ADAPTADORES                     │
│  Apresentação (Controllers REST)                 │
│  Dados (Repositórios JDBC)                       │
│  Config (Security, JWT, RabbitMQ, RestTemplate)  │
├──────────────────────────────────────────────────┤
│                  APLICAÇÃO                       │
│  Casos de Uso (UC1–UC9)                          │
├──────────────────────────────────────────────────┤
│              DOMÍNIO / SERVIÇOS                  │
│  PedidoService, FinanceiroService,               │
│  EstoqueService, EntregaService, ClienteService  │
├──────────────────────────────────────────────────┤
│               DOMÍNIO / DADOS                    │
│  Entidades (Pedido, Cliente, Produto, Cardápio)  │
│  Interfaces de Repositório                       │
└──────────────────────────────────────────────────┘
```

**Regra de dependência:** cada camada só conhece a camada imediatamente abaixo. O domínio não sabe nada sobre controllers, banco de dados ou frameworks externos.

### Serviços de Domínio

| Serviço | Responsabilidade |
|---|---|
| `PedidoService` | Criação, transição de status e pagamento de pedidos |
| `FinanceiroService` | Cálculo de impostos (10%) e descontos (7% para clientes fidelizados) |
| `EstoqueService` | Verifica disponibilidade e debita estoque via HTTP no `servico-estoque` |
| `EntregaService` | Publica pedido na fila RabbitMQ para processamento assíncrono |
| `CozinhaService` | Simula o ciclo de preparo: AGUARDANDO → PREPARACAO → PRONTO |
| `ClienteService` | Cadastro e recuperação de clientes |

---

## Serviços

| Serviço | Porta (host) | Tecnologia | Banco | Instâncias |
|---|---|---|---|---|
| `eureka-server` | 8761 | Spring Cloud Netflix Eureka | – | 1 |
| `gateway` | 8765 | Spring Cloud Gateway + JWT | – | 1 |
| `servico-pizzaria` | – (interno 8080) | Spring Boot 3.5.4 | H2 in-memory | 1 |
| `servico-estoque` | – (interno 8081) | Spring Boot 3.4.5 + JPA | PostgreSQL 16 | 3 |
| `servico-entrega` | – (interno 8082) | Spring Boot 3.4.5 + AMQP | – | 3 |
| `rabbitmq` | 5672 / 15672 | RabbitMQ 3 + Management | – | 1 |
| `postgres-estoque` | – (interno 5432) | PostgreSQL 16 | – | 1 |

---

## Pré-requisitos

- Docker Desktop instalado e em execução
- Portas `8761` e `8765` livres no host

---

## Como Executar

### Subir toda a stack (com múltiplas instâncias)

```bash
docker compose up --build -d --scale servico-estoque=3 --scale servico-entrega=3
```

### Subir com instância única (mais leve para desenvolvimento)

```bash
docker compose up --build -d
```

### Parar e limpar volumes (reset completo do banco)

```bash
docker compose down -v
```

### Ver logs de um serviço

```bash
docker compose logs servico-pizzaria -f
docker compose logs servico-estoque -f
docker compose logs servico-entrega -f
```

### Verificar status dos containers

```bash
docker compose ps
```

---

## Dados Pré-carregados

O banco H2 do monólito e o PostgreSQL do serviço de estoque são populados automaticamente no primeiro boot.

### Pizzas (Cardápio)

| ID | Produto | Preço |
|---|---|---|
| 1 | Pizza calabresa | R$ 55,00 |
| 2 | Pizza queijo e presunto | R$ 60,00 |
| 3 | Pizza margherita | R$ 40,00 |

### Clientes

| Nome | Email | Senha | CPF |
|---|---|---|---|
| Teste | teste@email.com | 123456 | 12345678901 |
| Huguinho Pato | huguinho.pato@email.com | 123456 | 9001 |
| Zezinho Pato | zezinho.pato@email.com | 123456 | 9002 |

### Estoque inicial

30 unidades de cada ingrediente (IDs 1 a 9). Cada pizza consome os ingredientes definidos em sua receita cadastrada no banco.

### Pedidos de exemplo (já no banco)

| ID | Cliente CPF | Produto | Status |
|---|---|---|---|
| 10 | 12345678901 | Pizza calabresa | ENTREGUE |
| 11 | 12345678901 | Pizza margherita | ENTREGUE |
| 12 | 12345678901 | Pizza queijo e presunto | ENTREGUE |
| 13 | 9001 (Huguinho) | Pizza calabresa | ENTREGUE |
| 14 | 9002 (Zezinho) | Pizza margherita | ENTREGUE |

---

## Casos de Uso

### UC1 – Registrar cliente no sistema

Permite que um novo cliente se cadastre informando nome, CPF, celular, endereço, email e senha. A senha é criptografada antes de ser persistida.

**Rota:** `POST /clientes`

### UC2 – Autenticar no sistema

O cliente se autentica com email e senha para obter um token JWT. O token deve ser enviado no header `Authorization: Bearer <token>` em todas as requisições subsequentes. A validação do token é feita pelo **Gateway** antes de repassar ao monólito.

**Rota:** `POST /auth/login`

### UC3 – Carregar cardápio

Retorna o cardápio disponível com todos os produtos e preços. O `CardapioPresenter` formata a saída destacando sugestões do chef.

**Rotas:** `GET /cardapio/corrente` · `GET /cardapio/lista`

### UC4 – Submeter pedido para aprovação

O cliente envia uma lista de itens. O sistema:
1. Verifica disponibilidade de ingredientes via HTTP no `servico-estoque`
2. Se disponível: debita o estoque, calcula impostos (10%) e desconto (7% para clientes com mais de 3 pedidos nos últimos 20 dias), salva o pedido com status `APROVADO`
3. Se indisponível: salva o pedido com status `NEGADO`

**Rota:** `POST /pedido/submeter`

### UC5 – Consultar status do pedido

Retorna o status atual de um pedido pelo ID.

**Rota:** `GET /pedido/{id}/status`

### UC6 – Cancelar pedido

Cancela um pedido que esteja com status `APROVADO` (ainda não pago). Pedidos em outros estados não podem ser cancelados.

**Rota:** `PATCH /pedido/{id}/cancelar`

### UC7 – Pagar pedido

Processa o pagamento. O fluxo de status após o pagamento é:
`PAGO → AGUARDANDO → PREPARACAO → PRONTO → TRANSPORTE`

Ao atingir `TRANSPORTE`, o ID do pedido é publicado na fila `pizzaria.entregas` do RabbitMQ. O `servico-entrega` consome a mensagem, aguarda ~5 segundos e chama de volta o monólito para marcar como `ENTREGUE`.

**Rota:** `PATCH /pedido/{id}/pagar`

### UC8 – Listar pedidos entregues entre duas datas

Relatório de todos os pedidos com status `ENTREGUE` em um intervalo de datas.

**Rota:** `GET /pedido/entregues?inicio=...&fim=...`

### UC9 – Listar pedidos entregues de um cliente entre duas datas

Mesmo que UC8, mas filtrado por CPF do cliente.

**Rota:** `GET /pedido/entregues/cliente/{cpf}?inicio=...&fim=...`

---

## Rotas e Exemplos

**Base URL:** `http://localhost:8765`

> Todas as rotas exceto `/auth/login` e `/clientes` exigem o header:  
> `Authorization: Bearer <token>`

---

### Autenticação

#### UC2 – Login
```
POST /auth/login
```
```json
{
  "email": "huguinho.pato@email.com",
  "senha": "123456"
}
```
**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

#### UC1 – Cadastrar cliente
```
POST /clientes
```
```json
{
  "cpf": "99988877766",
  "nome": "Maria Oliveira",
  "celular": "51999998888",
  "endereco": "Av. Ipiranga, 500",
  "email": "maria@email.com",
  "senha": "minhasenha"
}
```
**Resposta:** `201 Created` com os dados do cliente criado.

---

### Cardápio

#### UC3 – Cardápio corrente
```
GET /cardapio/corrente
```
**Resposta:**
```json
{
  "id": 1,
  "nome": "Cardápio Principal",
  "produtos": [
    { "id": 1, "descricao": "Pizza calabresa", "preco": 5500 },
    { "id": 2, "descricao": "Pizza queijo e presunto", "preco": 6000 },
    { "id": 3, "descricao": "Pizza margherita", "preco": 4000 }
  ]
}
```

#### UC3 – Listar todos os cardápios
```
GET /cardapio/lista
```

---

### Pedidos

#### UC4 – Submeter pedido
```
POST /pedido/submeter
```
```json
{
  "cpf": "9001",
  "enderecoEntrega": "Rua das Flores, 100",
  "itens": [
    { "produtoId": 1, "quantidade": 1 }
  ]
}
```
**Resposta (pedido aprovado):**
```json
{
  "id": 15,
  "clienteCpf": "9001",
  "enderecoEntrega": "Rua das Flores, 100",
  "status": "APROVADO",
  "valor": 5500,
  "impostos": 550,
  "desconto": 0,
  "valorCobrado": 6050,
  "itens": [
    { "produtoId": 1, "quantidade": 1, "valorUnitario": 5500 }
  ]
}
```

**Resposta (estoque insuficiente):**
```json
{
  "id": 16,
  "status": "NEGADO",
  ...
}
```

---

#### UC5 – Consultar status do pedido
```
GET /pedido/{id}/status
```
**Resposta:**
```
"APROVADO"
```

Ciclo completo de status:

| Status | Descrição |
|---|---|
| `APROVADO` | Pedido criado, aguardando pagamento |
| `NEGADO` | Estoque insuficiente no momento do pedido |
| `CANCELADO` | Cancelado pelo cliente antes do pagamento |
| `PAGO` | Pagamento confirmado |
| `AGUARDANDO` | Na fila da cozinha |
| `PREPARACAO` | Em preparo na cozinha |
| `PRONTO` | Pronto para sair para entrega |
| `TRANSPORTE` | Saiu para entrega — mensagem publicada no RabbitMQ |
| `ENTREGUE` | Entregue — confirmado pelo `servico-entrega` |

---

#### UC6 – Cancelar pedido
```
PATCH /pedido/{id}/cancelar
```
Sem body. Só funciona para pedidos com status `APROVADO`.

**Resposta:** `200 OK` com o pedido atualizado (status `CANCELADO`).

---

#### UC7 – Pagar pedido
```
PATCH /pedido/{id}/pagar
```
Sem body. Inicia o fluxo automático de preparo e dispara a entrega via RabbitMQ.  
Após ~5 segundos, o `servico-entrega` atualiza automaticamente para `ENTREGUE`.

**Resposta:** `200 OK` com o pedido atualizado.

---

#### UC8 – Listar pedidos entregues (todos os clientes)
```
GET /pedido/entregues?inicio=2026-05-01T00:00:00&fim=2026-06-30T23:59:59
```
**Resposta:**
```json
[
  {
    "id": 10,
    "clienteCpf": "12345678901",
    "status": "ENTREGUE",
    "valor": 5500,
    "impostos": 550,
    "desconto": 0,
    "valorCobrado": 6050,
    "dataEntrega": "2026-05-15T11:00:00"
  }
]
```

---

#### UC9 – Listar pedidos entregues de um cliente
```
GET /pedido/entregues/cliente/{cpf}?inicio=2026-05-01T00:00:00&fim=2026-06-30T23:59:59
```
**Exemplo para Huguinho Pato:**
```
GET /pedido/entregues/cliente/9001?inicio=2026-05-01T00:00:00&fim=2026-06-30T23:59:59
```

---

### Serviço de Estoque (acesso direto para testes)

> Normalmente chamado apenas internamente pelo monólito via Eureka.  
> Para acesso direto em testes, adicionar `ports: ["8081:8081"]` ao `servico-estoque` no compose.

#### Verificar disponibilidade de ingredientes
```
POST http://localhost:8081/estoque/disponibilidade
```
```json
{ "1": 1, "3": 1, "5": 1 }
```
*(chave = ingredienteId, valor = quantidade necessária)*  
**Resposta:** `true` ou `false`

#### Baixar estoque manualmente
```
PUT http://localhost:8081/estoque/baixa
```
```json
{ "1": 1, "3": 1, "5": 1 }
```
**Resposta:** `200 OK`

---

## Fluxo Completo de um Pedido

```
1. POST /auth/login
   → Gateway identifica rota pública, repassa ao monólito
   → Monólito valida credenciais, gera JWT
   → Cliente recebe token

2. POST /pedido/submeter  (Bearer token)
   → Gateway valida JWT
   → Monólito chama servico-estoque: POST /estoque/disponibilidade
   → Estoque confirma disponibilidade
   → Monólito chama servico-estoque: PUT /estoque/baixa
   → Monólito calcula imposto e desconto
   → Retorna pedido com status APROVADO

3. PATCH /pedido/{id}/pagar  (Bearer token)
   → Monólito avança status: PAGO → AGUARDANDO → PREPARACAO → PRONTO → TRANSPORTE
   → Publica ID do pedido na fila "pizzaria.entregas"
   → Retorna resposta ao cliente imediatamente

         ↓ assíncrono (~5 segundos)

4. servico-entrega consome mensagem da fila
   → Aguarda 5s (simulação do tempo de entrega)
   → Chama PATCH /pedido/{id}/entregar no monólito via Eureka
   → Monólito atualiza status para ENTREGUE

5. GET /pedido/{id}/status  (Bearer token)
   → Retorna "ENTREGUE"
```

---

## Monitoramento

### Eureka – Instâncias registradas
```
http://localhost:8761
```
Exibe todos os serviços registrados e quantas instâncias estão ativas.

### RabbitMQ Management UI
```
http://localhost:15672
```
- Usuário: `guest`
- Senha: `guest`

Permite visualizar a fila `pizzaria.entregas`, taxa de mensagens e consumers ativos.

### Verificar load balancing em ação

```bash
# Ver qual instância do estoque atendeu cada requisição (round-robin HTTP)
docker compose logs servico-estoque | grep "instância"

# Ver qual instância de entrega consumiu cada mensagem (competing consumers RabbitMQ)
docker compose logs servico-entrega | grep "instância"
```

Saída esperada com 3 pedidos submetidos em sequência:
```
servico-estoque-1 | [instância 2fc2e2b47bf4] verificando disponibilidade
servico-estoque-2 | [instância 547a3225f1d9] verificando disponibilidade
servico-estoque-3 | [instância e886036844db] verificando disponibilidade
```

---

## Estrutura do Projeto

```
animated-guacamole/
│
├── docker-compose.yml                   # orquestração de todos os serviços
├── README.md
│
├── eureka-server/                       # servidor de descoberta de serviços
│   ├── src/main/java/.../
│   │   └── EurekaServerApplication.java
│   ├── src/main/resources/application.yaml
│   ├── Dockerfile
│   └── pom.xml
│
├── gateway/                             # ponto de entrada único + validação JWT
│   ├── src/main/java/.../
│   │   └── JwtAuthFilter.java           # GlobalFilter que valida Bearer token
│   ├── src/main/resources/application.yaml
│   ├── Dockerfile
│   └── pom.xml
│
├── servico-estoque/                     # microsserviço de estoque (JPA + PostgreSQL)
│   ├── src/main/java/.../
│   │   ├── entidade/ItemEstoque.java
│   │   ├── repositorio/ItemEstoqueRepository.java
│   │   ├── servico/EstoqueService.java
│   │   └── controlador/EstoqueController.java
│   ├── src/main/resources/
│   │   ├── application.yaml
│   │   └── data.sql                     # popula 30 unidades de cada ingrediente
│   ├── Dockerfile
│   └── pom.xml
│
├── servico-entrega/                     # microsserviço de entrega (RabbitMQ consumer)
│   ├── src/main/java/.../
│   │   ├── listener/EntregaListener.java
│   │   ├── servico/EntregaService.java
│   │   └── config/RabbitMQConfig.java
│   ├── src/main/resources/application.yaml
│   ├── Dockerfile
│   └── pom.xml
│
└── ex5-pizzaria-clean-baseT1/           # monólito – núcleo de negócio (Clean Architecture)
    ├── src/main/java/.../
    │   ├── Adaptadores/
    │   │   ├── Apresentacao/            # Controllers REST (Auth, Pedido, Cardápio, Cliente)
    │   │   ├── Config/                  # SecurityConfig, JwtService, RabbitMQConfig, RestTemplateConfig
    │   │   └── Dados/                   # Repositórios JDBC (Pedido, Cliente, Cardápio, Estoque)
    │   ├── Aplicacao/                   # Casos de Uso UC1–UC9
    │   └── Dominio/
    │       ├── Entidades/               # Pedido, Cliente, Produto, Cardápio, StatusPedido
    │       └── Servicos/                # PedidoService, FinanceiroService, EstoqueService, EntregaService
    ├── src/main/resources/
    │   ├── application.yaml
    │   ├── schema.sql                   # DDL do banco H2
    │   └── data.sql                     # pizzas, clientes e pedidos de exemplo
    ├── Dockerfile
    └── pom.xml
```

---

## Decisões Técnicas Relevantes

**Por que o monólito roda com apenas 1 instância?**  
O banco H2 é in-memory e fica dentro do processo Java. Escalar o monólito criaria múltiplos bancos isolados — um pedido criado na instância A não existiria na instância B. Os microsserviços de estoque (PostgreSQL externo compartilhado) e entrega (stateless) não têm essa limitação.

**Por que RabbitMQ para entregas e não HTTP síncrono?**  
Entregas demoram tempo. Com HTTP síncrono, o cliente ficaria bloqueado aguardando o processamento. Com a fila, o monólito publica a mensagem e responde imediatamente. Além disso, se o `servico-entrega` cair, a mensagem permanece na fila e é processada quando ele voltar — garantia de entrega que HTTP não oferece.

**Por que `ON CONFLICT DO NOTHING` no data.sql do estoque?**  
Com 3 instâncias compartilhando o mesmo PostgreSQL, cada uma executa o `data.sql` ao iniciar. Sem essa cláusula, os dados seriam triplicados e o `findByIngredienteId` retornaria 3 resultados em vez de 1, causando `NonUniqueResultException`.

**Por que `ddl-auto: update` em vez de `create` no estoque?**  
Com `create`, cada instância tentaria recriar a tabela do zero ao iniciar, gerando conflito de DDL entre as 3. Com `update`, o Hibernate aplica apenas alterações incrementais necessárias, sem recriar.

**Por que a autenticação JWT foi movida para o Gateway?**  
Centralizar a validação do token no Gateway evita que requisições inválidas sequer cheguem ao monólito, reduzindo carga desnecessária. O monólito ainda gera o token no login, mas a validação em todas as demais rotas é responsabilidade exclusiva do Gateway.
