# Pending: Validação do Modelo de Banco de Dados

## Contexto

Preciso valider o modelo de banco de dados proposto com a equipe antes da implementação.

## Modelo Proposto

O banco foi simplificado para focar no que o MKT-GUS precisa:
- **Não guarda produtos** - vêm da API do Mercado Livre
- **Não guarda funcionários** - vêm do sistema do mercado

### Tabelas

| Tabela | Descrição |
|--------|------------|
| market | Cadastro dos supermercados (com endereço) |
| customer | Clientes fidelidade (CPF + pontos) |
| order | Pedidos realizados |
| order_item | Itens de cada pedido |
| coupon | Cupons (nosso e do mercado) |
| price_override_audit | Auditoria de preços alterados |

## Decisões Pendentes

1. **Cupons do mercado**: De onde vem? (API/integração ou manual?)
2. **Funcionários**: Como validar? (integração com sistema do mercado?)
3. **Endereço do market**: Campos estão adequados?

## Arquivo com Diagrama

Ver `docs/database-er-diagram.md`

---

**Ação**: Discutir com a equipe e confirmar modelo antes de implementar migration.