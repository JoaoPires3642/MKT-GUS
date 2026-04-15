# 🛒 MKT-GUS – Sistema de Autoatendimento

Projeto de sistema de autoatendimento (self-checkout) para supermercados e varejo alimentar.

---

## 🎯 Objetivo

Permitir que o cliente escaneie seus próprios produtos, visualize informações automáticas via API externa, acumule pontos de fidelidade via CPF e finalize a compra de forma autônoma, sem necessidade de operador de caixa.

---

## 🖥️ Tecnologias

- **Backend:** Java 21 + Spring Boot 3
- **Frontend:** React + Next.js + TypeScript + Tailwind CSS
- **Banco de Dados:** MySQL
- **Integrações:** API Mercado Livre (catálogo produtos), DroidCam (scanner)

---

## 📦 Estrutura

```
/autoatendimento      # Backend Java Spring Boot
/self-checkout        # Frontend React/Next.js
/docs                # Documentação
```

---

## 🎁 Programa de Fidelidade

O sistema inclui programa de fidelidade baseado em pontos:

- **Acúmulo:** 1 ponto a cada R$ 5 gasto (configurável por mercado)
- **Resgate:** Cupons com custo mínimo de 2 pontos

Configuração em `application.yml`:
```yaml
pontos:
  valor-por-ponto: 5.0
  pontos-por-bloco: 10
```

---

## 🏪 Suporte a Multiplos Mercados

O MKT-GUS foi projetado para operar em múltiplos supermercados. Cada mercado pode ter:
- Configuração de pontos própria
- Cadastro com endereço
- Controle de cupons próprios ou integrados

---

## 🚀 Como Executar

### 1. Banco de Dados

```bash
# MySQL
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=mkt_gus mysql:8
```

### 2. Backend

```bash
cd autoatendimento
./mvnw clean package
./mvnw spring-boot:run
```

Backend disponível em `http://localhost:8080`.

### 3. Frontend

```bash
cd self-checkout
npm install --legacy-peer-deps
npm run dev
```

Frontend disponível em `http://localhost:3000`.

### 4. Variáveis de Ambiente

```bash
# Backend
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mkt_gus
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=sua_senha
```

---

## 📚 Documentação

- `docs/database-er-diagram.md` - Modelo de banco de dados
- `docs/clean-architecture-cheatsheet.md` - Arquitetura Clean Architecture

---

## 🔧 Configurações

### Pontos

O sistema de pontos pode ser configurado por mercado (banco) ou usar valores padrão:

| Propriedade | Padrão | Descrição |
|-------------|--------|-----------|
| `pontos.valor-por-ponto` | 5.0 | R$ equivalentes a 1 ponto |
| `pontos.pontos-por-bloco` | 10 | Pontos ganhos por bloco |

### Banco de Dados

| Propriedade | Padrão |
|-------------|--------|
| `SPRING_DATASOURCE_URL` | jdbc:mysql://localhost:3306/mkt_gus |
| `SPRING_DATASOURCE_USERNAME` | root |
| `SPRING_DATASOURCE_PASSWORD` | (vazio) |

---

## 📄 Licença

Projeto acadêmico - 3º Semestre.
