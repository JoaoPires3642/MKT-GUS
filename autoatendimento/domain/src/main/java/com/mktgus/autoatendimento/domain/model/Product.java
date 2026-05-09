package com.mktgus.autoatendimento.domain.model;

public record Product(String ean, String name, String imageUrl, double price, boolean adultOnly, String description) {
    public Product {
        if (ean == null || ean.isBlank()) throw new IllegalArgumentException("EAN inválido");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome do produto inválido");
        if (price < 0) throw new IllegalArgumentException("Preço não pode ser negativo");
    }

    public boolean requiresAgeVerification() {
        return adultOnly;
    }

    public boolean hasPriceDivergence(double requestedPrice) {
        return Math.abs(price - requestedPrice) > 0.01;
    }
}
