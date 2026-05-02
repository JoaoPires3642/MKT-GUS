package com.mktgus.autoatendimento.application.cart;

import java.util.List;

public record SaveCartInput(
        String cpf,
        List<CartItemInput> items
) {
    public record CartItemInput(
            String ean,
            String productName,
            double unitPrice,
            int quantity,
            boolean adultOnly
    ) {}
}
