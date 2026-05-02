package com.mktgus.autoatendimento.application.purchase;

import com.mktgus.autoatendimento.domain.model.Order;

public record ConfirmPurchaseOutput(Order order, Integer updatedPointsBalance) {
}
