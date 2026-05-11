package com.mktgus.autoatendimento.infra.data.external.microservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mktgus.autoatendimento.application.gateway.ProductCatalogGateway;
import com.mktgus.autoatendimento.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Primary
@Component
public class LocalProductMicroserviceGateway implements ProductCatalogGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalProductMicroserviceGateway.class);
    private static final String LOCAL_PRODUCT_BY_EAN_URL = "http://localhost:9090/products/{ean}";
    private static final String DOCKER_HOST_PRODUCT_BY_EAN_URL = "http://host.docker.internal:9090/products/{ean}";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        try {
            ProductMicroserviceResponse response = fetchProduct(barcode);
            if (response == null || response.ean() == null || response.ean().isBlank()) {
                return Optional.empty();
            }

            return Optional.of(response.toProduct(barcode));
        } catch (RestClientException exception) {
            LOGGER.warn("Falha ao consultar microservico de produtos para barcode {}", barcode, exception);
            return Optional.empty();
        }
    }

    private ProductMicroserviceResponse fetchProduct(String barcode) {
        try {
            LOGGER.info("Consultando microservico de produtos em http://localhost:9090/products/{}", barcode);
            return restTemplate.getForObject(LOCAL_PRODUCT_BY_EAN_URL, ProductMicroserviceResponse.class, barcode);
        } catch (ResourceAccessException exception) {
            LOGGER.info(
                    "Microservico nao acessivel via localhost. Tentando Docker host em http://host.docker.internal:9090/products/{}",
                    barcode
            );
            return restTemplate.getForObject(DOCKER_HOST_PRODUCT_BY_EAN_URL, ProductMicroserviceResponse.class, barcode);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProductMicroserviceResponse(
            String ean,
            String name,
            @JsonProperty("nome")
            String nome,
            String imageUrl,
            @JsonProperty("urlImagem")
            String urlImagem,
            Double price,
            @JsonProperty("valor")
            Double valor,
            Boolean adultOnly,
            @JsonProperty("produtoMaiorDeIdade")
            Boolean produtoMaiorDeIdade,
            String description
    ) {
        private Product toProduct(String requestedBarcode) {
            return new Product(
                    firstNonBlank(ean, requestedBarcode),
                    firstNonBlank(name, nome, "Produto"),
                    firstNonBlank(imageUrl, urlImagem, null),
                    price != null ? price : valueOrZero(valor),
                    adultOnly != null ? adultOnly : Boolean.TRUE.equals(produtoMaiorDeIdade),
                    description
            );
        }

        private static String firstNonBlank(String first, String second, String fallback) {
            if (first != null && !first.isBlank()) {
                return first;
            }
            if (second != null && !second.isBlank()) {
                return second;
            }
            return fallback;
        }

        private static String firstNonBlank(String first, String fallback) {
            return firstNonBlank(first, null, fallback);
        }

        private static double valueOrZero(Double value) {
            return value == null ? 0.0 : value;
        }
    }
}
