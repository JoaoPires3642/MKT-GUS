package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.application.response.OrderHistoryResponse;
import com.mktgus.autoatendimento.application.response.OrderItemResponse;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCustomerOrderHistoryUseCase {

    private final OrderGateway orderGateway;

    public ListCustomerOrderHistoryUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public List<OrderHistoryResponse> execute(String rawCpf) {
        Long cpf = parseCpf(rawCpf);
        return orderGateway.findByCustomerCpf(cpf).stream()
                .map(this::toResponse)
                .toList();
    }

    private Long parseCpf(String rawCpf) {
        if (rawCpf == null || rawCpf.isBlank()) {
            throw new ValidationException("CPF obrigatorio para consultar historico.");
        }

        String digits = rawCpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            throw new ValidationException("CPF invalido.");
        }

        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException exception) {
            throw new ValidationException("CPF invalido.");
        }
    }

    private OrderHistoryResponse toResponse(Order order) {
        return new OrderHistoryResponse(
                order.id(),
                order.marketId(),
                maskCpf(order.customerCpf()),
                order.couponId(),
                order.orderedAt(),
                order.totalAmount(),
                order.itemCount(),
                order.items().stream().map(this::toItemResponse).toList()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.ean(),
                item.productName(),
                item.unitPrice(),
                item.quantity(),
                item.adultOnly(),
                item.totalPrice()
        );
    }

    private String maskCpf(Long cpf) {
        if (cpf == null) {
            return null;
        }
        String value = String.format("%011d", cpf);
        return "***." + value.substring(3, 6) + "." + value.substring(6, 9) + "-**";
    }
}
