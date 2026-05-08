package com.mktgus.autoatendimento.infra.data.persistence.repository;

import com.mktgus.autoatendimento.infra.data.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByPedidoId(Long pedidoId);

}