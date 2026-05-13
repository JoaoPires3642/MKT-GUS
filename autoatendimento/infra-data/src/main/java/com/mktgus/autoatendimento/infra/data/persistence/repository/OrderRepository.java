package com.mktgus.autoatendimento.infra.data.persistence.repository;

import com.mktgus.autoatendimento.infra.data.persistence.entity.OrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByClienteCpfOrderByDataHoraDesc(Long cpf);

    @Query("""
            select orderEntity from OrderEntity orderEntity
            where (:marketId is null or orderEntity.marketId = :marketId)
              and (:from is null or orderEntity.dataHora >= :from)
              and (:to is null or orderEntity.dataHora <= :to)
            order by orderEntity.dataHora desc
            """)
    List<OrderEntity> search(
            @Param("marketId") Long marketId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
