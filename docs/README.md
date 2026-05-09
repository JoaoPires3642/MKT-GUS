# Documentacao do MKT-GUS

## Comece Por Aqui

- `../README.md`: visao geral do projeto e execucao
- `tutorial.md`: onboarding e setup manual

## Guias Principais

- `arquitetura.md`: arquitetura atual do sistema
- `backend-architecture.md`: regras de evolucao da clean architecture no backend
- `endpoints.md`: contratos HTTP principais do backend
- `integracao-api.md`: direcao para troca de integracoes externas

## Estado Atual Do Projeto

Hoje o projeto ja possui:

- fluxo de checkout com barcode, CPF, cupom e ajuste de preco
- emissao fiscal desacoplada por gateway
- base de pagamento digital plug-and-play com provider fake
- frontend de kiosk em Next.js integrado ao backend

## Integracoes Desacopladas

- catalogo de produtos: Mercado Livre
- fiscal: gateway com stub e ponto de extensao para integrador real
- pagamento: gateway fake pronto para evoluir para provedor real

## Execucao Rapida

```bash
docker compose up -d --build
```

## Proximos Docs Uteis

- `database-er-diagram.md`
- `workflow.md`
- `contribuir.md`
