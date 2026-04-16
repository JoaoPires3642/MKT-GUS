# Workflow de Desenvolvimento

## Fluxo de Trabalho

```
Branch develop ─────┬─────▶ Merge via PR
                   │
Feature #1 ────────┤
Feature #2 ────────┤
Bugfix #3 ─────────┘
```

---

## 1. Branches

### Estrutura de Nomenclatura

| Tipo | Exemplo |
|------|---------|
| Feature | `feature/cadastro-cliente` |
| Bugfix | `bugfix/corrigir-login` |
| Docs | `docs/atualizar-readme` |
| Refactor | `refactor/clean-architecture` |

### Criar Branch

```bash
git checkout develop
git pull origin develop
git checkout -b feature/minha-feature
```

---

## 2. Commits

### Formato

```
<tipo>(<escopo>): <descrição>

[corpo opcional]
```

### Tipos

| Tipo | Uso |
|------|-----|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Documentação |
| `style` | Formatação (sem mudança de lógica) |
| `refactor` | Refatoração de código |
| `test` | Adicionar testes |
| `chore` | Tarefas de manutenção |

### Exemplos

```bash
git commit -m "feat(carrinho): adicionar remoção de item"
git commit -m "fix(pontos): corrigir cálculo de pontos"
git commit -m "docs(readme): atualizar instruções de setup"
```

---

## 3. Pull Requests

### Antes de Abrir PR

1. Atualize sua branch com `develop`
2. Resolva conflitos
3. Teste localmente
4. Verifique lint e testes

```bash
git fetch origin
git rebase origin/develop
npm run lint
npm run test
./mvnw test
```

### Abrir PR

Título do PR deve seguir o mesmo formato de commit:

```
feat(carrinho): adicionar remoção de item
```

Descrição deve incluir:
- O que foi feito
- Por que foi feito
- Como testar
- Screenshots (se aplicável)

### Checklist do PR

- [ ] Código testado localmente
- [ ] Commits organizados e com mensagens claras
- [ ] Sem conflitos com `develop`
- [ ] Documentação atualizada (se necessário)
- [ ] Testes adicionados/atualizados

---

## 4. Code Review

### Processo

1. **Autor** abre PR
2. **Revisor(es)** recebem notificação
3. Revisor analisa código e comenta
4. Se aprovado → merge
5. Se reprovado → autor ajusta e reenvia

### O que revisar

| Item | Descrição |
|------|-----------|
| Lógica | O código faz o que deveria? |
| Nomenclatura | Variáveis e métodos estão bem nomeados? |
| Estrutura | Segue Clean Architecture? |
| Testes | Cobertura adequada? |
| Segurança | Dados sensíveis protegidos? |
| Performance | Algum gargalo potencial? |

### Comentários

- Seja construtivo
- Explique o porquê da sugestão
- Sugira alternativas quando discordar

---

## 5. Merge

### Regras

- **Não fazer merge direto no `main` ou `develop`**
- Todos os PRs precisam de **pelo menos 1 aprovação**
- Após aprovada, a branch é deletada após merge

### Squash Merge (Recomendado)

Mantém histórico limpo:

```
develop ─────────────────────▶
         └─▶ squashed feature
```

---

## 6. Issues

### Criar Issue

Título deve ser claro e descritivo.

Formatos:
- `Epic: [título]`
- `Story: [título]`
- `Bug: [título]`

Descrição deve incluir:
- Contexto/Problema
- O que precisa ser feito
- Critérios de aceitação

### Labels

| Label | Uso |
|-------|-----|
| `P0` | Prioridade máxima |
| `P1` | Prioridade alta |
| `P2` | Prioridade média |
| `Epic` | Trabalho grande |
| `Story` | Trabalho entregável |
| `Bug` | Correção |

---

## 7. Status do Projeto

### Colunas do Project Board

| Coluna | Significado |
|--------|-------------|
| **Backlog** | Issues criadas, aguardando priorização |
| **To Do** | Priorizado, pronto para começar |
| **In Progress** | Em desenvolvimento |
| **In Review** | PR aberto, aguardando aprovação |
| **Done** | Entregue e mergeado |

---

## Resumo

```
1. Criar branch      → feature/minha-feature
2. Desenvolver       → commits claros
3. Abrir PR         → descrever mudanças
4. Code Review      → revisar e aprovar
5. Merge            → squash merge
6. Atualizar Issue  → mover para Done
```