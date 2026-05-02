package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.domain.model.Product;

import java.util.List;

public interface ScannedProductGateway {
    List<Product> findBySession(String sessionId);

    void save(String sessionId, Product product);

    void clear(String sessionId);
}
