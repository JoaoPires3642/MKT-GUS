package com.mktgus.autoatendimento.application.model;

import com.mktgus.autoatendimento.domain.model.Order;

public record ConfirmPurchaseOutput(Order order, Integer updatedPointsBalance) {
}
