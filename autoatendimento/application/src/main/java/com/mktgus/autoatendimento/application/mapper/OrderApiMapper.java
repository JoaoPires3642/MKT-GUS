package com.mktgus.autoatendimento.application.mapper;

import com.mktgus.autoatendimento.application.purchase.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.application.purchase.ConfirmPurchaseOutput;
import com.mktgus.autoatendimento.application.request.OrderRequest;
import com.mktgus.autoatendimento.application.response.OrderItemResponse;
import com.mktgus.autoatendimento.application.response.OrderResponse;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderApiMapper {
    ConfirmPurchaseInput toInput(OrderRequest request);

    ConfirmPurchaseInput.Item toInputItem(OrderRequest.OrderItemRequest item);

    ConfirmPurchaseInput.Coupon toInputCoupon(OrderRequest.CouponRequest coupon);

    @Mapping(target = "customerCpf", expression = "java(maskCpf(output.order().customerCpf()))")
    @Mapping(target = "couponId", source = "order.couponId")
    @Mapping(target = "orderedAt", source = "order.orderedAt")
    @Mapping(target = "totalAmount", source = "order.totalAmount")
    @Mapping(target = "items", source = "order.items")
    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "updatedPointsBalance", source = "updatedPointsBalance")
    OrderResponse toResponse(ConfirmPurchaseOutput output);

    default String maskCpf(Long cpf) {
        if (cpf == null) return null;
        String s = String.format("%011d", cpf);
        return "***." + s.substring(3, 6) + "." + s.substring(6, 9) + "-**";
    }

    OrderItemResponse toItemResponse(OrderItem item);
}2