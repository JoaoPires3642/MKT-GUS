package com.mktgus.autoatendimento.application.mapper;

import com.mktgus.autoatendimento.application.payment.PaymentStatusOutput;
import com.mktgus.autoatendimento.application.payment.StartPaymentInput;
import com.mktgus.autoatendimento.application.payment.StartPaymentOutput;
import com.mktgus.autoatendimento.application.request.StartPaymentRequest;
import com.mktgus.autoatendimento.application.response.PaymentResponse;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentApiMapper {
    StartPaymentInput toInput(StartPaymentRequest request);

    default PaymentResponse toResponse(StartPaymentOutput output) {
        return toResponse(output.transaction());
    }

    default PaymentResponse toResponse(PaymentStatusOutput output) {
        return toResponse(output.transaction());
    }

    PaymentResponse toResponse(PaymentTransaction transaction);
}
