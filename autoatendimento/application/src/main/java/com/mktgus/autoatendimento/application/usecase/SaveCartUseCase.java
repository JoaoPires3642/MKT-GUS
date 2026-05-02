package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.cart.SaveCartInput;
import com.mktgus.autoatendimento.application.gateway.CartCacheGateway;
import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SaveCartUseCase {

    private final CartCacheGateway cartCacheGateway;

    public SaveCartUseCase(CartCacheGateway cartCacheGateway) {
        this.cartCacheGateway = cartCacheGateway;
    }

    public void execute(SaveCartInput input) {
        if (input.cpf() == null || input.cpf().isBlank()) {
            return;
        }
        var items = input.items().stream()
                .map(i -> new CartSnapshot.CartItemSnapshot(
                        i.ean(), i.productName(), i.unitPrice(), i.quantity(), i.adultOnly()))
                .toList();
        cartCacheGateway.save(new CartSnapshot(input.cpf(), items, LocalDateTime.now()));
    }
}
