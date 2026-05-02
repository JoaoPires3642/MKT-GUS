package com.mktgus.autoatendimento.application.cart;

import java.time.LocalDateTime;
import java.util.List;

public record CartSnapshot(String cpf, List<CartItemSnapshot> items, LocalDateTime savedAt) {

    public record CartItemSnapshot(
            String ean,
            String productName,
            double unitPrice,
            int quantity,
            boolean adultOnly
    ) {}
}
