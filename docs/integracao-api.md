# Integração com Sistema do Cliente

## Visão Geral

O MKT-GUS foi projetado para ser **pluggable** - ou seja, pode trocar facilmente integrações externas como:

- **Catálogo de Produtos** (atual: Mercado Livre → futuro: sistema do cliente)
- **Programa de Pontos** (atual: nosso sistema → futuro: sistema do cliente)
- **Funcionários** (atual: nosso banco → futuro: sistema do cliente)
- **Cupons** (atual: nosso banco → futuro: sistema do cliente)

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

### Estrutura Atual

```java
// domain/gateway/ProductCatalogGateway.java
public interface ProductCatalogGateway {
    Optional<Product> findByBarcode(String barcode);
}
```

### Implementação Atual (Mercado Livre)

```java
// infrastructure/external/mercadolivre/MercadoLivreProductGateway.java
@Component
public class MercadoLivreProductGateway implements ProductCatalogGateway {
    @Override
    public Optional<Product> findByBarcode(String barcode) {
        // Chamada REST para API do Mercado Livre
    }
}
```

### Como Trocar para Sistema do Cliente

1. **Criar novo Gateway:**

```java
// infrastructure/external/mercadocliente/MercadoBackofficeGateway.java
@Component
public class MercadoBackofficeGateway implements ProductCatalogGateway {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MercadoBackofficeGateway(
            @Value("${mercadocliente.api.url}") String baseUrl,
            RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        try {
            ProdutoResponse response = restTemplate.getForObject(
                baseUrl + "/api/produtos/{ean}",
                ProdutoResponse.class,
                barcode
            );
            return Optional.of(mapToProduct(response));
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    private Product mapToProduct(ProdutoResponse response) {
        return new Product(
            response.getEan(),
            response.getNome(),
            response.getPreco(),
            response.isMaiorDeIdade(),
            response.getUrlImagem()
        );
    }
}
```

2. **Configurar no Spring:**

```yaml
# application.yml
mercadocliente:
  api:
    url: https://api.mercadocliente.com.br
    key: sua_api_key
```

3. **Desabilitar Gateway antigo (opcional):**

```java
// Usar @Primary ou @ConditionalOnProperty
@Component
@Primary
@ConditionalOnProperty(name = "gateway.produtos", value = "mercadocliente")
public class MercadoBackofficeGateway implements ProductCatalogGateway {
    // ...
}
```

---

## 2. Trocar Sistema de Pontos

### Estrutura Atual

```java
// domain/gateway/PointsGateway.java (a criar)
public interface PointsGateway {
    int getBalance(Long cpf);
    void addPoints(Long cpf, int points);
    void deductPoints(Long cpf, int points);
}
```

### Implementação Atual

```java
// infrastructure/persistence/gateway/JpaPointsGateway.java
@Component
public class JpaPointsGateway implements PointsGateway {
    // Opera na tabela customer.pontos
}
```

### Como Trocar para Sistema do Cliente

```java
// infrastructure/external/mercadocliente/MercadoPointsGateway.java
@Component
@ConditionalOnProperty(name = "gateway.pontos", value = "mercadocliente")
public class MercadoPointsGateway implements PointsGateway {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Override
    public int getBalance(Long cpf) {
        PointsResponse response = restTemplate.getForObject(
            baseUrl + "/api/clientes/{cpf}/pontos",
            PointsResponse.class,
            cpf
        );
        return response.getSaldo();
    }

    @Override
    public void addPoints(Long cpf, int points) {
        restTemplate.postForObject(
            baseUrl + "/api/clientes/{cpf}/pontos/acumular",
            new PointsRequest(points),
            Void.class,
            cpf
        );
    }

    @Override
    public void deductPoints(Long cpf, int points) {
        restTemplate.postForObject(
            baseUrl + "/api/clientes/{cpf}/pontos/resgatar",
            new PointsRequest(points),
            Void.class,
            cpf
        );
    }
}
```

---

## 3. Trocar Sistema de Funcionários

### Estrutura Atual

```java
// domain/gateway/EmployeeGateway.java
public interface EmployeeGateway {
    boolean existsByRegistration(Long registration);
}
```

### Como Trocar

```java
@Component
@ConditionalOnProperty(name = "gateway.funcionarios", value = "mercadocliente")
public class MercadoEmployeeGateway implements EmployeeGateway {

    @Override
    public boolean existsByRegistration(Long registration) {
        // GET /api/funcionarios/{matricula}
        // Retornar true se existir
    }
}
```

---

## 4. Configuração Centralizada

### Arquivo de Configuração

```yaml
# application.yml

# Gateway de Produtos
gateway:
  produtos: mercadocliente  # ou "mercadolivre" (padrão)
  
# Gateway de Pontos  
  pontos: mercadocliente    # ou "mktgus" (padrão)
  
# Gateway de Funcionários
  funcionarios: mercadocliente  # ou "mktgus" (padrão)

# Configurações do cliente
mercadocliente:
  api:
    url: https://api.mercadocliente.com.br
    key: ${MERCADOCLIENTE_API_KEY}
    timeout: 5000  # ms
```

---

## 5. Checklist de Integração

Ao integrar com um novo sistema do cliente:

- [ ] Definir endpoints da API do cliente
- [ ] Documentar formato de autenticação (API Key, Bearer Token, OAuth)
- [ ] Mapear modelos de dados (cliente → nosso domínio)
- [ ] Implementar novo Gateway
- [ ] Adicionar `@ConditionalOnProperty` ou `@Primary`
- [ ] Criar testes unitários
- [ ] Testar fluxo completo
- [ ] Documentar specifics da integração

---

## 6. Formatos de Dados Comuns

### Produto

```json
// Input (do cliente)
{
  "ean": "7891234567890",
  "nome": "Produto Exemplo",
  "preco": 12.99,
  "maiorDeIdade": false,
  "urlImagem": "https://..."
}

// Output (nosso domínio)
Product(ean, name, price, adultOnly, imageUrl)
```

### Cliente

```json
// Input
{
  "cpf": "12345678900",
  "nome": "João Silva",
  "pontos": 1500
}

// Output
Customer(cpf, points)
```

### Funcionário

```json
// Input
{
  "matricula": "12345",
  "nome": "Maria Santos",
  "cargo": "Gerente"
}

// Output
Employee(matricula, name, role)
```

---

## 7. Tratamento de Erros

### Boas Práticas

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

### Fallback Strategy

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

## 8. Segurança

### Autenticação

```java
// Adicionar headers de autenticação
@Component
public class AuthenticatedRestTemplate {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .additionalInterceptors((request, body, execution) -> {
                request.getHeaders().add("Authorization", "Bearer " + apiKey);
                return execution.execute(request, body);
            })
            .build();
    }
}
```

### Validação de Dados

Sempre validar dados vindos de sistemas externos:

```java
private Product mapToProduct(ProdutoResponse response) {
    if (response.getEan() == null || response.getEan().isBlank()) {
        throw new InvalidDataException("EAN inválido");
    }
    if (response.getPreco() < 0) {
        throw new InvalidDataException("Preço não pode ser negativo");
    }
    // ...
}
```

---

## Próximos Passos

Após implementar integração:
1. Testar todos os fluxos (happy path e erros)
2. Documentar os endpoints do cliente
3. Adicionar ao [endpoints.md](endpoints.md)
4. Atualizar [arquitetura.md](arquitetura.md) se houver mudanças