package com.mktgus.autoatendimento.application.purchase;

import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.TaxDocument;

public record ConfirmPurchaseOutput(Order order, Integer updatedPointsBalance, TaxDocument taxDocument) {}
