package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.infra.data.persistence.mapper.OrderEntityMapper;
import com.mktgus.autoatendimento.infra.data.persistence.repository.OrderItemRepository;
import com.mktgus.autoatendimento.infra.data.persistence.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaOrderGateway implements OrderGateway {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public JpaOrderGateway(OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Order save(Order order) {
        var savedOrder = orderRepository.save(OrderEntityMapper.toEntity(order));
        var savedItems = orderItemRepository.saveAll(
                OrderEntityMapper.toItemEntities(order, savedOrder)
        );
        return OrderEntityMapper.toDomain(savedOrder, savedItems);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id)
                .map(orderEntity -> {
                    var items = orderItemRepository.findByPedidoId(orderEntity.getId());
                    return OrderEntityMapper.toDomain(orderEntity, items);
                });
    }
}