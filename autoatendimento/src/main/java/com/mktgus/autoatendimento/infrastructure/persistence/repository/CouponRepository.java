package com.mktgus.autoatendimento.infrastructure.persistence.repository;

import com.mktgus.autoatendimento.infrastructure.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
