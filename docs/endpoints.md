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

**Erros:** `404` - Produto não encontrado

---

### Buscar Produto por Código (POST)

```
POST /produtos/buscar
```

**Request:**
```json
{ "barcode": "7891234567890" }
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

**Request:**
```json
{
  "customerCpf": "12345678900",
  "coupon": { "id": 1, "discountType": "fixed" },
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
  "items": [...]
}
```

---

## Cupons

### Listar Cupons

```
GET /api/cupons
```

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
    "minimumPurchase": 50.0
  }
]
```

---

## Clientes

### Verificar CPF

```
POST /pessoa/verificar-cpf
```

**Request:** `{ "cpf": "12345678900" }`

**Response:** `123456` (saldo de pontos)

---

## Pontos

### Finalizar Compra com Pontos

```
POST /api/pontos/finalizar-compra
```

**Request:**
```json
{ "cpf": "12345678900", "requiredPoints": 20 }
```

**Response:** `{ "message": "Compra finalizada com sucesso!" }`

---

## Funcionários

### Verificar Matrícula

```
POST /api/funcionarios/verificar-matricula
```

**Request:** `{ "registration": "12345" }`

**Response:** `{ "valid": true, "message": "Employee found." }`

---

## Códigos de Erro

| Código | Significado |
|--------|-------------|
| `400` | Bad Request - Dados inválidos |
| `404` | Not Found - Recurso não encontrado |
| `500` | Internal Server Error |

```json
{
  "error": "ValidationException",
  "message": "CPF invalido.",
  "timestamp": "2024-01-15T10:30:00"
}
```