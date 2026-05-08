package com.mktgus.autoatendimento.application.payment;

import com.mktgus.autoatendimento.domain.model.PaymentMethod;

public record StartPaymentInput(PaymentMethod method, double amount) {
}
