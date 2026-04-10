package com.mktgus.autoatendimento.infrastructure.persistence.repository;

import com.mktgus.autoatendimento.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
