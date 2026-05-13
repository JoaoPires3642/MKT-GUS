package com.mktgus.autoatendimento.application.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderHistoryResponse(
        Long id,
        Long marketId,
        String customerCpf,
        Long couponId,
        LocalDateTime orderedAt,
        double totalAmount,
        int itemCount,
        List<OrderItemResponse> items
) {
}
