package com.mktgus.autoatendimento.application.gateway;

import com.mktgus.autoatendimento.domain.model.Order;

public interface OrderGateway {
    Order save(Order order);
}
