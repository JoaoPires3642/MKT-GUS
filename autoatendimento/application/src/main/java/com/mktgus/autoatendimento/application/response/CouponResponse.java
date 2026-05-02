package com.mktgus.autoatendimento.application.response;

public record CouponResponse(
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
