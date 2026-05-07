package com.mktgus.autoatendimento.application.gateway;

import java.util.Optional;

import com.mktgus.autoatendimento.domain.model.Order;

public interface OrderGateway {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
