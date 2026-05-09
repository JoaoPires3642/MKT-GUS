package com.mktgus.autoatendimento.application.payment;

import com.mktgus.autoatendimento.domain.model.PaymentTransaction;

public record PaymentStatusOutput(PaymentTransaction transaction) {
}
