# Backend Clean Architecture Plan

## Why this refactor matters

Today the project already has better logical separation, but parts of the backend still share persistence models,
controller DTOs, and framework details across layers.

That means the architecture is improved, but not fully physically separated yet.

## Main technical debt still present

- JPA entities are still used across business flows
- some use cases still return persistence-oriented models
- controllers still expose internal structures too directly
- domain concepts are not fully isolated from persistence structures

## Desired end state

### Domain

- pure business entities
- no Spring annotations
- no JPA annotations
- no direct dependency on HTTP or repository implementations

### Application

- use cases with explicit input/output models
- dependency only on domain and interfaces
- business exceptions handled consistently

### Interface Adapters

- request and response DTOs
- mapping between HTTP and application
- mapping between application and presentation

### Infrastructure

- JPA entities and repositories
- HTTP clients for external APIs
- WebSocket implementation
- scanner and device integrations

## Initial implementation scope

The first implementation wave should focus on:

1. domain entities for customer, coupon, order, order item, product, and employee
2. application input/output models for key use cases
3. gateway mapping between domain and JPA models
4. controller response mapping

## Benefits expected

- database migration becomes safer
- Mercado Livre integration becomes replaceable
- barcode integration becomes replaceable
- business rules become easier to test
- backend behavior becomes more stable under infrastructure changes

## Refactor guideline

- preserve existing routes whenever possible
- preserve current behavior unless a bug fix is intentional
- make changes incrementally
- keep framework dependencies out of the center of the application
