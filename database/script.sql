SET NAMES utf8mb4;

DROP DATABASE IF EXISTS controle_financeiro;
CREATE DATABASE controle_financeiro
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE controle_financeiro;

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
  CONSTRAINT fk_transacoes_conta FOREIGN KEY (conta_id)
    REFERENCES contas (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_transacoes_categoria FOREIGN KEY (categoria_id)
    REFERENCES categorias (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_transacoes_conta      ON transacoes (conta_id);
CREATE INDEX idx_transacoes_categoria  ON transacoes (categoria_id);
CREATE INDEX idx_transacoes_vencimento ON transacoes (vencimento);

SET @hoje         = CURDATE();
SET @inicio_mes   = DATE_FORMAT(CURDATE(), '%Y-%m-01');
SET @mes_passado  = DATE_SUB(@inicio_mes, INTERVAL 1 MONTH);
SET @proximo_mes  = DATE_ADD(@inicio_mes, INTERVAL 1 MONTH);

SET @mp_dia05 = DATE_ADD(@mes_passado, INTERVAL 4 DAY);
SET @mp_dia08 = DATE_ADD(@mes_passado, INTERVAL 7 DAY);
SET @mp_dia10 = DATE_ADD(@mes_passado, INTERVAL 9 DAY);
SET @mp_dia12 = DATE_ADD(@mes_passado, INTERVAL 11 DAY);
SET @mp_dia15 = DATE_ADD(@mes_passado, INTERVAL 14 DAY);
SET @mp_dia18 = DATE_ADD(@mes_passado, INTERVAL 17 DAY);
SET @mp_dia20 = DATE_ADD(@mes_passado, INTERVAL 19 DAY);
SET @mp_dia25 = DATE_ADD(@mes_passado, INTERVAL 24 DAY);

SET @dia01 = @inicio_mes;
SET @dia05 = LEAST(CAST(DATE_ADD(@inicio_mes, INTERVAL 4 DAY) AS DATE), CAST(@hoje AS DATE));
SET @dia08 = LEAST(CAST(DATE_ADD(@inicio_mes, INTERVAL 7 DAY) AS DATE), CAST(@hoje AS DATE));
SET @dia10 = LEAST(CAST(DATE_ADD(@inicio_mes, INTERVAL 9 DAY) AS DATE), CAST(@hoje AS DATE));
SET @dia12 = LEAST(CAST(DATE_ADD(@inicio_mes, INTERVAL 11 DAY) AS DATE), CAST(@hoje AS DATE));

SET @pm_dia05 = DATE_ADD(@proximo_mes, INTERVAL 4 DAY);

INSERT INTO contas (nome, tipo, saldo_inicial) VALUES
  ('Banco Master',         'CORRENTE', 1200.00),
  ('Banco do Brasil', 'CORRENTE', 350.00),
  ('Poupança Itaú', 'POUPANCA', 2500.00),
  ('Carteira',       'CARTEIRA', 200.00);

INSERT INTO categorias (nome, tipo) VALUES
  ('Salário',           'RECEITA'),
  ('Freelance',         'RECEITA'),
  ('Rendimentos',       'RECEITA'),
  ('Outras Receitas',   'RECEITA'),
  ('Moradia',           'DESPESA'),
  ('Contas da Casa',    'DESPESA'),
  ('Alimentação',       'DESPESA'),
  ('Delivery',          'DESPESA'),
  ('Transporte',        'DESPESA'),
  ('Saúde e Bem-estar', 'DESPESA'),
  ('Cuidados Pessoais', 'DESPESA'),
  ('Assinaturas',       'DESPESA'),
  ('Cartão de Crédito', 'DESPESA'),
  ('Lazer',             'DESPESA'),
  ('Educação',          'DESPESA');

INSERT INTO transacoes
  (descricao, valor, vencimento, tipo, status, data_pagamento, conta_id, categoria_id)
VALUES
  ('Salário',                3500.00, @mp_dia05, 'RECEITA', 'PAGA', @mp_dia05, 1,  1),
  ('Aluguel',                1200.00, @mp_dia05, 'DESPESA', 'PAGA', @mp_dia05, 1,  5),
  ('Academia',                 99.90, @mp_dia05, 'DESPESA', 'PAGA', @mp_dia05, 1, 10),
  ('Supermercado',            612.35, @mp_dia08, 'DESPESA', 'PAGA', @mp_dia08, 1,  7),
  ('Condomínio',              350.00, @mp_dia10, 'DESPESA', 'PAGA', @mp_dia10, 1,  5),
  ('Conta de Luz',            165.40, @mp_dia10, 'DESPESA', 'PAGA', @mp_dia10, 1,  6),
  ('Fatura do Cartão',        842.37, @mp_dia10, 'DESPESA', 'PAGA', @mp_dia10, 1, 13),
  ('iFood',                    58.70, @mp_dia12, 'DESPESA', 'PAGA', @mp_dia12, 1,  8),
  ('Conta de Água',            78.90, @mp_dia15, 'DESPESA', 'PAGA', @mp_dia15, 1,  6),
  ('Internet',                 99.90, @mp_dia15, 'DESPESA', 'PAGA', @mp_dia15, 1,  6),
  ('Netflix',                  44.90, @mp_dia15, 'DESPESA', 'PAGA', @mp_dia15, 1, 12),
  ('Barbearia',                45.00, @mp_dia18, 'DESPESA', 'PAGA', @mp_dia18, 4, 11),
  ('YouTube Premium',          24.90, @mp_dia20, 'DESPESA', 'PAGA', @mp_dia20, 1, 12),
  ('Uber',                     27.50, @mp_dia20, 'DESPESA', 'PAGA', @mp_dia20, 1,  9),
  ('Freelance - Site institucional', 900.00, @mp_dia25, 'RECEITA', 'PAGA', @mp_dia25, 2, 2),
  ('Rendimento da poupança',   11.80, @mp_dia25, 'RECEITA', 'PAGA', @mp_dia25, 3,  3),

  ('Rendimento da poupança',   12.35, @dia01, 'RECEITA', 'PAGA', @dia01, 3,  3),
  ('Salário',                3500.00, @dia05, 'RECEITA', 'PAGA', @dia05, 1,  1),
  ('Aluguel',                1200.00, @dia05, 'DESPESA', 'PAGA', @dia05, 1,  5),
  ('Academia',                 99.90, @dia05, 'DESPESA', 'PAGA', @dia05, 1, 10),
  ('Supermercado',            587.20, @dia08, 'DESPESA', 'PAGA', @dia08, 1,  7),
  ('Combustível',             150.00, @dia08, 'DESPESA', 'PAGA', @dia08, 2,  9),
  ('Fatura do Cartão',        915.62, @dia10, 'DESPESA', 'PAGA', @dia10, 1, 13),
  ('iFood',                    64.30, @dia10, 'DESPESA', 'PAGA', @dia10, 1,  8),
  ('Venda de fone usado',     120.00, @dia10, 'RECEITA', 'PAGA', @dia10, 4,  4),
  ('Barbearia',                45.00, @dia12, 'DESPESA', 'PAGA', @dia12, 4, 11),
  ('Spotify',                  21.90, @dia12, 'DESPESA', 'PAGA', @dia12, 1, 12),
  ('Farmácia',                 37.80, @dia12, 'DESPESA', 'PAGA', @dia12, 4, 10),

  ('Conta de Luz',            182.75, DATE_SUB(@hoje, INTERVAL 6 DAY), 'DESPESA', 'PENDENTE', NULL, 1,  6),
  ('Freelance - Logo para cliente', 350.00, DATE_SUB(@hoje, INTERVAL 4 DAY), 'RECEITA', 'PENDENTE', NULL, 2, 2),
  ('Conta de Água',            81.40, DATE_SUB(@hoje, INTERVAL 3 DAY), 'DESPESA', 'PENDENTE', NULL, 1,  6),
  ('Internet',                 99.90, DATE_SUB(@hoje, INTERVAL 1 DAY), 'DESPESA', 'PENDENTE', NULL, 1,  6),

  ('Netflix',                  44.90, DATE_ADD(@hoje, INTERVAL 2 DAY),  'DESPESA', 'PENDENTE', NULL, 1, 12),
  ('Cinema',                   64.00, DATE_ADD(@hoje, INTERVAL 3 DAY),  'DESPESA', 'PENDENTE', NULL, 4, 14),
  ('YouTube Premium',          24.90, DATE_ADD(@hoje, INTERVAL 4 DAY),  'DESPESA', 'PENDENTE', NULL, 1, 12),
  ('Condomínio',              350.00, DATE_ADD(@hoje, INTERVAL 5 DAY),  'DESPESA', 'PENDENTE', NULL, 1,  5),
  ('Curso de Inglês',         189.00, DATE_ADD(@hoje, INTERVAL 7 DAY),  'DESPESA', 'PENDENTE', NULL, 1, 15),
  ('Salário',                3500.00, @pm_dia05, 'RECEITA', 'PENDENTE', NULL, 1,  1),
  ('Aluguel',                1200.00, @pm_dia05, 'DESPESA', 'PENDENTE', NULL, 1,  5);
