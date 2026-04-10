package com.mktgus.autoatendimento.domain.model;

public record Product(String ean, String name, String imageUrl, double price, boolean adultOnly) {
}
