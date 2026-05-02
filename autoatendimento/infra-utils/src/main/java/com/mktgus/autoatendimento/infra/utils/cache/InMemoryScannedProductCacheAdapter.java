package com.mktgus.autoatendimento.infra.utils.cache;

import com.mktgus.autoatendimento.application.gateway.ScannedProductGateway;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryScannedProductCacheAdapter implements ScannedProductGateway {

    private final Map<String, CopyOnWriteArrayList<Product>> cacheBySession = new ConcurrentHashMap<>();

    @Override
    public List<Product> findBySession(String sessionId) {
        return List.copyOf(cacheBySession.getOrDefault(sessionId, new CopyOnWriteArrayList<>()));
    }

    @Override
    public void save(String sessionId, Product product) {
        cacheBySession.computeIfAbsent(sessionId, key -> new CopyOnWriteArrayList<>()).add(product);
    }

    @Override
    public void clear(String sessionId) {
        cacheBySession.remove(sessionId);
    }
}
