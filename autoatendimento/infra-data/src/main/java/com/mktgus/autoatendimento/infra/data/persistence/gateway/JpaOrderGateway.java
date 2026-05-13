package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.OrderGateway;
import com.mktgus.autoatendimento.domain.model.Order;
import com.mktgus.autoatendimento.infra.data.persistence.entity.OrderEntity;
import com.mktgus.autoatendimento.infra.data.persistence.mapper.OrderEntityMapper;
import com.mktgus.autoatendimento.infra.data.persistence.repository.OrderItemRepository;
import com.mktgus.autoatendimento.infra.data.persistence.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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
                .map(this::toDomainWithItems);
    }

    @Override
    public List<Order> findByCustomerCpf(Long customerCpf) {
        return orderRepository.findByClienteCpfOrderByDataHoraDesc(customerCpf).stream()
                .map(this::toDomainWithItems)
                .toList();
    }

    @Override
    public List<Order> search(Long marketId, LocalDateTime from, LocalDateTime to, int limit) {
        return orderRepository.search(marketId, from, to, PageRequest.of(0, limit)).stream()
                .map(this::toDomainWithItems)
                .toList();
    }

    private Order toDomainWithItems(OrderEntity orderEntity) {
        var items = orderItemRepository.findByPedidoId(orderEntity.getId());
        return OrderEntityMapper.toDomain(orderEntity, items);
    }
}
