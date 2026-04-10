# Clean Architecture Cheat Sheet

## Camadas

### 1. Entities (Enterprise Business Rules)

Agregam as regras de negocio independentes da aplicacao.
Sao as regras de mais alto nivel e representam o nucleo do dominio.
As entidades devem modelar regras que possam ser aplicadas em qualquer contexto da aplicacao.

As entities devem ser estaveis e mudar pouco com o tempo, por isso tendem a ser altamente reutilizaveis.

#### Ponto principal

O centro da aplicacao nao e o banco de dados.
O centro da aplicacao nao e o framework.
O centro da aplicacao sao as entities.

#### Por que nao usar entidades fora do dominio?

Na abordagem tradicional, muitas vezes o mesmo dado transita do banco ate a interface.
Se a tabela do banco muda, a interface pode quebrar junto.

Quando usamos DTOs e modelos de fronteira, criamos independencia entre as camadas.
Isso protege a aplicacao contra acoplamento acidental entre banco, backend e UI.

### 2. Use Cases (Application Business Rules)

Os use cases aplicam as regras de negocio implementadas no nucleo.
Por isso sao chamados de regras de aplicacao.

Casos de uso resolvem problemas especificos de negocio, orquestrando entidades, gateways,
repositorios e outros colaboradores necessarios.

Use case nao e a mesma coisa que CRUD.

#### Responsabilidades

- orquestrar o fluxo de uma acao de negocio
- aplicar regras especificas da aplicacao
- lancar excecoes de negocio quando necessario
- depender de persistencia e integracoes externas por interfaces

#### Regras importantes

- o metodo `execute` deve receber um objeto de input
- esse input nao deve ser uma entidade do dominio ou uma entidade de banco
- dependencias devem ser injetadas por interfaces

#### Por que injetar interfaces?

Interfaces criam contratos entre as camadas.
Se hoje o repositorio usa MongoDB e amanha passar a usar MySQL, o caso de uso nao deve quebrar.

O caso de uso deve depender do contrato, nunca do detalhe tecnologico.

### 3. Controllers, Gateways, Presenters (Interface Adapters)

Essa camada adapta as interfaces externas para o formato interno da aplicacao.
Ela faz a traducao entre UI, API, banco e servicos externos com os use cases.

#### Responsabilidades

- mapear request para input de caso de uso
- mapear output de caso de uso para response
- converter formatos de dados
- adaptar chamadas a banco de dados e APIs externas

#### Beneficios

- separacao clara de responsabilidades
- melhor testabilidade
- flexibilidade para trocar tecnologias externas
- isolamento da complexidade tecnica

Em resumo, Interface Adapters sao os tradutores entre o mundo externo e o nucleo da aplicacao.
Eles sao a cola que conecta tudo sem acoplar diretamente as camadas.

### 4. Devices, DB, UI, Web, External Interfaces (Frameworks and Drivers)

Essa e a camada mais externa da arquitetura.
Ela contem detalhes tecnicos e implementacoes concretas.

#### Exemplos

- Spring Boot
- JPA
- MySQL
- MongoDB
- controllers HTTP
- WebSocket
- integracao com Mercado Livre
- barcode scanner
- UI e frameworks frontend

#### Responsabilidades

- conectar o sistema ao mundo externo
- implementar os contratos definidos pelas camadas internas
- encapsular detalhes de framework, banco, transporte e integracoes

Essa camada deve depender das camadas internas, nunca o contrario.

## Regra de Dependencia

As dependencias sempre devem apontar para dentro.

- Frameworks and Drivers dependem de Interface Adapters
- Interface Adapters dependem de Use Cases
- Use Cases dependem de Entities e interfaces
- Entities nao dependem de detalhes externos

## Resumo Pratico

- Entities representam o nucleo do negocio
- Use Cases orquestram a execucao das regras
- Interface Adapters fazem a traducao entre externo e interno
- Frameworks and Drivers implementam os detalhes tecnicos

## Lembrete Final

Se uma mudanca de banco, framework ou API externa quebra o centro da aplicacao,
entao a arquitetura ainda nao esta suficientemente desacoplada.
