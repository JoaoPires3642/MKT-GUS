package com.mktgus.autoatendimento.interfaces.api.mapper;

import com.mktgus.autoatendimento.application.model.ConfirmPurchaseInput;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import com.mktgus.autoatendimento.interfaces.api.request.OrderRequest;
import com.mktgus.autoatendimento.interfaces.api.response.OrderItemResponse;
import com.mktgus.autoatendimento.interfaces.api.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderApiMapper {
    ConfirmPurchaseInput toInput(OrderRequest request);

    ConfirmPurchaseInput.Item toInputItem(OrderRequest.OrderItemRequest item);

    ConfirmPurchaseInput.Coupon toInputCoupon(OrderRequest.CouponRequest coupon);

    @Mapping(target = "customerCpf", source = "customerCpf")
    @Mapping(target = "couponId", source = "couponId")
    @Mapping(target = "orderedAt", source = "orderedAt")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "items", source = "items")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "adultOnly", source = "adultOnly")
    @Mapping(target = "totalPrice", source = "totalPrice")
    OrderItemResponse toItemResponse(OrderItem item);
}
