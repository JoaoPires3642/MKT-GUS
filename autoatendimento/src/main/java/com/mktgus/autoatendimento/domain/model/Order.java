package com.mktgus.autoatendimento.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record Order(
        Long id,
        Long customerCpf,
        Long couponId,
        LocalDateTime orderedAt,
        double totalAmount,
        List<OrderItem> items
) {
}
