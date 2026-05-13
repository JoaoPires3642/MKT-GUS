# Endpoints da API

Base URL: `http://localhost:8080`

## Principais Rotas

| Metodo | Endpoint | Descricao |
|---|---|---|
| `POST` | `/produtos/buscar` | Buscar produto por barcode |
| `GET` | `/produtos/buscar/{barcode}` | Buscar produto por barcode via path |
| `POST` | `/pessoa/verificar-cpf` | Consultar cliente e saldo de pontos |
| `GET` | `/api/cupons` | Listar cupons |
| `POST` | `/api/funcionarios/verificar-matricula` | Validar matricula |
| `POST` | `/pagamentos/iniciar` | Iniciar transacao digital |
| `GET` | `/pagamentos/{paymentId}` | Consultar status do pagamento |
| `POST` | `/pagamentos/{paymentId}/confirmar` | Confirmar pagamento |
| `POST` | `/pedidos/confirmar-compra` | Fechar compra com pagamento confirmado |
| `GET` | `/pedidos/historico-cliente` | Consultar historico minimo por CPF |
| `GET` | `/pedidos/admin/historico` | Consultar historico operacional com filtros |
| `GET` | `/pedidos/admin/relatorio-simples` | Consultar base analitica minima |
| `POST` | `/pedidos/admin/relatorio-semanal/enviar` | Enviar relatorio semanal por e-mail agora |

## Produtos

### `POST /produtos/buscar`

Request:

```json
{
  "barcode": "9999999999999"
}
```

Response:

```json
{
  "ean": "9999999999999",
  "name": "Produto Homologacao Checkout",
  "price": 19.9,
  "imageUrl": null,
  "adultOnly": false
}
```

## Cliente

### `POST /pessoa/verificar-cpf`

Request:

```json
{
  "cpf": "52998224725"
}
```

Response:

```json
120
```

## Funcionario

### `POST /api/funcionarios/verificar-matricula`

Request:

```json
{
  "matricula": "12345"
}
```

Response:

```json
{
  "valid": true,
  "message": "Employee found."
}
```

## Pagamentos

### `POST /pagamentos/iniciar`

Request:

```json
{
  "method": "PIX",
  "amount": 19.9
}
```

Response:

```json
{
  "id": 1,
  "provider": "fake",
  "providerReference": "fake-123",
  "method": "PIX",
  "status": "PROCESSING",
  "amount": 19.9,
  "failureReason": null,
  "expiresAt": "2026-05-08T18:00:00",
  "confirmedAt": null
}
```

### `GET /pagamentos/{paymentId}`

Response esperada durante polling:

```json
{
  "id": 1,
  "provider": "fake",
  "providerReference": "fake-123",
  "method": "PIX",
  "status": "PAID",
  "amount": 19.9,
  "failureReason": null,
  "expiresAt": "2026-05-08T18:00:00",
  "confirmedAt": "2026-05-08T17:45:10"
}
```

### `POST /pagamentos/{paymentId}/confirmar`

Usado para confirmacao explicita quando o provider exigir acao final do backend.

## Pedido

### `POST /pedidos/confirmar-compra`

Request:

```json
{
  "clienteCpf": "52998224725",
  "paymentTransactionId": 1,
  "cupom": {
    "id": 1,
    "tipoDesconto": "percentage"
  },
  "itens": [
    {
      "ean": "9999999999999",
      "valorUnitario": 19.9,
      "quantidade": 1,
      "ajustePreco": null
    }
  ]
}
```

Regras importantes:

- a compra falha se `paymentTransactionId` nao existir
- a compra falha se o pagamento nao estiver confirmado
- a compra falha se o valor pago for diferente do total calculado

Response:

```json
{
  "id": 10,
  "customerCpf": "***.982.247-**",
  "couponId": 1,
  "orderedAt": "2026-05-08T17:45:11",
  "totalAmount": 17.91,
  "items": [
    {
      "ean": "9999999999999",
      "productName": "Produto Homologacao Checkout",
      "unitPrice": 19.9,
      "quantity": 1,
      "adultOnly": false,
      "totalPrice": 19.9
    }
  ],
  "updatedPointsBalance": 140,
  "taxDocument": {
    "status": "PENDING",
    "numeroDocumento": null,
    "chaveAcesso": null,
    "urlDanfe": null,
    "motivoFalha": "Modulo fiscal nao implementado. Reprocessar apos integracao."
  }
}
```

