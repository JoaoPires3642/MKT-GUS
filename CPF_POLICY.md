# Política de Retenção e Uso do CPF

## Objetivo

Descrever como o CPF do cliente é coletado, armazenado, exibido e descartado no fluxo do totem de autoatendimento MKT-GUS, em conformidade com a LGPD (Lei 13.709/2018).

## Base Legal

Tratamento de dados para execução de contrato (art. 7º, V) e legítimo interesse para programa de fidelidade (art. 7º, IX).

## Ciclo de Vida do CPF

### 1. Coleta
- Informado voluntariamente pelo cliente no totem para acumular pontos de fidelidade.
- Coleta opcional — compras sem CPF são permitidas.
- Somente dígitos são armazenados internamente (formatação removida no frontend antes do envio).

### 2. Validação
- Validação de dígitos verificadores no domínio (`Customer.java`) antes de qualquer persistência.
- CPF inválido é rejeitado imediatamente, sem armazenamento.

### 3. Armazenamento
| Camada | Formato | TTL |
|--------|---------|-----|
| Cache em memória (`InMemoryCartCacheAdapter`) | String com dígitos | 5 minutos |
| Banco de dados PostgreSQL (`CustomerEntity`) | `BIGINT` (chave primária) | Indefinido (programa de fidelidade ativo) |
| Pedidos (`OrderEntity`) | `BIGINT` (FK para cliente) | Indefinido (histórico fiscal) |

### 4. Exibição
- CPF nunca é retornado completo nas respostas HTTP após identificação.
- Formato mascarado obrigatório: `***.XXX.XXX-**`
- Implementado em: `CartController.maskCpf()` e `OrderApiMapper.maskCpf()`.

### 5. Logs
- Nenhum log deve conter CPF completo.
- `CpfMaskingConverter` (logback) aplica mascaramento automático em todas as mensagens de log.
- `show-sql` desabilitado em produção para evitar CPF em queries SQL logadas.

### 6. Transporte
- CPF trafega somente via HTTPS (Nginx com TLS obrigatório em produção).
- Backend interno aceita somente conexões originadas do proxy Nginx.

### 7. Exclusão
- Cache expira automaticamente em 5 minutos após cancelamento do carrinho.
- Dados de clientes do programa de fidelidade são retidos enquanto a conta estiver ativa.
- Pedidos são retidos conforme obrigação fiscal (mínimo 5 anos, art. 195 CTN).

## Direitos do Titular

O cliente pode solicitar, a qualquer momento:
- Acesso aos dados armazenados.
- Correção de dados incorretos.
- Exclusão dos dados de fidelidade (encerra participação no programa).

Contato: [e-mail do DPO do estabelecimento]

## Critérios de Conformidade

- [ ] CPF não fica exposto desnecessariamente no totem
- [ ] Logs de aplicação não armazenam CPF completo sem necessidade
- [ ] Fluxos principais continuam funcionando após mascaramento
- [ ] Transporte via HTTPS em ambientes não locais