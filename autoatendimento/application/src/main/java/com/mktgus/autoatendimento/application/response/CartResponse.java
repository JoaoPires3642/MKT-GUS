package com.mktgus.autoatendimento.application.response;

import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        String cpf,
        LocalDateTime savedAt,
        List<CartItemResponse> items
) {
    public record CartItemResponse(
            String ean,
            String productName,
            double unitPrice,
            int quantity,
            boolean adultOnly
    ) {}
}
