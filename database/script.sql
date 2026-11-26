SET NAMES utf8mb4;

DROP DATABASE IF EXISTS controle_financeiro;
CREATE DATABASE controle_financeiro
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE controle_financeiro;

-- Tabelas
CREATE TABLE contas (
  id            BIGINT        NOT NULL AUTO_INCREMENT,
  nome          VARCHAR(100)  NOT NULL,
  tipo          VARCHAR(20)   NOT NULL,
  saldo_inicial DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (id),
  CONSTRAINT uk_contas_nome UNIQUE (nome),
  CONSTRAINT ck_contas_tipo CHECK (tipo IN ('CORRENTE', 'POUPANCA', 'CARTEIRA'))
) ENGINE=InnoDB;

CREATE TABLE categorias (
  id   BIGINT       NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  tipo VARCHAR(20)  NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_categorias_nome UNIQUE (nome),
  CONSTRAINT ck_categorias_tipo CHECK (tipo IN ('RECEITA', 'DESPESA'))
) ENGINE=InnoDB;

CREATE TABLE transacoes (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  descricao      VARCHAR(150)  NOT NULL,
  valor          DECIMAL(12,2) NOT NULL,
  vencimento     DATE          NOT NULL,
  tipo           VARCHAR(20)   NOT NULL,
  status         VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
  data_pagamento DATE          NULL,
  conta_id       BIGINT        NOT NULL,
  categoria_id   BIGINT        NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT ck_transacoes_valor  CHECK (valor > 0),
  CONSTRAINT ck_transacoes_tipo   CHECK (tipo IN ('RECEITA', 'DESPESA')),
  CONSTRAINT ck_transacoes_status CHECK (status IN ('PENDENTE', 'PAGA')),
  -- RESTRICT: o banco também impede excluir conta/categoria com transações
  CONSTRAINT fk_transacoes_conta FOREIGN KEY (conta_id)
    REFERENCES contas (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_transacoes_categoria FOREIGN KEY (categoria_id)
    REFERENCES categorias (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_transacoes_conta      ON transacoes (conta_id);
CREATE INDEX idx_transacoes_categoria  ON transacoes (categoria_id);
CREATE INDEX idx_transacoes_vencimento ON transacoes (vencimento);

-- ---------------------------------------------------------
-- Dados de exemplo
-- Datas relativas a hoje, para sempre existirem transações
-- do mês atual, pendentes futuras e atrasadas.
-- ---------------------------------------------------------

SET @hoje        = CURDATE();
SET @inicio_mes  = DATE_FORMAT(CURDATE(), '%Y-%m-01');
SET @mes_passado = DATE_SUB(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH);

INSERT INTO contas (nome, tipo, saldo_inicial) VALUES
  ('Banco Master',         'CORRENTE', 500.00),   -- id 1
  ('Poupança',             'POUPANCA', 1000.00),  -- id 2
  ('Carteira',             'CARTEIRA', 100.00);   -- id 3

INSERT INTO categorias (nome, tipo) VALUES
  ('Salário',      'RECEITA'),  -- id 1
  ('Freelancer',   'RECEITA'),  -- id 2
  ('Rendimentos',  'RECEITA'),  -- id 3
  ('Aluguel',      'DESPESA'),  -- id 4
  ('Alimentação',  'DESPESA'),  -- id 5
  ('Transporte',   'DESPESA');  -- id 6
  ('Academia',                'DESPESA');  -- id 7


INSERT INTO transacoes
  (descricao, valor, vencimento, tipo, status, data_pagamento, conta_id, categoria_id)
VALUES
  -- Pagas no mês atual
  ('Salário',              3500.00, @inicio_mes, 'RECEITA', 'PAGA', @inicio_mes, 1, 1),
  ('Aluguel',              1200.00, @inicio_mes, 'DESPESA', 'PAGA', @inicio_mes, 1, 4),
  ('Mercado do mês',        850.80, @inicio_mes, 'DESPESA', 'PAGA', @inicio_mes, 1, 5),
  ('Rendimento poupança',    12.35, @inicio_mes, 'RECEITA', 'PAGA', @inicio_mes, 2, 3),

  -- Pagas no mês passado
  ('Salário',              3500.00, @mes_passado, 'RECEITA', 'PAGA', @mes_passado, 1, 1),
  ('Aluguel',              1200.00, @mes_passado, 'DESPESA', 'PAGA', @mes_passado, 1, 4),
  ('Freelance site',        800.00, @mes_passado, 'RECEITA', 'PAGA', @mes_passado, 3, 2),
  ('Uber',                   38.90, @mes_passado, 'DESPESA', 'PAGA', @mes_passado, 3, 6),

  -- Pendentes com vencimento passado (API devolve como ATRASADA)
  ('Conta de luz',          189.90, DATE_SUB(@hoje, INTERVAL 10 DAY), 'DESPESA', 'PENDENTE', NULL, 1, 4),
  ('Internet',               99.90, DATE_SUB(@hoje, INTERVAL 5 DAY),  'DESPESA', 'PENDENTE', NULL, 1, 4),
  ('Freelance',              350.00, DATE_SUB(@hoje, INTERVAL 3 DAY),  'RECEITA', 'PENDENTE', NULL, 1, 2),
  ('Combustível',           150.00, DATE_SUB(@hoje, INTERVAL 1 DAY),  'DESPESA', 'PENDENTE', NULL, 3, 6),

  -- Pendentes no prazo
  ('Feira',                 120.00, DATE_ADD(@hoje, INTERVAL 2 DAY),  'DESPESA', 'PENDENTE', NULL, 3, 5),
  ('Condomínio',            420.00, DATE_ADD(@hoje, INTERVAL 5 DAY),  'DESPESA', 'PENDENTE', NULL, 1, 4),
  ('Salário',              3500.00, DATE_ADD(@hoje, INTERVAL 20 DAY), 'RECEITA', 'PENDENTE', NULL, 1, 1);
