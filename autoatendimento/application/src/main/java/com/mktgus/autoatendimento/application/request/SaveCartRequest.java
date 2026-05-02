package com.mktgus.autoatendimento.application.request;

import java.util.List;

public record SaveCartRequest(
        String cpf,
        List<CartItemRequest> items
) {
    public record CartItemRequest(
            String ean,
            String productName,
            double unitPrice,
            int quantity,
            boolean adultOnly
    ) {}
}
