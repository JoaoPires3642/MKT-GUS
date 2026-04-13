package com.mktgus.autoatendimento.application.model;

import java.util.List;

public record ConfirmPurchaseInput(String customerCpf, List<Item> items, Coupon coupon) {
    public record Item(String ean, int quantity, double unitPrice, PriceOverride priceOverride) {
    }

    public record PriceOverride(String employeeRegistration, double authorizedUnitPrice, String reason) {
    }

    public record Coupon(Long id, String discountType) {
    }
}
