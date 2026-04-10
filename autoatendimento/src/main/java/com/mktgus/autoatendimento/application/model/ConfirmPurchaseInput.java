package com.mktgus.autoatendimento.application.model;

import java.util.List;

public record ConfirmPurchaseInput(String customerCpf, List<Item> items, Coupon coupon) {
    public record Item(String ean, int quantity, double unitPrice) {
    }

    public record Coupon(Long id, String discountType) {
    }
}
