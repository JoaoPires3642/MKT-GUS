package com.mktgus.autoatendimento.interfaces.api.response;

public record CouponResponse(
        Long id,
        String name,
        String description,
        double discountValue,
        boolean percentageDiscount,
        int cost,
        Double minimumPurchase,
        Double maximumDiscount
) {
}
