package com.mktgus.autoatendimento.infra.data.external.microservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        try {
            ProductMicroserviceResponse response = fetchProduct(barcode);
            if (response == null || response.ean() == null || response.ean().isBlank()) {
                LOGGER.warn("Resposta vazia ou sem EAN para barcode {}", barcode);
                return Optional.empty();
            }

            LOGGER.info(
                    "Produto do microservico carregado para barcode {} com imageUrl='{}'",
                    barcode,
                    response.imageUrl()
            );
            return Optional.of(new Product(
                    firstNonBlank(response.ean(), barcode),
                    firstNonBlank(response.name(), "Produto"),
                    normalizeImageUrl(response.imageUrl()),
                    response.price() != null ? response.price() : 0.0,
                    Boolean.TRUE.equals(response.adultOnly()),
                    response.description()
            ));
        } catch (RestClientException exception) {
            LOGGER.warn("Falha ao consultar microservico de produtos para barcode {}", barcode, exception);
            return Optional.empty();
        }
    }

    private ProductMicroserviceResponse fetchProduct(String barcode) {
        try {
            LOGGER.info("Consultando microservico de produtos em http://localhost:9090/products/{}", barcode);
            String responseBody = restTemplate.getForObject(LOCAL_PRODUCT_BY_EAN_URL, String.class, barcode);
            LOGGER.debug("Payload bruto do microservico para barcode {}: {}", barcode, responseBody);
            return parseResponse(responseBody, barcode);
        } catch (ResourceAccessException exception) {
            LOGGER.info(
                    "Microservico nao acessivel via localhost. Tentando Docker host em http://host.docker.internal:9090/products/{}",
                    barcode
            );
            String responseBody = restTemplate.getForObject(DOCKER_HOST_PRODUCT_BY_EAN_URL, String.class, barcode);
            LOGGER.debug("Payload bruto do microservico via Docker host para barcode {}: {}", barcode, responseBody);
            return parseResponse(responseBody, barcode);
        }
    }

    private ProductMicroserviceResponse parseResponse(String responseBody, String barcode) {
        try {
            return objectMapper.readValue(responseBody, ProductMicroserviceResponse.class);
        } catch (Exception exception) {
            LOGGER.warn("Falha ao interpretar payload do microservico para barcode {}", barcode, exception);
            return null;
        }
    }

    private static String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        String trimmed = imageUrl.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("//")) {
            return "http:" + trimmed;
        }
        return trimmed;
    }

    private static String firstNonBlank(String value, String fallback) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        return fallback;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProductMicroserviceResponse(
            String ean,
            String name,
            @JsonAlias({
                    "imageUrl",
                    "urlImagem",
                    "url_imagem",
                    "imagem",
                    "image",
                    "thumbnail",
                    "thumbnailUrl",
                    "pictureUrl"
            })
            String imageUrl,
            Double price,
            Boolean adultOnly,
            String description
    ) {
    }
}
