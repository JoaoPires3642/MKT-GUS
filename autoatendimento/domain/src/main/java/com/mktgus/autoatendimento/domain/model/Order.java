package com.mktgus.autoatendimento.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record Order(
        Long id,
        Long marketId,
        Long customerCpf,
        Long couponId,
        LocalDateTime orderedAt,
        double totalAmount,
        List<OrderItem> items
) {
    public boolean isAnonymous() {
        return customerCpf == null;
    }

    public int itemCount() {
        return items == null ? 0 : items.size();
    }

    public double calculateSubtotal() {
        return items == null ? 0.0 : items.stream().mapToDouble(OrderItem::totalPrice).sum();
    }

    public boolean hasCoupon() {
        return couponId != null;
    }
}
