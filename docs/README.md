# Documentação do MKT-GUS

Sistema de autoatendimento (self-checkout) para supermercados.

---

## 📚 Índice

### Comece por aqui
- [Tutorial de Instalação](tutorial.md) - Como configurar e rodar o projeto

### Guias Técnicos
- [Arquitetura do Sistema](arquitetura.md) - Clean Architecture e estrutura do código
- [Endpoints da API](endpoints.md) - Todos os endpoints disponíveis
- [Integração com Sistema do Cliente](integracao-api.md) - Como trocar integrações externas

### Referências
- [Modelo de Banco de Dados](database-er-diagram.md) - Diagrama ER e tabelas

---

## 🚀 Começo Rápido

```bash
# 1. Clone o repositório
git clone https://github.com/JoaoPires3642/MKT-GUS.git

# 2. Configure o banco
mysql -u root -p -e "CREATE DATABASE mkt_gus;"

# 3. Rode o backend
cd autoatendimento && ./mvnw spring-boot:run

# 4. Rode o frontend (outro terminal)
cd self-checkout && npm install && npm run dev
```

Acesse: **http://localhost:3000**

---

## 🏗️ Arquitetura

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Frontend  │────▶│   Backend   │────▶│    Banco    │
│  (Next.js)  │     │   (Java)    │     │   (MySQL)   │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
                    ┌──────▼──────┐
                    │  Mercado     │
                    │  Livre API  │
                    └─────────────┘
```

### Camadas

| Camada | Descrição |
|--------|-----------|
| **Domain** | Regras de negócio puras |
| **Application** | Casos de uso |
| **Interfaces** | Controllers REST |
| **Infrastructure** | JPA, APIs externas |

[Ver documentação completa →](arquitetura.md)

---

## 🔌 Endpoints Principais

| Método | Endpoint | Descrição |
|--------|---------|-----------|
| `GET` | `/produtos/buscar/{barcode}` | Buscar produto |
| `POST` | `/pedidos/confirmar-compra` | Confirmar compra |
| `GET` | `/api/cupons` | Listar cupons |
| `POST` | `/pessoa/verificar-cpf` | Verificar cliente |
| `POST` | `/api/pontos/finalizar-compra` | Atualizar pontos |

[Ver todos os endpoints →](endpoints.md)

---

## 🔄 Integrações

O MKT-GUS suporta troca de integrações via configuração:

- **Catálogo de Produtos** - Mercado Livre (atual) ou sistema do cliente
- **Programa de Pontos** - Nosso sistema ou sistema do cliente
- **Funcionários** - Cadastro local ou integração

[Ver guia de integração →](integracao-api.md)

---

## 📊 Modelo de Dados

Principais tabelas:

- `customer` - Clientes (CPF + pontos)
- `order` - Pedidos
- `order_item` - Itens do pedido
- `coupon` - Cupons de desconto
- `market` - Cadastro de mercados

[Ver diagrama ER →](database-er-diagram.md)

---

## 👥 Equipe

| Membro | Função |
|--------|--------|
| João Vitor Pires | Backend, Integração |
| Arthur Fiorin | Backend, QA |
| Gustavo Furtado | Modelagem, Arquitetura |
| Lucas Amorim | Frontend, Documentação |
| Lucas Guedes | Frontend, Documentação |

---

## 📝 Licença

Projeto acadêmico - 3º Semestre