INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM10', '10% de desconto - válido para compras acima de R$ 50', 10.0, true, 10, 1, 50.0, 30.0
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM10');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM15', 'R$ 15 de desconto - válido para compras acima de R$ 100', 15.0, false, 20, 1, 100.0, NULL
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM15');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM05P', '5% de desconto - sem valor mínimo', 5.0, true, 5, 1, NULL, 10.0
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM05P');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM50', 'R$ 50 de desconto - válido para compras acima de R$ 300', 50.0, false, 40, 1, 300.0, NULL
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM50');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM20', '20% de desconto - válido para compras acima de R$ 150', 20.0, true, 30, 1, 150.0, 100.0
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM20');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM08', 'R$ 8 de desconto - sem valor mínimo', 8.0, false, 5, 1, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM08');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM15P', '15% de desconto - válido para compras acima de R$ 80', 15.0, true, 25, 1, 80.0, 50.0
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM15P');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM100', 'R$ 100 de desconto - válido para compras acima de R$ 500', 100.0, false, 80, 1, 500.0, NULL
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM100');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM25', '25% de desconto - válido para compras acima de R$ 200', 25.0, true, 50, 1, 200.0, 150.0
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM25');

INSERT INTO coupon_entity (nome, descricao, valor_desconto, desconto_em_porcentual, custo, market_id, min_purchase, max_discount)
SELECT 'CUPOM05F', 'R$ 5 de desconto - sem valor mínimo', 5.0, false, 2, 1, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM coupon_entity WHERE nome = 'CUPOM05F');
