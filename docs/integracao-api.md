# Integração com Sistema do Cliente

## Visão Geral

O MKT-GUS foi projetado para ser **pluggable** - pode trocar facilmente integrações externas como:

- **Catálogo de Produtos** (Mercado Livre → sistema do cliente)
- **Programa de Pontos** (nosso sistema → sistema do cliente)
- **Funcionários** (nosso banco → sistema do cliente)

---

## Arquitetura de Integração

```
MKT-GUS
├── ProductCatalogGateway (interface)
│   ├── MercadoLivreGateway (atual)
│   └── MercadoBackofficeGateway (futuro) ← criar novo
│
├── PointsGateway (interface)
│   ├── MktGusPointsGateway (atual)
│   └── MercadoPointsGateway (futuro) ← criar novo
│
└── EmployeeGateway (interface)
    ├── JpaEmployeeGateway (atual)
    └── MercadoEmployeeGateway (futuro) ← criar novo
```

A troca é feita via **injeção de dependência** - sem mudar código dos Use Cases.

---

## 1. Trocar Catálogo de Produtos

### Interface

```java
public interface ProductCatalogGateway {
    Optional<Product> findByBarcode(String barcode);
}
```

### Implementação Atual (Mercado Livre)

```java
@Component
public class MercadoLivreProductGateway implements ProductCatalogGateway {
    @Override
    public Optional<Product> findByBarcode(String barcode) {
        // Chamada REST para API do Mercado Livre
    }
}
```

### Como Trocar para Sistema do Cliente

1. Criar novo Gateway implementando `ProductCatalogGateway`
2. Configurar no Spring com `@ConditionalOnProperty`
3. Trocar via propriedades

---

## 2. Checklist de Integração

Ao integrar com um novo sistema do cliente:

- [ ] Definir endpoints da API do cliente
- [ ] Documentar formato de autenticação (API Key, Bearer Token, OAuth)
- [ ] Mapear modelos de dados (cliente → nosso domínio)
- [ ] Implementar novo Gateway
- [ ] Adicionar `@ConditionalOnProperty` ou `@Primary`
- [ ] Criar testes unitários
- [ ] Testar fluxo completo

---

## 3. Configuração Centralizada

```yaml
gateway:
  produtos: mercadocliente  # ou "mercadolivre" (padrão)
  pontos: mercadocliente    # ou "mktgus" (padrão)

mercadocliente:
  api:
    url: https://api.mercadocliente.com.br
    key: ${MERCADOCLIENTE_API_KEY}
    timeout: 5000  # ms
```

---

## 4. Tratamento de Erros

```java
public Optional<Product> findByBarcode(String barcode) {
    try {
        return Optional.ofNullable(
            restTemplate.getForObject(url, ProductResponse.class, barcode)
        );
    } catch (HttpClientErrorException.NotFound e) {
        return Optional.empty();  // Produto não existe
    } catch (HttpServerErrorException e) {
        throw new ExternalServiceException("Serviço de catálogo indisponível");
    }
}
```

---

## 5. Fallback Strategy

```java
@Component
public class CatalogFallbackGateway implements ProductCatalogGateway {
    
    private final MercadoLivreProductGateway mlGateway;
    private final MercadoBackofficeGateway mcGateway;

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        // Tenta primeiro o sistema do cliente
        Optional<Product> product = mcGateway.findByBarcode(barcode);
        
        // Se não encontrar, tenta Mercado Livre
        if (product.isEmpty()) {
            product = mlGateway.findByBarcode(barcode);
        }
        
        return product;
    }
}
```

---

## Próximos Passos

Após implementar integração:
1. Testar todos os fluxos (happy path e erros)
2. Documentar os endpoints do cliente
3. Adicionar ao `endpoints.md`
4. Atualizar `arquitetura.md` se houver mudanças