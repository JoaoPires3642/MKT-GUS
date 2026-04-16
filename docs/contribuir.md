# Guia de Contribuição

Obrigado por contribuir com o MKT-GUS! Este guia explica como você pode ajudar.

---

## Como Contribuir

### 1. Fork e Clone

```bash
# Fork no GitHub (botão "Fork" no repositório)
git clone https://github.com/SEU-USUARIO/MKT-GUS.git
cd MKT-GUS
```

### 2. Configurar Remote

```bash
git remote add upstream https://github.com/JoaoPires3642/MKT-GUS.git
```

### 3. Criar Feature Branch

```bash
git checkout develop
git pull upstream develop
git checkout -b feature/sua-feature
```

---

## Padrões de Código

### Backend (Java)

| Regra | Padrão |
|-------|--------|
| Classes | PascalCase: `CustomerService` |
| Métodos | camelCase: `findByCpf()` |
| Constantes | UPPER_SNAKE: `MAX_POINTS` |
| Interfaces | Sufixo `Gateway` ou `UseCase` |
| Entidades | Sufixo `Entity` ou sem sufixo |

### Frontend (TypeScript/React)

| Regra | Padrão |
|-------|--------|
| Componentes | PascalCase: `CustomerCard.tsx` |
| Funções | camelCase: `handleSubmit()` |
| Hooks | Prefixo `use`: `useCustomer()` |
| Arquivos | kebab-case: `customer-card.tsx` |

### Commits

```
<tipo>(<escopo>): <descrição>

feat(carrinho): adicionar item
fix(pontos): corrigir cálculo
docs(readme): atualizar instruções
```

---

## Estrutura do Projeto

```
MKT-GUS/
├── autoatendimento/      # Backend Java
│   ├── src/main/java/
│   │   ├── domain/          # Entidades e interfaces
│   │   ├── application/      # Use cases
│   │   ├── interfaces/       # Controllers
│   │   └── infrastructure/   # Implementações
│   └── src/main/resources/
│       └── application.yml   # Config
│
├── self-checkout/        # Frontend Next.js
│   ├── app/              # Páginas
│   ├── components/       # Componentes React
│   └── lib/              # Utilitários
│
└── docs/                 # Documentação
```

---

## Testes

### Backend

```bash
cd autoatendimento
./mvnw test
```

### Frontend

```bash
cd self-checkout
npm test
```

---

## Pull Requests

### Checklist

Antes de abrir PR:

- [ ] Código segue padrões de nomenclatura
- [ ] Commits organizados e descritivos
- [ ] Testes passaram
- [ ] Lint passou (`npm run lint` / `./mvnw checkstyle`)
- [ ] Documentação atualizada (se necessário)

### Template de PR

```markdown
## Descrição
O que foi feito?

## Motivação
Por que esta mudança é necessária?

## Como Testar
1. Passo 1
2. Passo 2
3. Resultado esperado

## Screenshots (se aplicável)
[Adicionar screenshots aqui]

## Issues Relacionadas
Closes #XX
```

---

## Bugs e Issues

### Reportar Bug

1. Verifique se já existe issue similar
2. Crie nova issue com标签 `Bug`
3. Inclua:
   - Passos para reproduzir
   - Comportamento esperado
   - Comportamento atual
   - Ambiente (SO, navegador, etc)

### Propor Feature

1. Verifique se já existe issue similar
2. Crie nova issue com标签 `Feature`
3. Inclua:
   - Descrição da funcionalidade
   - Caso de uso
   - Critérios de aceitação

---

## Dúvidas?

- Abra uma [Discussion](https://github.com/JoaoPires3642/MKT-GUS/discussions)
- Crie uma [Issue](https://github.com/JoaoPires3642/MKT-GUS/issues)

---

## Licença

Ao contribuir, você concorda que suas contribuições serão licenciadas sob a licença do projeto.