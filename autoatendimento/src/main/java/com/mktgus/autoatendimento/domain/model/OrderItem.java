package com.mktgus.autoatendimento.domain.model;

public record OrderItem(
        String ean,
        String productName,
        double unitPrice,
        int quantity,
        boolean adultOnly,
        double totalPrice
) {
}
