package com.mktgus.autoatendimento.interfaces.api.response;

public record OrderItemResponse(
        String ean,
        String productName,
        double unitPrice,
        int quantity,
        boolean adultOnly,
        double totalPrice
) {
}
