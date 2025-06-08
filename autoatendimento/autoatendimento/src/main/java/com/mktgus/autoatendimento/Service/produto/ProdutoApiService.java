package com.mktgus.autoatendimento.Service.produto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mktgus.autoatendimento.dto.produtoDTO.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoApiService {

    private static final String PRODUCT_SEARCH_URL = "https://api.mercadolibre.com/products/search";
    private static final String PRODUCT_ITEMS_URL = "https://api.mercadolibre.com/products/%s/items";
    private static final String ACCESS_TOKEN = System.getenv("ACCESS_TOKEN");
    private static final String SITE_ID = "MLB";

    private final List<ProdutoDto> listaDeProdutos = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProdutoDto getProdutoPorCodigoDeBarras(String barcode) {
        // Passo 1: Buscar o ID do produto usando /products/search
        String productUrl = PRODUCT_SEARCH_URL + "?site_id=" + SITE_ID + "&status=active&product_identifier=" + barcode;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + ACCESS_TOKEN);

        try {
            ResponseEntity<String> productResponse = restTemplate.exchange(
                    productUrl, HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    String.class
            );

            JsonNode productRoot = objectMapper.readTree(productResponse.getBody());
            JsonNode productResults = productRoot.path("results");

            if (!productResults.isArray() || productResults.isEmpty()) {
                System.out.println("Nenhum produto encontrado em /products/search para o código de barras: " + barcode);
                return null;
            }

            JsonNode productNode = productResults.get(0);
            String productId = productNode.path("id").asText();
            String name = productNode.path("name").asText();
            String categoryId = productNode.path("category_id").asText(null);
            // Acessar a URL da imagem do primeiro elemento do array "pictures"
            String thumbnail = productNode.path("pictures").isArray() && productNode.path("pictures").size() > 0
                    ? productNode.path("pictures").get(0).path("url").asText(null)
                    : null;

            // Passo 2: Buscar o preço do produto usando /products/$idmercadolivre/items
            double price = getProductPrice(productId);

            boolean produtoMaiorDeIdade = isProductForAdults(categoryId);
            ProdutoDto produto = new ProdutoDto(barcode, name, thumbnail, price, produtoMaiorDeIdade);
            listaDeProdutos.add(produto);
            return produto;

        } catch (HttpClientErrorException e) {
            System.err.println("Erro em /products/search: " + e.getStatusCode());
            System.err.println("Resposta: " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao processar /products/search: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private double getProductPrice(String productId) {
        String itemsUrl = String.format(PRODUCT_ITEMS_URL, productId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + ACCESS_TOKEN);

        try {
            ResponseEntity<String> itemsResponse = restTemplate.exchange(
                    itemsUrl, HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    String.class
            );

            JsonNode itemsRoot = objectMapper.readTree(itemsResponse.getBody());
            JsonNode itemsResults = itemsRoot.path("results");

            if (!itemsResults.isArray() || itemsResults.isEmpty()) {
                System.out.println("Nenhum item encontrado em /products/" + productId + "/items");
                return 0.0;
            }

            JsonNode itemNode = itemsResults.get(0);
            return itemNode.path("price").asDouble(0.0);

        } catch (HttpClientErrorException e) {
            System.err.println("Erro em /products/" + productId + "/items: " + e.getStatusCode());
            System.err.println("Resposta: " + e.getResponseBodyAsString());
            return 0.0;
        } catch (Exception e) {
            System.err.println("Erro ao processar /products/" + productId + "/items: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    public List<ProdutoDto> getTodosProdutos() {
        return new ArrayList<>(listaDeProdutos);
    }

    private boolean isProductForAdults(String categoryId) {
        String[] adultCategories = {
                "MLB1416", "MLB439740", "MLB194809", "MLB194799", "MLB194810",
                "MLB269510", "MLB194811", "MLB270404", "MLB194826", "MLB269584",
                "MLB1405", "MLB277634", "MLB123456"
        };

        for (String id : adultCategories) {
            if (categoryId != null && categoryId.equals(id)) {
                return true;
            }
        }
        return false;
    }
}