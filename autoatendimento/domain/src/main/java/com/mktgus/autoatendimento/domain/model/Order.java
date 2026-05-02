package com.mktgus.autoatendimento.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record Order(
        Long id,
        Long marketId,
        Long customerCpf,
        Long couponId,
        LocalDateTime orderedAt,
        double totalAmount,
        List<OrderItem> items
) {
    public Order {
        items = List.copyOf(Objects.requireNonNull(items, "Itens do pedido são obrigatórios"));
    }

    public boolean isAnonymous() {
        return customerCpf == null;
    }

    public int itemCount() {
        return items.size();
    }

    public double calculateSubtotal() {
        return items.stream().mapToDouble(OrderItem::totalPrice).sum();
    }

    public boolean hasCoupon() {
        return couponId != null;
    }
}
