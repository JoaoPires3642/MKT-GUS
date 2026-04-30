# Database ER Diagram - Estado Atual

> ✅ **IMPLEMENTADO**: Campo `market_id` adicionado à tabela `coupon` como identificador simples.
>
> ⚠️ **IMPORTANTE**: No backend atual nao existe tabela `market` nem relacionamento JPA de `coupon` para `market`.

```mermaid
erDiagram
    CUSTOMER {
        bigint cpf PK "CPF (chave primária)"
        int pontos
    }

    COUPON {
        bigint id PK "ID autoincremento"
        varchar nome
        varchar descricao
        decimal valor_desconto
        boolean desconto_em_porcentual
        int custo
        bigint market_id "Identificador do mercado, sem FK"
        decimal min_purchase
        decimal max_discount
    }

    ORDER {
        bigint id PK "ID autoincremento"
        bigint id_cliente FK "FK -> customer(cpf)"
        bigint id_cupom FK "FK -> coupon(id), opcional"
        datetime data_hora
        decimal valor_total
    }

    ORDER_ITEM {
        bigint id PK "ID autoincremento"
        bigint order_id FK "FK -> order(id)"
        varchar codigo_ean
        varchar nome_produto
        decimal valor_unitario
        int quantidade
        decimal valor_total_item
        boolean produto_maior_idade
    }

    PRICE_OVERRIDE_AUDIT {
        bigint id PK "ID autoincremento"
        bigint order_id FK "FK -> order(id)"
        varchar ean
        varchar product_name
        decimal original_unit_price
        decimal authorized_unit_price
        int quantity
        varchar employee_registration "Matrícula do funcionário (nosso ou do mercado)"
        varchar employee_type "MKTGUS ou MERCADO"
        varchar reason
        datetime authorized_at
    }

    CUSTOMER ||--o{ ORDER : "faz"
    COUPON ||--o{ ORDER : "aplicado_em"
    ORDER ||--o{ ORDER_ITEM : "possui"
    ORDER ||--o{ PRICE_OVERRIDE_AUDIT : "tem_auditado"
```

## Tabelas e Campos

### customer
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| cpf | BIGINT | PK |
| pontos | INT | DEFAULT 0 |

### coupon
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| nome | VARCHAR(50) | NOT NULL |
| descricao | VARCHAR(100) | |
| valor_desconto | DECIMAL | NOT NULL |
| desconto_em_porcentual | BOOLEAN | NOT NULL |
| custo | INT | NOT NULL, mínimo 2 |
| market_id | BIGINT | Nullable, sem FK no modelo atual |
| min_purchase | DECIMAL | |
| max_discount | DECIMAL | |

### order
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| id_cliente | BIGINT | FK -> customer(cpf), nullable |
| id_cupom | BIGINT | FK -> coupon(id), nullable |
| data_hora | DATETIME | NOT NULL |
| valor_total | DECIMAL | NOT NULL |

### order_item
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| order_id | BIGINT | FK -> order(id), NOT NULL |
| codigo_ean | VARCHAR(50) | Código de barras do produto |
| nome_produto | VARCHAR(255) | Nome do produto (via Mercado Livre) |
| valor_unitario | DECIMAL | Preço unitário |
| quantidade | INT | NOT NULL, DEFAULT 1 |
| valor_total_item | DECIMAL | valor_unitario * quantidade |
| produto_maior_idade | BOOLEAN | DEFAULT FALSE |

### price_override_audit
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| order_id | BIGINT | FK -> order(id), NOT NULL |
| ean | VARCHAR(64) | NOT NULL |
| product_name | VARCHAR(255) | NOT NULL |
| original_unit_price | DECIMAL | NOT NULL |
| authorized_unit_price | DECIMAL | NOT NULL |
| quantity | INT | NOT NULL |
| employee_registration | VARCHAR(50) | Matrícula do funcionário |
| employee_type | VARCHAR(20) | 'MKTGUS' ou 'MERCADO' |
| reason | VARCHAR(255) | Motivo da alteration de preço |
| authorized_at | DATETIME | NOT NULL |

## Relacionamentos

- **customer** 1:N **order** - Um cliente pode estar em varios pedidos; no modelo atual o pedido pode existir sem cliente identificado
- **coupon** 1:N **order** - Um cupom pode ser usado em varios pedidos; no pedido o vinculo e opcional
- **order** 1:N **order_item** - Um pedido tem vários itens
- **order** 1:N **price_override_audit** - Um pedido pode ter várias auditorias de preço

## Gap Atual

- `coupon.market_id` existe no banco/modelo, mas ainda nao referencia uma tabela `market`
- Nao existe validacao no fluxo de compra para conferir se o cupom pertence a um mercado especifico
- Tambem nao existe `market_id` em `order`, entao hoje nao ha como cruzar pedido x mercado no banco

## Notas

- Produtos **não** são armazenados - vêm da API do Mercado Livre no momento da compra
- O termo "mercado" no backend atual aparece como integracao externa com Mercado Livre, nao como entidade relacional

## Lógica de Pontos

### Cálculo de pontos ao confirmar compra:
```java
// Usa configuracao global da aplicacao

// Padrão global: R$ 5 = 1 ponto (bloco de 10 pts = R$ 50)
int pontosGanhos = (valorTotal / taxaPorPonto) * pontosPorBloco;
```

### Configuração padrão (application.yml):
```yaml
pontos:
  valor_por_ponto: 5.0   # R$ 5 = 1 ponto
  pontos_por_bloco: 10     # ganha em bloco
```
