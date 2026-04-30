package com.mktgus.autoatendimento.domain.model;

public record Coupon(
        Long id,
        String name,
        String description,
        double discountValue,
        boolean percentageDiscount,
        int cost,
        Long marketId,
        Double minimumPurchase,
        Double maximumDiscount
) {
}
