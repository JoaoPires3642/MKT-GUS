package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import java.util.Optional;

public interface CartCacheGateway {
    void save(CartSnapshot snapshot);
    Optional<CartSnapshot> recover(String cpf);
    void evict(String cpf);
}
