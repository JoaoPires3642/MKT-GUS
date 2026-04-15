# Endpoints da API

Base URL: `http://localhost:8080`

---

## 📋 Sumário

| # | Método | Endpoint | Descrição |
|---|--------|---------|-----------|
| 1 | `GET` | `/produtos/listar` | Listar produtos escaneados |
| 2 | `GET` | `/produtos/buscar/{barcode}` | Buscar produto por código |
| 3 | `POST` | `/produtos/buscar` | Buscar produto (POST) |
| 4 | `POST` | `/pedidos/confirmar-compra` | Confirmar compra |
| 5 | `GET` | `/api/cupons` | Listar cupons |
| 6 | `POST` | `/pessoa/verificar-cpf` | Verificar CPF do cliente |
| 7 | `POST` | `/api/pontos/finalizar-compra` | Atualizar pontos |
| 8 | `POST` | `/api/funcionarios/verificar-matricula` | Verificar matrícula |

---

## Produtos

### Listar Produtos Escaneados

```
GET /produtos/listar
```

Lista todos os produtos escaneados na sessão atual.

**Response:**
```json
[
  {
    "ean": "7891234567890",
    "name": "Produto Exemplo",
    "price": 12.99,
    "imageUrl": "https://...",
    "adultOnly": false
  }
]
```

---

### Buscar Produto por Código (GET)

```
GET /produtos/buscar/{barcode}
```

Busca um produto pelo código de barras e adiciona à lista.

**Parâmetros:**
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| barcode | path | Código de barras (EAN/UPC) |

**Response:**
```json
{
  "ean": "7891234567890",
  "name": "Produto Exemplo",
  "price": 12.99,
  "imageUrl": "https://...",
  "adultOnly": false
}
```

**Erros:**
- `404` - Produto não encontrado

---

### Buscar Produto por Código (POST)

```
POST /produtos/buscar
```

Mesma funcionalidade do GET, mas via POST com body.

**Request:**
```json
{
  "barcode": "7891234567890"
}
```

**Response:**
```json
{
  "ean": "7891234567890",
  "name": "Produto Exemplo",
  "price": 12.99,
  "imageUrl": "https://...",
  "adultOnly": false
}
```

---

## Pedidos

### Confirmar Compra

```
POST /pedidos/confirmar-compra
```

Finaliza a compra, aplica cupom (se houver), atualiza pontos e persiste o pedido.

**Request:**
```json
{
  "customerCpf": "12345678900",
  "coupon": {
    "id": 1,
    "discountType": "fixed"
  },
  "items": [
    {
      "ean": "7891234567890",
      "unitPrice": 12.99,
      "quantity": 2,
      "priceOverride": {
        "authorizedUnitPrice": 10.00,
        "employeeRegistration": "12345",
        "reason": "Promoção interna"
      }
    }
  ]
}
```

**Response:**
```json
{
  "orderId": 1,
  "customerCpf": "12345678900",
  "totalAmount": 25.98,
  "pointsEarned": 200,
  "finalPointsBalance": 1200,
  "items": [
    {
      "ean": "7891234567890",
      "name": "Produto Exemplo",
      "unitPrice": 12.99,
      "quantity": 2,
      "totalPrice": 25.98
    }
  ]
}
```

**Erros:**
- `400` - CPF inválido, quantidade inválida, saldo insuficiente
- `404` - Cliente ou cupom não encontrado

---

## Cupons

### Listar Cupons

```
GET /api/cupons
```

Lista todos os cupons disponíveis para uso.

**Response:**
```json
[
  {
    "id": 1,
    "name": "Desconto R$10",
    "description": "Ganhe R$10 de desconto",
    "discountValue": 10.0,
    "discountType": "fixed",
    "cost": 20,
    "minimumPurchase": 50.0,
    "maximumDiscount": null
  },
  {
    "id": 2,
    "name": "Desconto 15%",
    "description": "Ganhe 15% de desconto",
    "discountValue": 15.0,
    "discountType": "percentage",
    "cost": 30,
    "minimumPurchase": 100.0,
    "maximumDiscount": 50.0
  }
]
```

---

## Clientes

### Verificar CPF

```
POST /pessoa/verificar-cpf
```

Verifica se o CPF existe e retorna os pontos atuais.

**Request:**
```json
{
  "cpf": "12345678900"
}
```

**Response:**
```json
123456
```

Retorna o saldo de pontos do cliente (integer).

**Erros:**
- `404` - Cliente não encontrado

---

## Pontos

### Finalizar Compra com Pontos

```
POST /api/pontos/finalizar-compra
```

Atualiza os pontos do cliente após uma compra.

**Request:**
```json
{
  "cpf": "12345678900",
  "requiredPoints": 20
}
```

**Response:**
```json
{
  "message": "Compra finalizada com sucesso!"
}
```

**Erros:**
- `400` - Saldo de pontos insuficiente

---

## Funcionários

### Verificar Matrícula

```
POST /api/funcionarios/verificar-matricula
```

Verifica se a matrícula do funcionário é válida (para autorização de alteração de preço).

**Request:**
```json
{
  "registration": "12345"
}
```

**Response:**
```json
{
  "valid": true,
  "message": "Employee found."
}
```

**Erros:**
- `400` - Matrícula inválida

---

## WebSocket

### Conexão

```
WS /ws
```

Conexão WebSocket para receber notificações de produtos escaneados (via DroidCam).

**Headers:**
```
Upgrade: websocket
```

**Mensagens Recebidas:**
```json
{
  "type": "PRODUCT_SCANNED",
  "ean": "7891234567890",
  "productName": "Produto Exemplo",
  "price": 12.99
}
```

---

## Códigos de Erro Comuns

| Código | Significado |
|--------|-------------|
| `400` | Bad Request - Dados inválidos |
| `404` | Not Found - Recurso não encontrado |
| `500` | Internal Server Error - Erro no servidor |

### Formato de Erro:

```json
{
  "error": "ValidationException",
  "message": "CPF invalido.",
  "timestamp": "2024-01-15T10:30:00"
}
```