package com.mktgus.autoatendimento.infrastructure.persistence.repository;

import com.mktgus.autoatendimento.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

}
