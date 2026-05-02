package com.mktgus.autoatendimento.application.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerCpf,
        Long couponId,
        LocalDateTime orderedAt,
        double totalAmount,
        List<OrderItemResponse> items,
        Integer updatedPointsBalance
) {
}
