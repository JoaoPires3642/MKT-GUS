package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.application.payment.PaymentProviderResult;
import com.mktgus.autoatendimento.domain.model.PaymentMethod;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;

public interface PaymentGateway {
    PaymentProviderResult start(PaymentMethod method, double amount);
    PaymentProviderResult refresh(PaymentTransaction transaction);
    PaymentProviderResult confirm(PaymentTransaction transaction);
}