### `GET /pedidos/historico-cliente?cpf=52998224725`

Consulta minima de historico por cliente. O CPF e usado apenas como filtro de busca e retorna mascarado no payload.

Response:

```json
[
  {
    "id": 10,
    "marketId": 1,
    "customerCpf": "***.982.247-**",
    "couponId": 1,
    "orderedAt": "2026-05-08T17:45:11",
    "totalAmount": 17.91,
    "itemCount": 1,
    "items": [
      {
        "ean": "9999999999999",
        "productName": "Produto Homologacao Checkout",
        "unitPrice": 19.9,
        "quantity": 1,
        "adultOnly": false,
        "totalPrice": 19.9
      }
    ]
  }
]
```

### `GET /pedidos/admin/historico`

Consulta minima para gestor ou operacao. Filtros opcionais:

- `marketId`
- `from` no formato ISO date-time, exemplo `2026-05-01T00:00:00`
- `to` no formato ISO date-time, exemplo `2026-05-31T23:59:59`
- `limit`, com limite maximo interno de 500 registros

Retorna os mesmos campos de `historico-cliente`, mantendo CPF mascarado.

### `GET /pedidos/admin/relatorio-simples`

Base analitica minima para fidelizacao e relatorios. Filtros opcionais: `marketId`, `from`, `to`.

Response:

```json
{
  "from": "2026-05-01T00:00:00",
  "to": "2026-05-31T23:59:59",
  "marketId": 1,
  "totalOrders": 120,
  "identifiedOrders": 86,
  "anonymousOrders": 34,
  "itemsSold": 340,
  "totalRevenue": 4200.5,
  "averageTicket": 35.0,
  "topProducts": [
    {
      "ean": "9999999999999",
      "productName": "Produto Homologacao Checkout",
      "quantitySold": 40,
      "grossRevenue": 796.0
    }
  ]
}
```

Metadados de pedido preservados para evolucao analitica: `id`, `marketId`, CPF vinculado ao pedido, `couponId`, `orderedAt`, `totalAmount`, itens com `ean`, `productName`, `unitPrice`, `quantity`, `adultOnly` e `totalPrice`.

Minimizacao de dados pessoais: os endpoints de consulta retornam CPF mascarado e nao retornam dados cadastrais adicionais do cliente.

## Relatorio Semanal por E-mail

O backend possui um job agendado para enviar o relatorio simples de pedidos uma vez por semana.

Configuracao padrao:

- destinatario: `fiorinarthur18@gmail.com`
- agenda: toda segunda-feira as 08:00 no timezone `America/Sao_Paulo`
- periodo analisado: ultimos 7 dias
- mercado: `MERCADO_ID`, com fallback para `1`

Variaveis de ambiente principais:

```env
REPORTS_WEEKLY_ORDER_ENABLED=true
REPORTS_WEEKLY_ORDER_RECIPIENT_EMAIL=fiorinarthur18@gmail.com
REPORTS_WEEKLY_ORDER_MARKET_ID=1
REPORTS_WEEKLY_ORDER_CRON=0 0 8 * * MON
REPORTS_WEEKLY_ORDER_ZONE=America/Sao_Paulo
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-sender-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password-here
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS_ENABLE=true
```

O e-mail usa os mesmos dados agregados de `/pedidos/admin/relatorio-simples` e nao inclui CPF nem dados cadastrais de clientes.

### Envio sob demanda

Para enviar o mesmo relatorio hoje, sem esperar o proximo agendamento:

```http
POST /pedidos/admin/relatorio-semanal/enviar
```

Resposta esperada:

```http
202 Accepted
```

Esse endpoint usa o destinatario configurado em `REPORTS_WEEKLY_ORDER_RECIPIENT_EMAIL` e o mesmo periodo movel dos ultimos 7 dias.
