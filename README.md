# 🛒 MKT-GUS – Sistema de Autoatendimento

Projeto desenvolvido como **PI do 3º semestre**, com o objetivo de explorar o funcionamento de um sistema de autoatendimento em mercados.

---

## 🎯 Objetivo

Simular um sistema de autoatendimento, utilizando recursos como câmera do celular, leitura de código de barras e integração com a API do Mercado Livre para obter informações dos produtos.

---

## 🖥️ Tecnologias Utilizadas

- **Backend:** Java + Spring Boot (`/autoatendimento`)
- **Frontend:** React + Next.js + TypeScript + Tailwind CSS (`/self-checkout`)
- **Integrações:** API Mercado Livre, Biblioteca Barcode, DroidCam

---

## 📦 Estrutura do Projeto

```
/autoatendimento      # Backend Java Spring Boot
/self-checkout        # Frontend React/Next.js
```

---

## 🚀 Como rodar o projeto

### 1. Backend (Java + Spring Boot)

Abra um terminal e execute:

```bash
cd autoatendimento/autoatendimento
./mvnw clean package
./mvnw spring-boot:run
```

> O backend será iniciado na porta configurada (padrão: 8080).

---

### 2. Frontend (React + Next.js)

Abra outro terminal e execute:

```bash
cd self-checkout
npm install --legacy-peer-deps
npm run dev
```

> O frontend estará disponível em `http://localhost:3000`.

---

### 3. Observações Importantes

- **Terminais separados:** Rode backend e frontend em terminais diferentes.
- **Dependências:** O frontend utiliza Tailwind CSS e diversos componentes customizados.
- **API Mercado Livre:** Certifique-se de que o backend está configurado para consumir a API corretamente.
- **Leitura de código de barras:** Utilize DroidCam para transformar seu celular em um scanner.

---
