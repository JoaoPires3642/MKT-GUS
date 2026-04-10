package com.mktgus.autoatendimento.infrastructure.external.mercadolivre;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mktgus.autoatendimento.domain.gateway.ProductCatalogGateway;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class MercadoLivreProductGateway implements ProductCatalogGateway {
    private static final String SEARCH_URL = "https://api.mercadolibre.com/products/search";
    private static final String ITEMS_URL = "https://api.mercadolibre.com/products/%s/items";
    private static final String SITE_ID = "MLB";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String accessToken = System.getenv("ACCESS_TOKEN");

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        try {
            JsonNode productNode = fetchProductNode(barcode);
            if (productNode == null) {
                return Optional.empty();
            }

            String productId = productNode.path("id").asText();
            String name = productNode.path("name").asText();
            String categoryId = productNode.path("category_id").asText(null);
            String thumbnail = extractThumbnail(productNode);
            double price = fetchPrice(productId);
            boolean adultOnly = isAdultProduct(categoryId);
            return Optional.of(new Product(barcode, name, thumbnail, price, adultOnly));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private JsonNode fetchProductNode(String barcode) throws Exception {
        String url = SEARCH_URL + "?site_id=" + SITE_ID + "&status=active&product_identifier=" + barcode;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, authEntity(), String.class);
        JsonNode results = objectMapper.readTree(response.getBody()).path("results");
        if (!results.isArray() || results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    private double fetchPrice(String productId) throws Exception {
        String url = String.format(ITEMS_URL, productId);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, authEntity(), String.class);
        JsonNode results = objectMapper.readTree(response.getBody()).path("results");
        if (!results.isArray() || results.isEmpty()) {
            return 0.0;
        }
        return results.get(0).path("price").asDouble(0.0);
    }

    private HttpEntity<Void> authEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return new HttpEntity<>(headers);
    }

    private String extractThumbnail(JsonNode productNode) {
        JsonNode pictures = productNode.path("pictures");
        if (!pictures.isArray() || pictures.isEmpty()) {
            return null;
        }
        return pictures.get(0).path("url").asText(null);
    }

    private boolean isAdultProduct(String categoryId) {
        String[] adultCategories = {
                "MLB1416", "MLB439740", "MLB194809", "MLB194799", "MLB194810", "MLB269510", "MLB194811",
                "MLB270404", "MLB194826", "MLB269584", "MLB1405", "MLB277634", "MLB123456"
        };

        for (String adultCategory : adultCategories) {
            if (adultCategory.equals(categoryId)) {
                return true;
            }
        }

        return false;
    }
}
