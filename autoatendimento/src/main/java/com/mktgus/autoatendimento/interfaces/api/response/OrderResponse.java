package com.mktgus.autoatendimento.interfaces.api.response;

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
