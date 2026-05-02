package com.mktgus.autoatendimento.domain.model;

public record OrderItem(
        String ean,
        String productName,
        double unitPrice,
        int quantity,
        boolean adultOnly,
        double totalPrice
) {
    public static OrderItem of(String ean, String productName, double unitPrice, int quantity, boolean adultOnly) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva para o item: " + ean);
        if (unitPrice < 0) throw new IllegalArgumentException("Preço unitário não pode ser negativo");
        return new OrderItem(ean, productName, unitPrice, quantity, adultOnly, unitPrice * quantity);
    }

    public boolean requiresAgeVerification() {
        return adultOnly;
    }
}
