package com.mktgus.autoatendimento.application.request;

import com.mktgus.autoatendimento.domain.model.PaymentMethod;

public record StartPaymentRequest(PaymentMethod method, double amount) {
}
