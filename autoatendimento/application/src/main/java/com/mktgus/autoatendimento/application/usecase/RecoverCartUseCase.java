package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.gateway.CartCacheGateway;
import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecoverCartUseCase {

    private final CartCacheGateway cartCacheGateway;

    public RecoverCartUseCase(CartCacheGateway cartCacheGateway) {
        this.cartCacheGateway = cartCacheGateway;
    }

    public Optional<CartSnapshot> execute(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return Optional.empty();
        }
        return cartCacheGateway.recover(cpf);
    }
}
