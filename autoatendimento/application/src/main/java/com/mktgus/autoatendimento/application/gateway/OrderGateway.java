package com.mktgus.autoatendimento.application.gateway;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.mktgus.autoatendimento.domain.model.Order;

public interface OrderGateway {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByCustomerCpf(Long customerCpf);
    List<Order> search(Long marketId, LocalDateTime from, LocalDateTime to, int limit);
}
