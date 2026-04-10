# Backend Architecture

## Goal

This backend should evolve toward a pragmatic Clean Architecture where business rules are independent from Spring, JPA,
Mercado Livre, barcode integrations, and database choices.

## Target Layers

### 1. Domain

The domain layer contains the core business model.
It should not depend on Spring, JPA, HTTP, or external APIs.

Current target domain concepts:

- `Customer`
- `Employee`
- `Coupon`
- `Order`
- `OrderItem`
- `Product`

Rules for this layer:

- No `@Entity`, `@Repository`, or Spring annotations
- No framework-specific exceptions
- No direct dependency on DTOs from controllers
- Business concepts and rules live here first

### 2. Application

The application layer coordinates business actions through use cases.
It depends on domain models and abstract contracts only.

Examples in this project:

- verify customer CPF
- update customer points
- verify employee registration
- find product by barcode
- process barcode scan
- confirm purchase
- list coupons

Rules for this layer:

- Use cases receive input models, not JPA entities
- Use cases return output models, not persistence entities
- Persistence and external integrations are accessed through ports
- Business exceptions are raised here when needed

### 3. Interface Adapters

This layer translates the external world into the internal application format.

Examples:

- REST controllers
- request and response DTOs
- presenters and mappers
- gateway implementations that adapt domain ports to infrastructure details

Rules for this layer:

- Controllers should only validate/translate input and call use cases
- Controllers must not contain business rules
- Responses should be mapped from application/domain output models

### 4. Infrastructure

This layer contains technical details and framework integrations.

Examples:

- Spring Boot configuration
- JPA entities and repositories
- Mercado Livre HTTP integration
- WebSocket notifier
- barcode scanner integration
- datasource configuration

Rules for this layer:

- Infrastructure implements contracts defined by inner layers
- JPA entities stay here as persistence models
- External APIs stay here behind interfaces

## Current Refactor Direction

The backend already has the first separation by packages:

- `domain`
- `application`
- `infrastructure`
- `interfaces`

The next step is physical separation between:

- pure domain entities
- application input/output models
- JPA persistence entities
- controller request/response DTOs

## Incremental Migration Strategy

### Step 1

Create pure domain entities and stop using JPA entities directly inside use cases.

### Step 2

Move JPA models to infrastructure and map them through gateways.

### Step 3

Make controllers depend on request/response DTOs only.

### Step 4

Keep external services behind explicit ports so Mercado Livre, barcode, or database providers can be replaced safely.

## Practical Rules For This Project

- Database schema changes must not force controller or frontend contract changes.
- External API changes must be isolated inside infrastructure adapters.
- Business rules should be testable without starting Spring Boot.
- Use cases should stay focused on business flows, not CRUD alone.
- Framework code should remain at the edges.
