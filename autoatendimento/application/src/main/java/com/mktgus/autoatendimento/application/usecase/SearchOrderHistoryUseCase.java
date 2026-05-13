package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.application.order.OrderHistoryFilter;
import com.mktgus.autoatendimento.application.response.OrderHistoryResponse;
import com.mktgus.autoatendimento.application.response.OrderItemResponse;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.domain.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchOrderHistoryUseCase {

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;

    private final OrderGateway orderGateway;

    public SearchOrderHistoryUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public List<OrderHistoryResponse> execute(OrderHistoryFilter filter) {
        validatePeriod(filter);
        int limit = normalizeLimit(filter.limit());

        return orderGateway.search(filter.marketId(), filter.from(), filter.to(), limit).stream()
                .map(this::toResponse)
                .toList();
    }

    private void validatePeriod(OrderHistoryFilter filter) {
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to())) {
            throw new ValidationException("Data inicial nao pode ser maior que a data final.");
        }
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit <= 0) {
            throw new ValidationException("Limite deve ser positivo.");
        }
        return Math.min(limit, MAX_LIMIT);
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
