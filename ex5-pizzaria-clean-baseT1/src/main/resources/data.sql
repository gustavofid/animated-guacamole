-- Inserção dos ingredientes
INSERT INTO ingredientes (id, descricao) VALUES (1, 'Disco de pizza');
INSERT INTO ingredientes (id, descricao) VALUES (2, 'Porcao de tomate');
INSERT INTO ingredientes (id, descricao) VALUES (3, 'Porcao de mussarela');
INSERT INTO ingredientes (id, descricao) VALUES (4, 'Porcao de presunto');
INSERT INTO ingredientes (id, descricao) VALUES (5, 'Porcao de calabresa');
INSERT INTO ingredientes (id, descricao) VALUES (6, 'Molho de tomate (200ml)');
INSERT INTO ingredientes (id, descricao) VALUES (7, 'Porcao de azeitona');
INSERT INTO ingredientes (id, descricao) VALUES (8, 'Porcao de oregano');
INSERT INTO ingredientes (id, descricao) VALUES (9, 'Porcao de cebola');

-- Inserção dos itens de estoque
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (1, 30, 1);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (2, 30, 2);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (3, 30, 3);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (4, 30, 4);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (5, 30, 5);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (6, 30, 6);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (7, 30, 7);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (8, 30, 8);
INSERT INTO itensEstoque (id, quantidade, ingrediente_id) VALUES (9, 30, 9);

-- Inserção das receitas 
INSERT INTO receitas (id, titulo) VALUES (1, 'Pizza calabresa');
INSERT INTO receitas (id, titulo) VALUES (2, 'Pizza queijo e presunto');
INSERT INTO receitas (id, titulo) VALUES (3, 'Pizza margherita');

-- Associação dos ingredientes à receita Pizza calabresa
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (1, 1); -- Disco de pizza
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (1, 6); -- Molho de tomate (200ml)
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (1, 3); -- Porcao de mussarela
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (1, 5); -- Porcao de calabresa
-- Associação dos ingredientes à receita Pizza queijo e presunto
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (2, 1); -- Disco de pizza
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (2, 6); -- Molho de tomate (200ml)
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (2, 3); -- Porcao de mussarela
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (2, 4); -- Porcao de presunto
-- Associação dos ingredientes à receita Pizza margherita
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (3, 1); -- Disco de pizza
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (3, 6); -- Molho de tomate (200ml)
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (3, 3); -- Porcao de mussarela
INSERT INTO receita_ingrediente (receita_id, ingrediente_id) VALUES (3, 8); -- Porcao de cebola

-- insercao dos produtos
INSERT INTO produtos (id,descricao,preco) VALUES (1,'Pizza calabresa',5500);
INSERT INTO produtos (id,descricao,preco) VALUES (2,'Pizza queijo e presunto',6000);
INSERT INTO produtos (id,descricao,preco) VALUES (3,'Pizza margherita',4000);

-- Associação dos produtos com as receitas
INSERT INTO produto_receita (produto_id,receita_id) VALUES(1,1);
INSERT INTO produto_receita (produto_id,receita_id) VALUES(2,2);
INSERT INTO produto_receita (produto_id,receita_id) VALUES(3,3);

-- Insercao dos cardapios
INSERT INTO cardapios (id,titulo) VALUES(1,'Cardapio de Agosto');
INSERT INTO cardapios (id,titulo) VALUES(2,'Cardapio de Setembro');

-- Associação dos cardapios com os produtos
INSERT INTO cardapio_produto (cardapio_id,produto_id) VALUES (1,1);
INSERT INTO cardapio_produto (cardapio_id,produto_id) VALUES (1,2);
INSERT INTO cardapio_produto (cardapio_id,produto_id) VALUES (1,3);

INSERT INTO cardapio_produto (cardapio_id,produto_id) VALUES (2,1);
INSERT INTO cardapio_produto (cardapio_id,produto_id) VALUES (2,3);

-- Inserção dos clientes para os pedidos de teste
INSERT INTO clientes (cpf, nome, celular, endereco, email, senha) VALUES ('12345678901', 'Cliente Teste', '51988887777', 'Rua de Teste, 100', 'teste@email.com', '123456');
INSERT INTO clientes (cpf, nome, celular, endereco, email, senha) VALUES ('9001', 'Huguinho Pato', '51985744566', 'Rua das Flores, 100', 'huguinho.pato@email.com', '123456');
INSERT INTO clientes (cpf, nome, celular, endereco, email, senha) VALUES ('9002', 'Zezinho Pato', '5199172079', 'Av. Central, 200', 'zezinho.pato@email.com', '123456');

-- Pedido 1: Entregue em 2026-05-15 (CPF 12345678901)
INSERT INTO pedidos (id, cliente_cpf, endereco_entrega, status, valor, impostos, desconto, valor_cobrado, data_criacao, data_hora_pagamento, data_entrega)
VALUES (10, '12345678901', 'Rua de Teste, 100', 'ENTREGUE', 5500, 550, 0, 6050, '2026-05-15 10:00:00', '2026-05-15 10:05:00', '2026-05-15 11:00:00');

-- Pedido 2: Entregue em 2026-05-20 (CPF 12345678901)
INSERT INTO pedidos (id, cliente_cpf, endereco_entrega, status, valor, impostos, desconto, valor_cobrado, data_criacao, data_hora_pagamento, data_entrega)
VALUES (11, '12345678901', 'Rua de Teste, 100', 'ENTREGUE', 4000, 400, 0, 4400, '2026-05-20 12:00:00', '2026-05-20 12:10:00', '2026-05-20 13:00:00');

-- Pedido 3: Entregue em 2026-05-21 (CPF 12345678901)
INSERT INTO pedidos (id, cliente_cpf, endereco_entrega, status, valor, impostos, desconto, valor_cobrado, data_criacao, data_hora_pagamento, data_entrega)
VALUES (12, '12345678901', 'Rua de Teste, 100', 'ENTREGUE', 6000, 600, 0, 6600, '2026-05-21 09:00:00', '2026-05-21 09:15:00', '2026-05-21 10:00:00');

-- Pedido 4: Entregue em 2026-05-18 (CPF 9001)
INSERT INTO pedidos (id, cliente_cpf, endereco_entrega, status, valor, impostos, desconto, valor_cobrado, data_criacao, data_hora_pagamento, data_entrega)
VALUES (13, '9001', 'Rua das Flores, 100', 'ENTREGUE', 5500, 550, 0, 6050, '2026-05-18 14:00:00', '2026-05-18 14:10:00', '2026-05-18 15:00:00');

-- Pedido 5: Entregue em 2026-05-19 (CPF 9002)
INSERT INTO pedidos (id, cliente_cpf, endereco_entrega, status, valor, impostos, desconto, valor_cobrado, data_criacao, data_hora_pagamento, data_entrega)
VALUES (14, '9002', 'Av. Central, 200', 'ENTREGUE', 4000, 400, 0, 4400, '2026-05-19 11:00:00', '2026-05-19 11:05:00', '2026-05-19 12:00:00');

-- Itens para os pedidos de teste
INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, valor_unitario) VALUES (10, 1, 1, 5500);
INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, valor_unitario) VALUES (11, 3, 1, 4000);
INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, valor_unitario) VALUES (12, 2, 1, 6000);
INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, valor_unitario) VALUES (13, 1, 1, 5500);
INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, valor_unitario) VALUES (14, 3, 1, 4000);

-- Reinicia a sequência de IDs para evitar conflito com os IDs manuais inseridos acima
ALTER TABLE pedidos ALTER COLUMN id RESTART WITH 20;
