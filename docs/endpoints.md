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
  "customerCpf": 52998224725,
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
