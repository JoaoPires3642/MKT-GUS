package com.mktgus.autoatendimento.infra.utils.cache;

import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import com.mktgus.autoatendimento.application.gateway.CartCacheGateway;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryCartCacheAdapter implements CartCacheGateway {

    static final int TTL_MINUTES = 5;
    private final Map<String, CartSnapshot> cache = new ConcurrentHashMap<>();

    @Override
    public void save(CartSnapshot snapshot) {
        cache.put(snapshot.cpf(), snapshot);
    }

    @Override
    public Optional<CartSnapshot> recover(String cpf) {
        CartSnapshot snapshot = cache.get(cpf);
        if (snapshot == null) {
            return Optional.empty();
        }
        if (isExpired(snapshot)) {
            cache.remove(cpf);
            return Optional.empty();
        }
        return Optional.of(snapshot);
    }

    @Override
    public void evict(String cpf) {
        cache.remove(cpf);
    }

    private boolean isExpired(CartSnapshot snapshot) {
        return snapshot.savedAt().plusMinutes(TTL_MINUTES).isBefore(LocalDateTime.now());
    }
}
