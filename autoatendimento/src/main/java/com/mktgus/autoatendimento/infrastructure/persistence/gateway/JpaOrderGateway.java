package com.mktgus.autoatendimento.infrastructure.persistence.gateway;

import com.mktgus.autoatendimento.domain.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.infrastructure.persistence.mapper.OrderEntityMapper;
import com.mktgus.autoatendimento.infrastructure.persistence.repository.OrderItemRepository;
import com.mktgus.autoatendimento.infrastructure.persistence.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaOrderGateway implements OrderGateway {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public JpaOrderGateway(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Order save(Order order) {
        var savedOrder = orderRepository.save(OrderEntityMapper.toEntity(order));
        var savedItems = orderItemRepository.saveAll(OrderEntityMapper.toItemEntities(order, savedOrder));
        return OrderEntityMapper.toDomain(savedOrder, savedItems);
    }
}
