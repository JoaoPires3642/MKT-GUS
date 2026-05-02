package com.mktgus.autoatendimento.application.response;

public record OrderItemResponse(
        String ean,
        String productName,
        double unitPrice,
        int quantity,
        boolean adultOnly,
        double totalPrice
) {
}
