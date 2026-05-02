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
    public boolean isEligibleForPurchase(double purchaseTotal) {
        return minimumPurchase == null || purchaseTotal >= minimumPurchase;
    }

    public double calculateDiscount(double subtotal) {
        if (percentageDiscount) {
            double raw = subtotal * (discountValue / 100.0);
            return maximumDiscount != null ? Math.min(raw, maximumDiscount) : raw;
        }
        return discountValue;
    }

    public double applyTo(double subtotal) {
        return Math.max(0.0, subtotal - calculateDiscount(subtotal));
    }
}
