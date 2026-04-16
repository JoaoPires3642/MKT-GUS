package com.mktgus.autoatendimento.domain.model;

public record Coupon(
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
