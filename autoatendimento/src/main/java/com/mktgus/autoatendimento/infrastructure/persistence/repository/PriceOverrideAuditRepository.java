package com.mktgus.autoatendimento.infrastructure.persistence.repository;

import com.mktgus.autoatendimento.infrastructure.persistence.entity.PriceOverrideAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceOverrideAuditRepository extends JpaRepository<PriceOverrideAuditEntity, Long> {
}
