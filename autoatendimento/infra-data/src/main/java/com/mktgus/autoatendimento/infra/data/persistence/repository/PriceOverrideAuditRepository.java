package com.mktgus.autoatendimento.infra.data.persistence.repository;

import com.mktgus.autoatendimento.infra.data.persistence.entity.PriceOverrideAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceOverrideAuditRepository extends JpaRepository<PriceOverrideAuditEntity, Long> {
}
