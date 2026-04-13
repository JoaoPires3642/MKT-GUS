# PDF x Codigo Gap Analysis

| Item do PDF | Status no codigo | Falta o que | Prioridade |
|---|---|---|---|
| RN01 Identificacao do produto por codigo de barras | Parcialmente atendido | Fluxo funciona, mas depende de catalogo externo inadequado para varejo real; falta fonte de catalogo/preco mais confiavel para loja | Alta |
| RN02 Identificacao do cliente por CPF | Atendido parcialmente | Ha validacao e vinculo de pontos, mas falta mascaramento melhor, politica de sessao e tratamento LGPD mais completo | Alta |
| RN03 Programa de pontos automatico | Atendido parcialmente | Backend ja centraliza melhor o calculo, mas ainda falta consolidar regras, historico e testes de ponta a ponta | Media |
| RN04 Transparencia de precos | Parcialmente atendido | Exibicao existe, mas divergencia entre etiqueta e catalogo exigiu override; falta UX clara para divergencia e politica formal no sistema | Alta |
| RN05 Autonomia do cliente | Parcialmente atendido | Fluxo principal existe, mas ha excecoes operacionais que precisam estar melhor modeladas como supervisao assistida | Media |
| RL01 LGPD | Nao atendido por completo | HTTPS/TLS real, mascaramento de CPF, politica de retencao, trilha de tratamento de dados, minimizacao e revisao de exposicao em tela | Alta |
| RL02 CDC Preco claro e correto | Parcialmente atendido | Precisa consolidar tratamento de divergencia de preco, mensagem clara ao cliente e auditoria operacional | Alta |
| RL03 Normas fiscais | Nao atendido | Emissao fiscal real ou integracao com modulo fiscal/NFC-e ainda nao existe | Alta |
| Seguranca: protecao do CPF | Parcialmente atendido | CPF ainda precisa de melhor ocultacao em UI, transporte seguro e revisao de logs/dados sensiveis | Alta |
| Seguranca: validacao de CPF | Atendido parcialmente | Existe validacao basica/consulta, mas falta cobertura mais forte e validacao de fluxo completo de sessao | Media |
| Seguranca: controle de sessao | Pouco atendido | Timeout, expiracao por inatividade, limpeza completa de sessao e isolamento entre clientes | Alta |
| Seguranca: integridade do carrinho | Parcialmente atendido | Melhorou com autorizacao de preco, mas falta consolidar todas as alteracoes sensiveis e travas de sessao | Alta |
| Acessibilidade: totem/touch targets | Parcialmente atendido | Precisa auditar tamanhos minimos, consistencia de acoes e uso real em totem | Media |
| Acessibilidade: contraste e legibilidade | Parcialmente atendido | Falta validacao sistematica dos contrastes e estados visuais | Media |
| Acessibilidade: fluxo simplificado | Parcialmente atendido | Fluxo existe, mas excecoes e popups precisam ser mais guiados e consistentes | Media |
| Acessibilidade: feedback visual | Parcialmente atendido | Ha notificacoes e popups, mas falta padronizar feedback claro em todos os eventos criticos | Media |
| Consulta automatica com nome, preco, imagem e descricao | Parcialmente atendido | Nome/preco/imagem existem; descricao completa do item nao esta clara no frontend | Media |
| Gestao do carrinho com revisao antes da compra | Atendido parcialmente | Funciona, mas precisa reforcar integridade, sessao e UX de excecoes | Media |
| Finalizacao com meios de pagamento digitais | Nao atendido de forma real | Tela existe, mas falta integracao real com pagamento | Alta |
| Dados estrategicos de consumo para o gestor | Pouco atendido | Ha pedidos/clientes, mas faltam historico consultavel, relatorios e visao analitica | Media |
| Supervisao humana em excecoes operacionais | Parcialmente atendido | Ja existe base para +18 e ajuste de preco, mas precisa consolidar como modulo coerente | Alta |
| Auditoria de intervencao do funcionario | Parcialmente atendido | Ja existe auditoria de override de preco, mas ainda falta expandir o modelo para outras excecoes e consulta administrativa | Media |
| Dependencia de API do Mercado Livre como catalogo principal | Atendido tecnicamente, fraco operacionalmente | Falta estrategia de fallback ou catalogo mais aderente ao supermercado real | Alta |

## Resumo

- Prioridade alta:
  - fiscal
  - LGPD/seguranca real
  - sessao de totem
  - pagamento real
  - transparencia/correcao de preco
  - catalogo confiavel
  - supervisao humana consolidada
- Prioridade media:
  - acessibilidade auditada
  - descricao completa
  - historico e analytics
  - evolucao do programa de pontos
