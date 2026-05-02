package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.cart.SaveCartInput;
import com.mktgus.autoatendimento.application.exception.ValidationException;
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
        if (input == null) {
            throw new ValidationException("Carrinho invalido.");
        }
        if (input.cpf() == null || input.cpf().isBlank()) {
            throw new ValidationException("CPF obrigatorio para salvar carrinho.");
        }
        if (input.items() == null || input.items().isEmpty()) {
            throw new ValidationException("Carrinho vazio nao pode ser salvo.");
        }
        var items = input.items().stream()
                .map(i -> new CartSnapshot.CartItemSnapshot(
                        i.ean(), i.productName(), i.unitPrice(), i.quantity(), i.adultOnly()))
                .toList();
        cartCacheGateway.save(new CartSnapshot(input.cpf(), items, LocalDateTime.now()));
    }
}
