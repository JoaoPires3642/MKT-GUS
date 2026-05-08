# Arquitetura do Sistema

## Visao Geral

O MKT-GUS e um monorepo com:

- backend Spring Boot modularizado em Maven
- frontend Next.js para o kiosk de autoatendimento
- integracoes externas desacopladas por portas e adapters

## Modulos Reais Do Backend

```text
autoatendimento/
  domain/        modelos de negocio puros
  application/   casos de uso, configs e contratos
  web.ui/        controllers REST e mappers HTTP
  infra-utils/   utilitarios tecnicos
  infra-data/    JPA e integracoes externas
  app/           bootstrap Spring Boot
```

## Regra Central

As regras de negocio devem conhecer contratos, nunca implementacoes concretas.

Exemplos atuais:

- catalogo de produtos via `ProductCatalogGateway`
- emissao fiscal via `TaxEmissionGateway`
- pagamento digital via `PaymentGateway`

## Fluxos Importantes

### Checkout

1. frontend monta carrinho
2. backend valida itens, cupom e ajustes de preco
3. pagamento digital e iniciado
4. frontend consulta status da transacao
5. compra so e confirmada quando a transacao estiver paga
6. pedido salvo dispara fluxo fiscal

### Fiscal

- `ConfirmPurchaseUseCase` chama `IssueTaxDocumentUseCase`
- persistencia do documento fiscal fica separada do pedido
- provider atual pode ser stub enquanto a integracao real nao existir

### Pagamento

- `PaymentTransaction` representa a transacao
- `PaymentGateway` abstrai o provedor
- `PaymentTransactionGateway` persiste o estado
- provider atual: `FakePaymentGateway`
- proximo passo natural: adapter real de mercado/maquininha

## Metodos de Pagamento Previstos

- `CREDIT`
- `DEBIT`
- `VALE`
- `PIX`

## Estados de Pagamento

- `PENDING`
- `PROCESSING`
- `AUTHORIZED`
- `PAID`
- `FAILED`
- `CANCELED`
- `EXPIRED`

## Fronteiras Da Arquitetura

### `domain`

- sem Spring
- sem JPA
- sem DTO HTTP

### `application`

- casos de uso
- configuracoes de negocio
- contratos de integracao

### `web.ui`

- controllers REST
- requests/responses
- mapeamento API -> use case

### `infra-data`

- entities JPA
- repositories
- gateways concretos
- integracoes Mercado Livre, fiscal fake, payment fake

## Frontend

O `self-checkout` funciona como kiosk e hoje contem:

- leitura de produtos
- CPF e pontos
- cupons
- ajuste de preco autorizado
- fluxo de pagamento assíncrono
- tela final de status de impressao fiscal

## Decisoes Atuais

- pedido nao pode ser fechado sem pagamento confirmado
- emissao fiscal e pagamento sao responsabilidades separadas
- homologacao e desenvolvimento podem usar adapters fake sem alterar os casos de uso
