package com.mktgus.autoatendimento.interfaces.api.request;

import java.util.List;

public record OrderRequest(
        String customerCpf,
        List<OrderItemRequest> items,
        CouponRequest coupon
) {
    public record OrderItemRequest(
            String ean,
            int quantity,
            double unitPrice
    ) {}

    public record CouponRequest(
            Long id,
            double discount,
            String discountType
    ) {}
}
