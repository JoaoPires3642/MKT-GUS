# Database ER Diagram - Modelo Proposto

> ✅ **IMPLEMENTADO**: Campos `pontos_valor_por_ponto` e `pontos_por_bloco` adicionados à tabela `market`.

```mermaid
erDiagram
    MARKET {
        bigint id PK "ID autoincremento"
        varchar nome
        varchar rua
        varchar numero
        varchar bairro
        varchar cidade
        varchar estado
        varchar cep
        boolean ativo
        decimal pontos_valor_por_ponto "R$ por ponto (padrão: 5)"
        int pontos_por_bloco "Pontos por bloco (padrão: 10)"
    }

    CUSTOMER {
        bigint cpf PK "CPF (chave primária)"
        int pontos
        timestamp data_cadastro
    }

    COUPON {
        bigint id PK "ID autoincremento"
        varchar nome
        varchar descricao
        decimal valor_desconto
        boolean desconto_em_porcentual
        int custo
        decimal min_purchase
        decimal max_discount
        varchar tipo "MKTGUS ou MERCADO"
        varchar codigo_externo "Código do cupom do mercado (se aplicável)"
    }

    ORDER {
        bigint id PK "ID autoincremento"
        bigint market_id FK "FK -> market(id)"
        bigint customer_cpf FK "FK -> customer(cpf)"
        bigint coupon_id FK "FK -> coupon(id), opcional"
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

    MARKET ||--o{ ORDER : "possui"
    CUSTOMER ||--o{ ORDER : "faz"
    COUPON ||--o{ ORDER : "aplicado_em"
    ORDER ||--o{ ORDER_ITEM : "possui"
    ORDER ||--o{ PRICE_OVERRIDE_AUDIT : "tem_auditado"
```

## Tabelas e Campos

### market
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| nome | VARCHAR(100) | NOT NULL |
| rua | VARCHAR(150) | |
| numero | VARCHAR(20) | |
| bairro | VARCHAR(100) | |
| cidade | VARCHAR(100) | |
| estado | VARCHAR(50) | |
| cep | VARCHAR(20) | |
| ativo | BOOLEAN | DEFAULT TRUE |
| pontos_valor_por_ponto | DECIMAL | DEFAULT 5.00 (R$ por ponto) |
| pontos_por_bloco | INT | DEFAULT 10 |

### customer
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| cpf | BIGINT | PK |
| pontos | INT | DEFAULT 0 |
| data_cadastro | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### coupon
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| nome | VARCHAR(50) | NOT NULL |
| descricao | VARCHAR(100) | |
| valor_desconto | DECIMAL | NOT NULL |
| desconto_em_porcentual | BOOLEAN | NOT NULL |
| custo | INT | NOT NULL, mínimo 2 |
| min_purchase | DECIMAL | |
| max_discount | DECIMAL | |
| tipo | VARCHAR(20) | 'MKTGUS' ou 'MERCADO' |
| codigo_externo | VARCHAR(50) | Código do cupom externally (se tipo='MERCADO') |

### order
| Coluna | Tipo | Restrições |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| market_id | BIGINT | FK -> market(id), NOT NULL |
| customer_cpf | BIGINT | FK -> customer(cpf), NOT NULL |
| coupon_id | BIGINT | FK -> coupon(id), nullable |
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

- **market** 1:N **order** - Cada mercado pode ter vários pedidos
- **customer** 1:N **order** - Cada cliente pode fazer vários pedidos
- **coupon** 0:1 **order** - Um cupom pode ser aplicado em um pedido (opcional)
- **order** 1:N **order_item** - Um pedido tem vários itens
- **order** 1:N **price_override_audit** - Um pedido pode ter várias auditorias de preço

## Decisões de Design Pendentes

1. **Cupons do mercado**: De onde vem a informação dos cupons do sistema do mercado? (API/integração ou manual)
2. **Funcionários**: Como será a validação de funcionários? (integração com sistema do mercado)
3. **Estrutura de endereços do market**: Os campos de endereço estão adequados ou precisam de normalização?

## Notas

- Produtos **não** são armazenados - vienen da API do Mercado Livre no momento da compra
- Funcionários **não** são armazenados - vêm do sistema do mercado quando precisam autorizar alteração de preço

## Lógica de Pontos

### Cálculo de pontos ao confirmar compra:
```java
// Se market.tem configuração de pontos: usar do market
// Se não: usar padrão global (application.yml)

// Padrão global: R$ 5 = 1 ponto (bloco de 10 pts = R$ 50)
int pontosGanhos = (valorTotal / taxaPorPonto) * pontosPorBloco;
```

### Configuração padrão (application.yml):
```yaml
pontos:
  valor_por_ponto: 5.0   # R$ 5 = 1 ponto
  pontos_por_bloco: 10     # ganha em bloco
```