package com.mktgus.autoatendimento.interfaces.api.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record OrderRequest(
        @JsonAlias("clienteCpf")
        String customerCpf,
        @JsonAlias("itens")
        List<OrderItemRequest> items,
        @JsonAlias("cupom")
        CouponRequest coupon
) {
    public record OrderItemRequest(
            @JsonAlias("codigoEan")
            String ean,
            @JsonAlias("quantidade")
            int quantity,
            @JsonAlias("valorUnitario")
            double unitPrice,
            @JsonAlias("ajustePreco")
            PriceOverrideRequest priceOverride
    ) {}

    public record PriceOverrideRequest(
            @JsonAlias({"matriculaFuncionario", "employeeRegistration"})
            String employeeRegistration,
            @JsonAlias({"valorAutorizado", "authorizedUnitPrice"})
            double authorizedUnitPrice,
            @JsonAlias("motivo")
            String reason
    ) {}

    public record CouponRequest(
            Long id,
            @JsonAlias("desconto")
            double discount,
            @JsonAlias("tipoDesconto")
            String discountType
    ) {}
}
